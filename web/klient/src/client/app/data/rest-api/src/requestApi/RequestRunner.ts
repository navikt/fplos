import RequestProcess from './RequestProcess';
import NotificationMapper from './NotificationMapper';
import RestApiRequestContext from './RestApiRequestContext';
import { RequestType } from '../RequestConfig';
import HttpClientApi from '../HttpClientApiTsType';

const getMethod = (httpClientApi: HttpClientApi, restMethod: string) => {
  if (restMethod === RequestType.GET) {
    return httpClientApi.get;
  }
  if (restMethod === RequestType.GET_ASYNC) {
    return httpClientApi.getAsync;
  }
  if (restMethod === RequestType.POST) {
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
  return httpClientApi.postAndOpenBlob;
};

/**
 * RequestRunner
 *
 * Denne klassen håndterer kall mot en spesifikk URL.
 *
 * Ved start av nytt kall blir eventuelt pågåande kall stoppet først (kan skje ved long-polling). En kan eventuelt
 * stoppe et kall manuelt ved å bruke metoden @see stopProcess.
 */
class RequestRunner {
  httpClientApi: HttpClientApi;

  context: RestApiRequestContext

  process: RequestProcess;

  constructor(httpClientApi: HttpClientApi, context: RestApiRequestContext) {
    this.httpClientApi = httpClientApi;
    this.context = context;
  }

  private getConfig = () => this.context.getConfig();

  private getRestMethod = () => getMethod(this.httpClientApi, this.getConfig().restMethod)

  private getPath = (): string => {
    const contextPath = this.getConfig().contextPath ? `/${this.getConfig().contextPath}` : '';
    return this.getConfig().path ? `${this.context.getHostname()}${contextPath}${this.getConfig().path}` : undefined;
  }

  public cancelRequest = () => {
    if (this.process) {
      this.process.cancel();
    }
  }

  public startProcess = (params: any, notificationMapper?: NotificationMapper) => {
    this.cancelRequest();

    this.process = new RequestProcess(this.httpClientApi, this.getRestMethod(), this.getPath(), this.getConfig().config);
    if (notificationMapper) {
      this.process.setNotificationEmitter(notificationMapper.getNotificationEmitter());
    }

    return this.process.run(params || this.getConfig().requestPayload);
  }
}

export default RequestRunner;
