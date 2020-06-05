import { useState, useEffect, useContext } from 'react';
import { RestApiPathsKeys } from 'data/restApiPaths';

import { RestDataContext } from './RestDataContext';


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

function useRestApi<T>(key: RestApiPathsKeys):RestApiData<T> {
  const [data, setData] = useState({
    state: ApiState.LOADING,
    error: undefined,
    data: undefined,
  });

  const context = useContext(RestDataContext);
  const { dispatch, requestApi } = context;

  const setPartData = (partialData) => setData({ ...data, ...partialData });

  useEffect(() => {
    setPartData({
      state: ApiState.LOADING,
    });
    requestApi.getRequestRunner(key).startProcess({})
      .then((dataRes) => {
        dispatch({ type: 'success', key, data: dataRes.payload });
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
