import { useContext, useCallback } from 'react';

import { RestApiErrorDispatchContext } from './RestApiErrorContext';

const useRestApiErrorDispatcher = () => {
  const dispatch = useContext(RestApiErrorDispatchContext);

  const addErrorMessage = useCallback((data) => dispatch({ type: 'add', data }), []);
  const removeErrorMessage = useCallback(() => dispatch({ type: 'remove' }), []);

  return {
    addErrorMessage,
    removeErrorMessage,
  };
};

export default useRestApiErrorDispatcher;
