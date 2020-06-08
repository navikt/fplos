import { useContext } from 'react';

import { RestDataErrorContext } from 'data/rest-api-hooks/RestDataErrorContext';

const useRestApiError = () => {
  const { state } = useContext(RestDataErrorContext);
  return state.errors;
};

export default useRestApiError;
