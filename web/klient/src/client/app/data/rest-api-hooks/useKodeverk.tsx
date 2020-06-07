import Kodeverk from 'kodeverk/kodeverkTsType';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import useGlobalStateRestApi from './useGlobalStateRestApi';

function useKodeverk<T = Kodeverk>(kodeverkType): T[] {
  const alleKodeverk = useGlobalStateRestApi<{[key: string]: T[]}>(RestApiGlobalStatePathsKeys.KODEVERK);
  return alleKodeverk[kodeverkType];
}

export default useKodeverk;
