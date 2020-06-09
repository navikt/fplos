import Kodeverk from 'kodeverk/kodeverkTsType';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import useGlobalStateRestApiData from './useGlobalStateRestApiData';

function useKodeverk<T = Kodeverk>(kodeverkType): T[] {
  const alleKodeverk = useGlobalStateRestApiData<{[key: string]: T[]}>(RestApiGlobalStatePathsKeys.KODEVERK);
  return alleKodeverk[kodeverkType];
}

export default useKodeverk;
