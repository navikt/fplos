import { useState, useEffect, useContext } from 'react';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import { RestApiGlobalDataContext } from './RestApiGlobalDataContext';
import RestApiState from '../src/RestApiState';

interface RestApiData<T> {
  state: RestApiState;
  error?: string;
  data?: T;
}

function useGlobalStateRestApi<T>(key: RestApiGlobalStatePathsKeys, params: any = {}):RestApiData<T> {
  const [data, setData] = useState({
    state: RestApiState.LOADING,
    error: undefined,
    data: undefined,
  });

  const context = useContext(RestApiGlobalDataContext);
  const { dispatch, requestApi } = context;

  useEffect(() => {
    setData({
      state: RestApiState.LOADING,
      error: undefined,
      data: undefined,
    });

    dispatch({ type: 'remove', key });

    requestApi.getRequestRunner(key).startProcess(params)
      .then((dataRes) => {
        dispatch({ type: 'success', key, data: dataRes.payload });
        setData({
          state: RestApiState.SUCCESS,
          data: dataRes.payload,
          error: undefined,
        });
      })
      .catch(() => {
        setData({
          state: RestApiState.ERROR,
          data: undefined,
          error: 'fetch failed',
        });
      });
  }, []);

  return data;
}

export default useGlobalStateRestApi;
