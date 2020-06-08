import { useContext } from 'react';

import { RestDataErrorContext } from 'data/rest-api-hooks/RestDataErrorContext';

const useRestApiErrorDispatcher = () => {
  const { dispatch } = useContext(RestDataErrorContext);
  return dispatch;
};

export default useRestApiErrorDispatcher;
