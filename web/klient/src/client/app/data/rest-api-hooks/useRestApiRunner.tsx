import { useState } from 'react';

import { createRequestApi, RequestRunner } from 'data/rest-api-new';
import { endpoints, RestApiPathsKeys } from 'data/restApiPaths';

const contextPath = 'fplos';
const requestApi = createRequestApi(contextPath, endpoints);

export enum ApiState {
  NOT_STARTED = 'NOT_STARTED',
  LOADING = 'LOADING',
  SUCCESS = 'SUCCESS',
  ERROR = 'ERROR',
}

interface SearchResultAccessDenied {
  feilmelding?: string;
  type?: string;
}

interface RestApiData<T> {
  startRequest: (params?: any) => Promise<T>;
  resetRequestData: () => void;
  state: ApiState;
  error?: SearchResultAccessDenied;
  data?: T;
  requestApi: RequestRunner;
}

function useRestApiRunner<T>(key: RestApiPathsKeys):RestApiData<T> {
  const [data, setData] = useState({
    state: ApiState.LOADING,
    error: undefined,
    data: undefined,
  });

  const setPartData = (partialData) => setData({ ...data, ...partialData });

  const startRequest = function doCall(params: any = {}):Promise<T> {
    setPartData({
      state: ApiState.LOADING,
    });

    return requestApi.getRequestRunner(key).startProcess(params)
      .then((dataRes) => {
        setPartData({
          state: ApiState.SUCCESS,
          data: dataRes.payload,
        });
        return Promise.resolve(dataRes.payload);
      })
      .catch((error) => {
        setPartData({
          state: ApiState.ERROR,
          error,
        });
        return undefined;
      });
  };

  const resetRequestData = () => {
    setPartData({
      state: ApiState.NOT_STARTED,
      error: undefined,
      data: undefined,
    });
  };

  return {
    startRequest,
    resetRequestData,
    requestApi: requestApi.getRequestRunner(key),
    ...data,
  };
}

export default useRestApiRunner;
