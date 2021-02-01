import { useContext, useCallback } from 'react';

import { RestApiErrorDispatchContext } from './RestApiErrorContext';

/**
 * Hook som tilbyr funksjoner for å legge til eller fjerne feil i kontekst.
 * Fungerer kun i komponenter som har en @see RestApiErrorProvider over seg i komponent-treet.
 */
const useRestApiErrorDispatcher = () => {
  const dispatch = useContext(RestApiErrorDispatchContext);
  if (!dispatch) {
    throw new Error('Dispatch er undefined')
  }

  const addErrorMessage = useCallback((data) => dispatch({ type: 'add', data }), []);
  const removeErrorMessages = useCallback(() => dispatch({ type: 'remove' }), []);

  return {
    addErrorMessage,
    removeErrorMessages,
  };
};

export default useRestApiErrorDispatcher;
