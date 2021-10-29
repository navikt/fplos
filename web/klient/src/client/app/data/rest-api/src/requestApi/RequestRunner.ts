import { ResponseType } from 'axios';

import EventType from './eventType';
import AsyncPollingStatus from './asyncPollingStatus';
import HttpClientApi from '../HttpClientApiTsType';
import { Response } from './ResponseTsType';
import RequestAdditionalConfig from '../RequestAdditionalConfigTsType';
import TimeoutError from './error/TimeoutError';
import RequestErrorEventHandler from './error/RequestErrorEventHandler';

const HTTP_ACCEPTED = 202;
const MAX_POLLING_ATTEMPTS = 150;
export const REQUEST_POLLING_CANCELLED = 'INTERNAL_CANCELLATION';

const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

const hasLocationAndStatusDelayedOrHalted = (
  responseData: { location: string, status: string },
): boolean => !!responseData.location && (responseData.status === AsyncPollingStatus.DELAYED
  || responseData.status === AsyncPollingStatus.HALTED);

type Notify = (eventType: keyof typeof EventType, data?: any, isPolling?: boolean) => void
type NotificationEmitter = (eventType: keyof typeof EventType, data?: any) => void

/**
 * RequestRunner
 *
 * Denne klassen utfører et spesifikt kall mot en URL. Håndterer også "long-polling".
 *
 * En starter prosess med run og avbryter med cancel.
 */
class RequestRunner {
  httpClientApi: HttpClientApi;

  restMethod: (url: string, params: any, responseType?: ResponseType) => Promise<Response>;

  path: string;

  config: RequestAdditionalConfig;

  maxPollingLimit: number = MAX_POLLING_ATTEMPTS;

  // eslint-disable-next-line class-methods-use-this
  notify: Notify = () => undefined;

  isCancelled = false;

  isPollingRequest = false;

  constructor(httpClientApi: HttpClientApi, restMethod: (url: string, params?: any, responseType?: ResponseType) => Promise<Response>,
    path: string, config: RequestAdditionalConfig) {
    this.httpClientApi = httpClientApi;
    this.restMethod = restMethod;
    this.path = path;
    this.config = config;
    this.maxPollingLimit = config.maxPollingLimit || this.maxPollingLimit;
  }

  setNotificationEmitter = (notificationEmitter: NotificationEmitter): void => {
    this.notify = notificationEmitter;
  };

  execLongPolling = async (location: string, pollingInterval = 0, pollingCounter = 0): Promise<Response | null> => {
    if (pollingCounter === this.maxPollingLimit) {
      throw new TimeoutError(location);
    }

    await wait(pollingInterval);

    if (this.isCancelled) {
      return Promise.resolve(null);
    }

    this.notify(EventType.STATUS_REQUEST_STARTED);
    const statusOrResultResponse = await this.httpClientApi.get(location);
    this.notify(EventType.STATUS_REQUEST_FINISHED);

    if (!('data' in statusOrResultResponse)) {
      return statusOrResultResponse;
    }
    const responseData = statusOrResultResponse.data;
    if (responseData && responseData.status === AsyncPollingStatus.PENDING) {
      const { pollIntervalMillis, message } = responseData;
      this.notify(EventType.UPDATE_POLLING_MESSAGE, message);
      return this.execLongPolling(location, pollIntervalMillis, pollingCounter + 1);
    }

    return statusOrResultResponse;
  };

  execute = async (path: string, restMethod: (pathArg: string, params?: any) => Promise<Response>, params: any): Promise<Response | null> => {
    let response = await restMethod(path, params);
    if ('status' in response && response.status === HTTP_ACCEPTED && response.headers.location) {
      this.isPollingRequest = true;
      try {
        return await this.execLongPolling(response.headers.location);
      } catch (error: any) {
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
    return response;
  };

  cancel = (): void => {
    this.isCancelled = true;
  };

  start = async <T, P>(params: P): Promise<{payload: T}> => {
    this.notify(EventType.REQUEST_STARTED);

    try {
      const response = await this.execute(this.path, this.restMethod, params);
      if (this.isCancelled) {
        throw new Error(REQUEST_POLLING_CANCELLED);
      }

      const responseData = response && 'data' in response ? response.data : undefined;
      this.notify(EventType.REQUEST_FINISHED, responseData, this.isPollingRequest);
      return responseData ? { payload: responseData } : { payload: undefined };
    } catch (error: any) {
      if (error?.message !== REQUEST_POLLING_CANCELLED) {
        new RequestErrorEventHandler(this.notify, this.isPollingRequest).handleError(error);
      }
      throw error;
    }
  };
}

export default RequestRunner;
