import { fpLosApiKeys } from 'data/fpLosApi';
import Kodeverk from 'kodeverk/kodeverkTsType';

import useRestApiData from './useRestApiData';

const useKodeverk = (kodeverkType): Kodeverk[] => {
  const alleKodeverk = useRestApiData<{[key: string]: Kodeverk[]}>(fpLosApiKeys.KODEVERK);
  return alleKodeverk[kodeverkType];
};

export default useKodeverk;
