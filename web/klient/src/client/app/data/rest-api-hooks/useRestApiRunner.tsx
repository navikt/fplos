import { useState, useEffect } from 'react';

import { endpoints, RestApiPathsKeys } from 'data/restApiPaths';
import useRestApiErrorDispatcher from 'data/rest-api-hooks/useRestApiErrorDispatcher';
import { createRequestApi, RequestRunner, NotificationMapper } from 'data/rest-api';
import RestApiState from './RestApiState';

const contextPath = 'fplos';
const requestApi = createRequestApi(contextPath, endpoints);

interface SearchResultAccessDenied {
  feilmelding?: string;
  type?: string;
}

interface RestApiData<T> {
  startRequest: (params?: any, keepData?: boolean) => Promise<T>;
  resetRequestData: () => void;
  state: RestApiState;
  error?: SearchResultAccessDenied;
  data?: T;
  requestApi: RequestRunner;
}

function useRestApiRunner<T>(key: RestApiPathsKeys):RestApiData<T> {
  const [data, setData] = useState({
    state: RestApiState.NOT_STARTED,
    error: undefined,
    data: undefined,
  });

  const dispatch = useRestApiErrorDispatcher();

  const startRequest = function doCall(params: any = {}, keepData = false):Promise<T> {
    setData((oldState) => ({
      state: RestApiState.LOADING,
      error: undefined,
      data: keepData ? oldState.data : undefined,
    }));

    const notif = new NotificationMapper();
    notif.addRequestErrorEventHandlers((errorData, type) => {
      dispatch({ type: 'add', data: { ...errorData, type } });
    });

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

        dispatch({ type: 'add', data: error });

        return undefined;
      });
  };

  const resetRequestData = () => {
    setData({
      state: RestApiState.NOT_STARTED,
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
