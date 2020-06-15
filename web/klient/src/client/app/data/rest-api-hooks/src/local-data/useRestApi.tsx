import {
  useState, useEffect, DependencyList,
} from 'react';

import { createRequestApi, NotificationMapper } from 'data/rest-api';
import { endpoints, RestApiPathsKeys } from 'data/restApiPaths';

import useRestApiErrorDispatcher from '../error/useRestApiErrorDispatcher';
import RestApiState from '../RestApiState';

const requestApi = createRequestApi(endpoints);

interface RestApiData<T> {
  state: RestApiState;
  error?: Error;
  data?: T;
}

/**
  * Hook som utfører et restkall ved mount. En kan i tillegg legge ved en dependencies-liste som kan trigge ny henting når data
  * blir oppdatert. Hook returnerer rest-kallets status/resultat/feil
  */
function useRestApi<T>(key: RestApiPathsKeys, params: any = {}, keepData = false, dependencies: DependencyList = []):RestApiData<T> {
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

  useEffect(() => {
    setData((oldState) => ({
      state: RestApiState.LOADING,
      error: undefined,
      data: keepData ? oldState.data : undefined,
    }));

    requestApi.getRequestRunner(key).startProcess(params, notif)
      .then((dataRes) => {
        if (dataRes.payload === 'INTERNAL_CANCELLATION') {
          setData({
            state: RestApiState.NOT_STARTED,
            error: undefined,
            data: undefined,
          });
        } else {
          setData({
            state: RestApiState.SUCCESS,
            data: dataRes.payload,
            error: undefined,
          });
        }
      })
      .catch((error) => {
        setData({
          state: RestApiState.ERROR,
          data: undefined,
          error,
        });
      });
  }, dependencies);

  return data;
}

export default useRestApi;
