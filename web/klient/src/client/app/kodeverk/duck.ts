import { createSelector } from 'reselect';
import fpLosApi from 'data/fpLosApi';

/* Selectors */
export const getAlleKodeverk = createSelector(
  [fpLosApi.KODEVERK.getRestApiData()],
  (kodeverk = {}) => kodeverk,
);

export const getKodeverkReceived = fpLosApi.KODEVERK.getRestApiFinished();
