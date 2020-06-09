import { useContext } from 'react';

import { RestApiErrorContext } from './RestApiErrorContext';

const useRestApiError = () => {
  const { state } = useContext(RestApiErrorContext);
  return state.errors;
};

export default useRestApiError;
