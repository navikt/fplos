import { useState, useCallback } from 'react';

import { endpoints, RestApiPathsKeys } from 'data/restApiPaths';
import { createRequestApi, RequestRunner, NotificationMapper } from 'data/rest-api';
import useRestApiErrorDispatcher from '../error/useRestApiErrorDispatcher';
import RestApiState from '../RestApiState';

const requestApi = createRequestApi(endpoints);

interface RestApiData<T> {
  startRequest: (params?: any, keepData?: boolean) => Promise<T>;
  resetRequestData: () => void;
  state: RestApiState;
  error?: Error;
  data?: T;
  requestApi: RequestRunner;
}

/**
 * Hook som gir deg ein funksjon til å starte restkall, i tillegg til kallets status/resultat/feil
 */
function useRestApiRunner<T>(key: RestApiPathsKeys):RestApiData<T> {
  const [data, setData] = useState({
    state: RestApiState.NOT_STARTED,
    error: undefined,
    data: undefined,
  });

  const { addErrorMessage } = useRestApiErrorDispatcher();
  const notif = new NotificationMapper();
  notif.addRequestErrorEventHandlers((errorData, type) => {
    addErrorMessage({ ...errorData, type });
  });

  const startRequest = useCallback((params: any = {}, keepData = false):Promise<T> => {
    setData((oldState) => ({
      state: RestApiState.LOADING,
      error: undefined,
      data: keepData ? oldState.data : undefined,
    }));

    return requestApi.getRequestRunner(key).startProcess(params, notif)
      .then((dataRes) => {
        if (dataRes.payload === 'INTERNAL_CANCELLATION') {
          setData({
            state: RestApiState.NOT_STARTED,
            data: undefined,
            error: undefined,
          });
        } else {
          setData({
            state: RestApiState.SUCCESS,
            data: dataRes.payload,
            error: undefined,
          });
        }
        return Promise.resolve(dataRes.payload);
      })
      .catch((error) => {
        setData({
          state: RestApiState.ERROR,
          data: undefined,
          error,
        });
        throw error;
      });
  }, []);

  const resetRequestData = useCallback(() => {
    setData({
      state: RestApiState.NOT_STARTED,
      error: undefined,
      data: undefined,
    });
  }, []);

  return {
    startRequest,
    resetRequestData,
    requestApi: requestApi.getRequestRunner(key),
    ...data,
  };
}

export default useRestApiRunner;
