import Kodeverk from 'kodeverk/kodeverkTsType';
import { RestApiPathsKeys } from 'data/restApiPaths';

import useRestApiData from './useRestApiData';

function useKodeverk<T = Kodeverk>(kodeverkType): T[] {
  const alleKodeverk = useRestApiData<{[key: string]: T[]}>(RestApiPathsKeys.KODEVERK);
  return alleKodeverk[kodeverkType];
}

export default useKodeverk;
