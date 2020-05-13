interface ActionTypes {
  requestStarted: () => string;
  requestFinished: () => string;
  requestError: () => string;
  reset: () => string;
  statusRequestStarted: () => string;
  statusRequestFinished?: () => string;
  updatePollingMessage: () => string;
  pollingTimeout: () => string;
}

export default ActionTypes;
