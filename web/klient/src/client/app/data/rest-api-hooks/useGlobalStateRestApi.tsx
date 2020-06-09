import { useState, useEffect, useContext } from 'react';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import { RestApiGlobalDataContext } from './RestApiGlobalDataContext';
import RestApiState from './RestApiState';

interface RestApiData<T> {
  state: RestApiState;
  error?: string;
  data?: T;
}

function useGlobalStateRestApi<T>(key: RestApiGlobalStatePathsKeys, params: any = {}, options: any = { keepData: false }):RestApiData<T> {
  const [data, setData] = useState({
    state: RestApiState.LOADING,
    error: undefined,
    data: undefined,
  });

  const context = useContext(RestApiGlobalDataContext);
  const { dispatch, requestApi } = context;

  const setPartData = (partialData) => setData({ ...data, ...partialData });

  useEffect(() => {
    setPartData({
      state: RestApiState.LOADING,
    });

    if (!options.keepData) {
      dispatch({ type: 'remove', key });
    }

    requestApi.getRequestRunner(key).startProcess(params)
      .then((dataRes) => {
        dispatch({ type: 'success', key, data: dataRes.payload });
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
  }, []);

  return data;
}

export default useGlobalStateRestApi;
