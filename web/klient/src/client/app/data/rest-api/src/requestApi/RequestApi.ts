import HttpClientApi from '../HttpClientApiTsType';
import NotificationMapper from './NotificationMapper';
import Link from './LinkTsType';
import AbstractRequestApi from './AbstractRequestApi';
import RequestRunner from './RequestRunner';
import RequestConfig, { RequestType } from '../RequestConfig';

const DEFAULT_CATEGORY = 'DEFAULT_CATEGORY';

const getMethod = (httpClientApi: HttpClientApi, restMethod: string, isResponseBlob?: boolean) => {
  if (restMethod === RequestType.GET) {
    return httpClientApi.get;
  }
  if (restMethod === RequestType.GET_ASYNC) {
    return httpClientApi.getAsync;
  }
  if (restMethod === RequestType.POST && !isResponseBlob) {
    return httpClientApi.post;
  }
  if (restMethod === RequestType.POST_ASYNC) {
    return httpClientApi.postAsync;
  }
  if (restMethod === RequestType.PUT) {
    return httpClientApi.put;
  }
  if (restMethod === RequestType.PUT_ASYNC) {
    return httpClientApi.putAsync;
  }
  return httpClientApi.postBlob;
};

const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

/**
 * RequestApi
 *
 * Denne klassen opprettes med en referanse til et HttpClientApi (for eksempel Axios), context-path og konfig for
 * de enkelte endepunktene. Det blir så satt opp RequestRunner's for endepunktene. Desse kan hentes via metoden @see getRequestRunner.
 */
class RequestApi extends AbstractRequestApi {
  httpClientApi: HttpClientApi;

  endpointConfigList: RequestConfig[];

  links: {[key: string]: Link[]} = {};

  notificationMapper: NotificationMapper = new NotificationMapper();

  activeRunners: Record<string, RequestRunner> = {};

  constructor(httpClientApi: HttpClientApi, endpointConfigList: RequestConfig[]) {
    super();
    this.httpClientApi = httpClientApi;
    this.endpointConfigList = endpointConfigList;
  }

  private findLinks = (rel: string | undefined): Link | undefined => Object.values(this.links).flat().find((link) => link.rel === rel);

  private cancelRequest = (endpointName: string): boolean => {
    if (this.activeRunners[endpointName]) {
      this.activeRunners[endpointName].cancel();
      return true;
    }
    return false;
  }

  public startRequest = async <T, P>(endpointName: string, params?: P): Promise<{ payload: T }> => {
    const endpointConfig = this.endpointConfigList.find((c) => c.name === endpointName);
    if (!endpointConfig) {
      throw new Error(`Mangler konfig for endepunkt ${endpointName}`);
    }
    const link = this.findLinks(endpointConfig.rel);
    const restMethod = link ? link.type : endpointConfig.restMethod;
    if (!restMethod) {
      throw new Error(`Mangler restMethod for endepunkt ${endpointName}`);
    }
    const href = link ? link.href : endpointConfig.path;
    if (!href) {
      throw new Error(`Mangler href for endepunkt ${endpointName}`);
    }

    const apiRestMethod = getMethod(this.httpClientApi, restMethod, endpointConfig.config?.isResponseBlob);
    const runner = new RequestRunner(this.httpClientApi, apiRestMethod, href, endpointConfig.config);
    if (this.notificationMapper) {
      runner.setNotificationEmitter(this.notificationMapper.getNotificationEmitter());
    }

    const hasCancelled = this.cancelRequest(endpointName);
    if (hasCancelled) {
      // TODO (TOR) Fjern dette når ein har gått over til Server side events
      await wait(1500);
    }

    try {
      this.activeRunners = {
        ...this.activeRunners,
        [endpointName]: runner,
      };
      const result = await runner.start<T, P>(params || link?.requestPayload);
      delete this.activeRunners[endpointName];
      return result;
    } catch (error) {
      delete this.activeRunners[endpointName];
      throw error;
    }
  }

  public hasPath = (endpointName: string): boolean => {
    const endpointConfig = this.endpointConfigList.find((c) => c.name === endpointName);
    if (!endpointConfig) {
      throw new Error(`Mangler konfig for endepunkt ${endpointName}`);
    }
    const link = this.findLinks(endpointConfig.rel);
    return !!link?.href || !!endpointConfig?.path;
  };

  public setLinks = (links: Link[], linkCategory: string = DEFAULT_CATEGORY): void => {
    this.links = {
      ...this.links,
      [linkCategory]: links,
    };
  }

  public setRequestPendingHandler = (requestPendingHandler: (message?: string) => void): void => {
    this.notificationMapper.addUpdatePollingMessageEventHandler((data) => {
      requestPendingHandler(data);
    });
    this.notificationMapper.addRequestFinishedEventHandler(() => {
      requestPendingHandler();
    });
    this.notificationMapper.addRequestErrorEventHandlers(() => {
      requestPendingHandler();
    });
  }

  public setAddErrorMessageHandler = (addErrorMessage: (message: string) => void): void => {
    this.notificationMapper.addRequestErrorEventHandlers((errorData, type) => {
      addErrorMessage({ ...errorData, type });
    });
  };

  public resetCache = (): void => {
  }

  public isMock = (): boolean => false;

  // Kun for test
  public mock = () => { throw new Error('Not Implemented'); };

  // Kun for test
  public setMissingPath = () => { throw new Error('Not Implemented'); };

  // Kun for test
  public getRequestMockData = () => { throw new Error('Not Implemented'); };

  // Kun for test
  public clearAllMockData = () => { throw new Error('Not Implemented'); };
}

export default RequestApi;
