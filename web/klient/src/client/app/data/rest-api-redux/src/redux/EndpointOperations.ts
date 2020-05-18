import { createSelector } from 'reselect';

import { RequestConfig } from '../../../rest-api';
import { State } from './createRequestReducer';

import ReduxApiCreator from './ReduxApiCreator';

class EndpointOperations {
  reduxApiCreator: ReduxApiCreator;

  contextPath: string;

  name: string;

  path: string;

  constructor(reduxApiCreator: ReduxApiCreator, contextPath: string, config: RequestConfig) {
    this.reduxApiCreator = reduxApiCreator;
    this.contextPath = contextPath;
    this.name = config.name;
    this.path = config.path && contextPath ? `/${contextPath}${config.path}` : config.path;
  }

  makeRestApiRequest = () => this.reduxApiCreator.makeRequestActionCreator(this.name);

  isEndpointEnabled = () => this.reduxApiCreator.isEndpointEnabled(this.name);

  cancelRestApiRequest = () => this.reduxApiCreator.cancelRequest(this.name);

  resetRestApi = () => this.reduxApiCreator.makeResetActionCreator(this.name);

  getRestApiState = (): any => this.reduxApiCreator.getEndpointState(this.name);

  getRestApiData = (): any => createSelector([this.getRestApiState()], (apiState: State) => apiState.data);

  getRestApiMeta = (): any => createSelector([this.getRestApiState()], (apiState: State) => apiState.meta);

  getRestApiCacheParams = (): any => createSelector([this.getRestApiState()], (apiState: State) => apiState.cacheParams);

  getRestApiPreviousData = (): any => createSelector([this.getRestApiState()], (apiState: State) => apiState.previousData);

  getRestApiError = (): any => createSelector([this.getRestApiState()], (apiState: State) => apiState.error);

  getRestApiStarted = (): any => createSelector([this.getRestApiState()], (apiState: State) => apiState.started);

  getRestApiFinished = (): any => createSelector([this.getRestApiState()], (apiState: State) => apiState.finished);

  getRestApiPollingTimeout = (): any => createSelector([this.getRestApiState()], (apiState: State) => apiState.pollingTimeout);
}

export default EndpointOperations;
