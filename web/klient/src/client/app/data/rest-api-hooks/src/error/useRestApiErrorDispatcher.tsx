import { useContext } from 'react';

import { RestApiErrorContext } from './RestApiErrorContext';

const useRestApiErrorDispatcher = () => {
  const { dispatch } = useContext(RestApiErrorContext);
  return dispatch;
};

export default useRestApiErrorDispatcher;
