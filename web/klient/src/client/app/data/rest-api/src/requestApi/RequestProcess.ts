import EventType from './eventType';
import asyncPollingStatus from './asyncPollingStatus';
import { HttpClientApi } from '../HttpClientApiTsType';
import { Response, SuccessResponse } from './ResponseTsType';
import { RequestAdditionalConfig } from '../RequestAdditionalConfigTsType';
import TimeoutError from './error/TimeoutError';
import RequestErrorEventHandler from './error/RequestErrorEventHandler';

const HTTP_ACCEPTED = 202;
const MAX_POLLING_ATTEMPTS = 150;
const CANCELLED = 'INTERNAL_CANCELLATION';

const wait = ms => new Promise(resolve => setTimeout(resolve, ms));

const getRestMethod = (httpClientApi: HttpClientApi, restMethodString: string) => {
  switch (restMethodString) {
    case 'POST':
      return httpClientApi.post;
    case 'PUT':
      return httpClientApi.put;
    default:
      return httpClientApi.get;
  }
};

const hasLocationAndStatusDelayedOrHalted = responseData => responseData.location && (responseData.status === asyncPollingStatus.DELAYED
  || responseData.status === asyncPollingStatus.HALTED);

type Notify = (eventType: keyof typeof EventType, data?: any) => void
type NotificationEmitter = (eventType: keyof typeof EventType, data?: any) => void

interface ResponseDataLink {
  href: string;
  type: string;
  requestPayload: any;
  rel: string;
}

/**
 * RequestProcess
 *
 * Denne klassen utfører et spesifikt kall mot en URL. Tilbyr automatisk henting av data
 * fra "links" i kall-responsen. Håndterer også "long-polling".
 *
 * En starter prosess med run og avbryter med cancel.
 */
class RequestProcess {
  httpClientApi: HttpClientApi;

  restMethod: (url: string, params: any, responseType?: string) => Promise<Response>;

  path: string;

  config: RequestAdditionalConfig;

  maxPollingLimit: number = MAX_POLLING_ATTEMPTS;

  notify: Notify = () => undefined;

  isCancelled: boolean = false;

  constructor(httpClientApi: HttpClientApi, restMethod: (url: string, params: any, responseType?: string) => Promise<Response>,
    path: string, config: RequestAdditionalConfig) {
    this.httpClientApi = httpClientApi;
    this.restMethod = restMethod;
    this.path = path;
    this.config = config;
    this.maxPollingLimit = config.maxPollingLimit || this.maxPollingLimit;
  }

  setNotificationEmitter = (notificationEmitter: NotificationEmitter) => {
    this.notify = notificationEmitter;
  }

  execLongPolling = async (location: string, pollingInterval: number = 0, pollingCounter: number = 0): Promise<Response> => {
    if (pollingCounter === this.maxPollingLimit) {
      throw new TimeoutError(location);
    }

    await wait(pollingInterval);

    if (this.isCancelled) {
      return null;
    }

    this.notify(EventType.STATUS_REQUEST_STARTED);
    const statusOrResultResponse = await this.httpClientApi.get(location);
    this.notify(EventType.STATUS_REQUEST_FINISHED);

    if (!('data' in statusOrResultResponse)) {
      return statusOrResultResponse;
    }
    const responseData = statusOrResultResponse.data;
    if (responseData && responseData.status === asyncPollingStatus.PENDING) {
      const { pollIntervalMillis, message } = responseData;
      this.notify(EventType.UPDATE_POLLING_MESSAGE, message);
      return this.execLongPolling(location, pollIntervalMillis, pollingCounter + 1);
    }

    return statusOrResultResponse;
  };

  execLinkRequests = async (responseData: {links: ResponseDataLink[]}) => {
    const requestList = responseData.links
      .map(link => () => this.execute(// eslint-disable-line no-use-before-define
        link.href, getRestMethod(this.httpClientApi, link.type), link.requestPayload,
      )
        .then((response: SuccessResponse) => Promise.resolve({ [link.rel]: response.data })));

    // TODO (TOR) Må kunna konfigurera om ein skal feila om eitt av kalla feilar. Og kva med logging?
    return Promise.all([Promise.resolve(responseData), ...requestList.map(request => request())])
      .then(allResponses => (this.config.addLinkDataToArray
        ? allResponses.reduce((acc, rData) => (rData.links ? acc : acc.concat(Object.values(rData)[0])), [])
        : allResponses.reduce((acc, rData) => ({ ...acc, ...rData }), {})));
  }

  execute = async (path: string, restMethod: (path: string, params?: any) => Promise<Response>, params: any): Promise<Response> => {
    let response = await restMethod(path, params);
    if ('status' in response && response.status === HTTP_ACCEPTED) {
      try {
        response = await this.execLongPolling(response.headers.location);
      } catch (error) {
        const responseData = error.response ? error.response.data : undefined;
        if (responseData && hasLocationAndStatusDelayedOrHalted(responseData)) {
          response = await this.httpClientApi.get(responseData.location);
          if ('data' in response) {
            this.notify(EventType.POLLING_HALTED_OR_DELAYED, response.data.taskStatus);
          }
        } else {
          throw error;
        }
      }
    }
    const responseData = response && 'data' in response && response.data;
    if (this.config.fetchLinkDataAutomatically && responseData && responseData.links && responseData.links.length > 0) {
      // TODO (TOR) execLinkRequests returnerar no responseData.
      response = await this.execLinkRequests(responseData);
    }
    return response;
  }

  cancel = () => {
    this.isCancelled = true;
  }

  run = async (params: any): Promise<{payload: any}> => {
    this.notify(EventType.REQUEST_STARTED);

    try {
      const response = await this.execute(this.path, this.restMethod, params);
      if (this.isCancelled) {
        return { payload: CANCELLED };
      }

      // TODO (TOR) Bør unngå denne testen ved å sikre at ein alltid får det same tilbake fra execute
      const responseData = 'data' in response && response.data !== undefined ? response.data : response;

      this.notify(EventType.REQUEST_FINISHED, responseData);
      return responseData ? { payload: responseData } : { payload: [] };
    } catch (error) {
      new RequestErrorEventHandler(this.notify).handleError(error);
      throw error;
    }
  }
}

export default RequestProcess;
