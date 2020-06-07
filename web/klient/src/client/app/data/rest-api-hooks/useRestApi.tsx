import { useState, useEffect } from 'react';

import { createRequestApi } from 'data/rest-api-new';
import { endpoints, RestApiPathsKeys } from 'data/restApiPaths';

const contextPath = 'fplos';
const requestApi = createRequestApi(contextPath, endpoints);

export enum ApiState {
  LOADING = 'LOADING',
  SUCCESS = 'SUCCESS',
  ERROR = 'ERROR',
}

interface RestApiData<T> {
  state: ApiState;
  error?: string;
  data?: T;
}

function useRestApi<T>(key: RestApiPathsKeys, params: any = {}):RestApiData<T> {
  const [data, setData] = useState({
    state: ApiState.LOADING,
    error: undefined,
    data: undefined,
  });

  const setPartData = (partialData) => setData({ ...data, ...partialData });

  useEffect(() => {
    setPartData({
      state: ApiState.LOADING,
    });

    requestApi.getRequestRunner(key).startProcess(params)
      .then((dataRes) => {
        setPartData({
          state: ApiState.SUCCESS,
          data: dataRes.payload,
        });
      })
      .catch(() => {
        setPartData({
          state: ApiState.ERROR,
          error: 'fetch failed',
        });
      });
  }, []);

  return data;
}

export default useRestApi;
