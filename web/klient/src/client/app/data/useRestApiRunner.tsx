import { useContext } from 'react';
import { RestApiPathsKeys } from 'data/restApiPaths';

import { RestDataContext } from './RestDataContext';


export enum ApiState {
  LOADING = 'LOADING',
  SUCCESS = 'SUCCESS',
  ERROR = 'ERROR',
}

const useRestApiRunner = () => {
  const context = useContext(RestDataContext);
  const { dispatch, requestApi } = context;

  const fn = function doCall<T>(key: RestApiPathsKeys, params: any = {}, options: any = { keepData: false }):Promise<T> {
    if (!options.keepData) {
      dispatch({ type: 'remove', key });
    }

    return requestApi.getRequestRunner(key).startProcess(params)
      .then((dataRes) => {
        dispatch({ type: 'success', key, data: dataRes.payload });
      })
      .catch(() => {
        // FIXME DO stuff
      });
  };
  return fn;
};

export default useRestApiRunner;
