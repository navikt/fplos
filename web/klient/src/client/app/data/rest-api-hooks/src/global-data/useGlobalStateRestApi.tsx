import { useState, useEffect, useContext } from 'react';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import { NotificationMapper } from 'data/rest-api';
import useRestApiErrorDispatcher from '../error/useRestApiErrorDispatcher';
import { RestApiGlobalDataDispatchContext, RestApiContext } from './RestApiGlobalDataContext';
import RestApiState from '../RestApiState';

interface RestApiData<T> {
  state: RestApiState;
  error?: Error;
  data?: T;
}

/**
 * Hook som henter data fra backend (ved mount) og deretter lagrer i @see RestApiGlobalDataContext
 */
function useGlobalStateRestApi<T>(key: RestApiGlobalStatePathsKeys, params: any = {}):RestApiData<T> {
  const [data, setData] = useState({
    state: RestApiState.LOADING,
    error: undefined,
    data: undefined,
  });

  const { addErrorMessage } = useRestApiErrorDispatcher();
  const notif = new NotificationMapper();
  notif.addRequestErrorEventHandlers((errorData, type) => {
    addErrorMessage({ ...errorData, type });
  });

  const dispatch = useContext(RestApiGlobalDataDispatchContext);
  const requestApi = useContext(RestApiContext);

  useEffect(() => {
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
      .catch((error) => {
        setData({
          state: RestApiState.ERROR,
          data: undefined,
          error,
        });
      });
  }, []);

  return data;
}

export default useGlobalStateRestApi;
