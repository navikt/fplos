import { useContext, useCallback } from 'react';

import { RestApiErrorDispatchContext } from './RestApiErrorContext';

/**
 * Hook som tilbyr funksjoner for å legge til eller fjerne feil i kontekst.
 * Fungerer kun i komponenter som har en @see RestApiErrorProvider over seg i komponent-treet.
 */
const useRestApiErrorDispatcher = () => {
  const dispatch = useContext(RestApiErrorDispatchContext);

  const addErrorMessage = useCallback((data) => {
    if (dispatch) {
      dispatch({ type: 'add', data });
    }
  }, []);
  const removeErrorMessages = useCallback(() => {
    if (dispatch) {
      dispatch({ type: 'remove' });
    }
  }, []);

  return {
    addErrorMessage,
    removeErrorMessages,
  };
};

export default useRestApiErrorDispatcher;
