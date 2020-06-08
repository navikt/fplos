import { useState, useEffect, DependencyList } from 'react';

import { createRequestApi } from 'data/rest-api-new';
import { endpoints, RestApiPathsKeys } from 'data/restApiPaths';

import RestApiState from './RestApiState';

const contextPath = 'fplos';
const requestApi = createRequestApi(contextPath, endpoints);

interface RestApiData<T> {
  state: RestApiState;
  error?: string;
  data?: T;
}

function useRestApi<T>(key: RestApiPathsKeys, params: any = {}, dependencies: DependencyList = []):RestApiData<T> {
  const [data, setData] = useState({
    state: RestApiState.LOADING,
    error: undefined,
    data: undefined,
  });

  const setPartData = (partialData) => setData({ ...data, ...partialData });

  useEffect(() => {
    setPartData({
      state: RestApiState.LOADING,
    });

    requestApi.getRequestRunner(key).startProcess(params)
      .then((dataRes) => {
        setPartData({
          state: RestApiState.SUCCESS,
          data: dataRes.payload,
        });
      })
      .catch(() => {
        setPartData({
          state: RestApiState.ERROR,
          error: 'fetch failed',
        });
      });
  }, dependencies);

  return data;
}

export default useRestApi;
