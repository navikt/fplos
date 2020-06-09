import {
  useState, useEffect, DependencyList,
} from 'react';

import { createRequestApi, NotificationMapper } from 'data/rest-api';
import { endpoints, RestApiPathsKeys } from 'data/restApiPaths';
import useRestApiErrorDispatcher from 'data/rest-api-hooks/useRestApiErrorDispatcher';

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

  const dispatch = useRestApiErrorDispatcher();

  const setPartData = (partialData) => setData({ ...data, ...partialData });

  const notif = new NotificationMapper();
  notif.addRequestErrorEventHandlers((errorData, type) => {
    dispatch({ type: 'add', data: { ...errorData, type } });
  });

  useEffect(() => {
    setPartData({
      state: RestApiState.LOADING,
    });

    requestApi.getRequestRunner(key).startProcess(params, notif)
      .then((dataRes) => {
        if (dataRes.payload === 'INTERNAL_CANCELLATION') {
          setPartData({
            state: RestApiState.NOT_STARTED,
            data: undefined,
          });
        } else {
          setPartData({
            state: RestApiState.SUCCESS,
            data: dataRes.payload,
          });
        }
      })
      .catch((error) => {
        setPartData({
          state: RestApiState.ERROR,
          error,
        });
      });
  }, dependencies);

  return data;
}

export default useRestApi;
