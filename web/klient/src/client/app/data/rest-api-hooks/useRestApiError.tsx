import { useContext } from 'react';

import { RestApiErrorContext } from 'data/rest-api-hooks/RestApiErrorContext';

const useRestApiError = () => {
  const { state } = useContext(RestApiErrorContext);
  return state.errors;
};

export default useRestApiError;
