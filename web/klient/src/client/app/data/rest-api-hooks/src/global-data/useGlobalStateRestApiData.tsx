import { useContext } from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import { RestApiStateContext } from '../RestApiContext';

/**
 * Hook som bruker respons som allerede er hentet fra backend. For å kunne bruke denne
 * må @see useGlobalStateRestApi først brukes for å hente data fra backend
 */
function useGlobalStateRestApiData<T>(key: RestApiGlobalStatePathsKeys): T {
  const state = useContext(RestApiStateContext);
  return state[key];
}

export default useGlobalStateRestApiData;
