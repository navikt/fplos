import { useContext } from 'react';

import { RestApiErrorContext } from 'data/rest-api-hooks/RestApiErrorContext';

const useRestApiErrorDispatcher = () => {
  const { dispatch } = useContext(RestApiErrorContext);
  return dispatch;
};

export default useRestApiErrorDispatcher;
