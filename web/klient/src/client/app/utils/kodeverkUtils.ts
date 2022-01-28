import KodeverkType from 'kodeverk/kodeverkTyper';
import AlleKodeverk from 'types/alleKodeverkTsType';

export const getKodeverknavnFraKode = (
  kode: string,
  kodeverkType: KodeverkType,
  alleKodeverk: AlleKodeverk,
): string => {
  const kodeverkForType = alleKodeverk[kodeverkType];
  if (!kodeverkForType || kodeverkForType.length === 0) {
    return '';
  }

  const kodeverk = kodeverkForType.find((k) => k.kode === kode);
  return kodeverk ? kodeverk.navn : '';
};

export const getKodeverknavnFn = (
  alleKodeverk: AlleKodeverk,
) => (
  kode: string,
  kodeverkType: KodeverkType,
): string => getKodeverknavnFraKode(kode, kodeverkType, alleKodeverk);
