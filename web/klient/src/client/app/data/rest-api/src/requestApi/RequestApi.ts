import RequestRunner from './RequestRunner';
import RestApiRequestContext from './RestApiRequestContext';
import HttpClientApi from '../HttpClientApiTsType';
import RequestConfig from '../RequestConfig';
import NotificationMapper from './NotificationMapper';

/**
 * RequestApi
 *
 * Denne klassen opprettes med en referanse til et HttpClientApi (for eksempel Axios), context-path og konfig for
 * de enkelte endepunktene. Det blir så satt opp RequestRunner's for endepunktene. Desse kan hentes via metoden @see getRequestRunner.
 */
class RequestApi {
  requestRunnersMappedByName: {[key: string]: RequestRunner};

  constructor(httpClientApi: HttpClientApi, configs: RequestConfig[]) {
    this.requestRunnersMappedByName = configs.reduce((acc, config) => ({
      ...acc,
      [config.name]: new RequestRunner(httpClientApi, new RestApiRequestContext(config)),
    }), {});
  }

  public startRequest = (endpointName: string, params: any, notificationMapper?: NotificationMapper) => this.requestRunnersMappedByName[endpointName]
    .startProcess(params, notificationMapper);

  public cancelRequest = (endpointName: string) => this.requestRunnersMappedByName[endpointName].cancelRequest();
}

export default RequestApi;
