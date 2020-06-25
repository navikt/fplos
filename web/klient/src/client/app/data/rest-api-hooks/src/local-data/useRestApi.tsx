import {
  useState, useEffect, DependencyList, useContext,
} from 'react';

import { NotificationMapper } from 'data/rest-api';
import { RestApiPathsKeys } from 'data/restApiPaths';

import { RestApiRequestContext } from '../RestApiContext';
import useRestApiErrorDispatcher from '../error/useRestApiErrorDispatcher';
import RestApiState from '../RestApiState';

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

  const requestApi = useContext(RestApiRequestContext);

  useEffect(() => {
    setData((oldState) => ({
      state: RestApiState.LOADING,
      error: undefined,
      data: keepData ? oldState.data : undefined,
    }));

    requestApi.startRequest(key, params, notif)
      .then((dataRes) => {
        if (dataRes.payload === 'INTERNAL_CANCELLATION') {
          setData((oldState) => ({
            state: RestApiState.NOT_STARTED,
            data: keepData ? oldState.data : undefined,
            error: undefined,
          }));
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
