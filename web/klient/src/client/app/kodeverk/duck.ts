import { createSelector } from 'reselect';
import fpLosApi from 'data/fpLosApi';

/* Selectors */
export const getKodeverk = (kodeverkType: string) => createSelector(
  [fpLosApi.KODEVERK.getRestApiData()],
  (kodeverk = {}) => kodeverk[kodeverkType],
);

export const getAlleKodeverk = createSelector(
  [fpLosApi.KODEVERK.getRestApiData()],
  (kodeverk = {}) => kodeverk,
);

export const getKodeverkReceived = fpLosApi.KODEVERK.getRestApiFinished();
