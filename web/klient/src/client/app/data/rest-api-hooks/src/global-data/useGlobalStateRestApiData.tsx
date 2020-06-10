import { useContext } from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import { RestApiGlobalDataStateContext } from './RestApiGlobalDataContext';

/**
 * Hook som bruker respons som allerede er hentet fra backend. For å kunne bruke denne
 * må @see useGlobalStateRestApi først brukes for å hente data fra backend
 */
function useGlobalStateRestApiData<T>(key: RestApiGlobalStatePathsKeys): T {
  const state = useContext(RestApiGlobalDataStateContext);
  return state[key];
}

export default useGlobalStateRestApiData;
