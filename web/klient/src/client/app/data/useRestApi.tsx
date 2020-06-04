import { useState, useEffect, useContext } from 'react';
import { createRequestApi } from 'data/rest-api';
import { endpoints } from 'data/fpLosApi';

import { RestDataContext } from './RestDataContext';

const contextPath = 'fplos';
const requestApi = createRequestApi(contextPath, endpoints);

export const apiStates = {
  LOADING: 'LOADING',
  SUCCESS: 'SUCCESS',
  ERROR: 'ERROR',
};

const useRestApi = (key) => {
  const [data, setData] = useState({
    state: apiStates.LOADING,
    error: '',
    data: [],
  });

  const globalState = useContext(RestDataContext);
  const { dispatch } = globalState;

  const setPartData = (partialData) => setData({ ...data, ...partialData });

  useEffect(() => {
    setPartData({
      state: apiStates.LOADING,
    });
    requestApi.getRequestRunner(key).startProcess({})
      .then((dataRes) => {
        dispatch({ type: 'success', key, data: dataRes.payload });
        setPartData({
          state: apiStates.SUCCESS,
          data: dataRes,
        });
      })
      .catch(() => {
        setPartData({
          state: apiStates.ERROR,
          error: 'fetch failed',
        });
      });
  }, []);

  return data;
};

export default useRestApi;
