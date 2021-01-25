import Kodeverk from 'kodeverk/kodeverkTsType';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import useGlobalStateRestApiData from './useGlobalStateRestApiData';

/**
 * Hook som henter et gitt kodeverk fra respons som allerede er hentet fra backend. For å kunne bruke denne
 * må @see useGlobalStateRestApi først brukes for å hente data fra backend
 */
function useKodeverk<T = Kodeverk>(kodeverkType): T[] {
  const alleKodeverk = useGlobalStateRestApiData<{[key: string]: T[]}>(RestApiGlobalStatePathsKeys.KODEVERK);
  return alleKodeverk[kodeverkType];
}

export default useKodeverk;
