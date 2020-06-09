import { useContext } from 'react';

import { RestApiErrorStateContext } from './RestApiErrorContext';

const useRestApiError = () => {
  const state = useContext(RestApiErrorStateContext);
  return state.errors;
};

export default useRestApiError;
