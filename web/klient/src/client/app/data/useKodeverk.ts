import Kodeverk from 'types/kodeverkTsType';
import { RestApiGlobalStatePathsKeys, restApiHooks } from './fplosRestApi';

/**
 * Hook som henter et gitt kodeverk fra respons som allerede er hentet fra backend. For å kunne bruke denne
 * må @see useGlobalStateRestApi først brukes for å hente data fra backend
 */
function useKodeverk<T = Kodeverk>(kodeverkType: string): T[] {
  const alleKodeverk = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.KODEVERK);
  // @ts-ignore Fiks
  return alleKodeverk[kodeverkType];
}

export default useKodeverk;
