import { Dispatch } from 'redux';

import fpLosApi from 'data/fpLosApi';

export const fetchOppgaverForAvdeling = (avdelingEnhet: string) => (dispatch: Dispatch) => dispatch(
  fpLosApi.HENT_OPPGAVER_FOR_AVDELING.makeRestApiRequest()(
    { avdelingEnhet }, { keepData: true },
  ),
);
export const getOppgaverForAvdeling = fpLosApi.HENT_OPPGAVER_FOR_AVDELING.getRestApiData();

export const fetchOppgaverPerDato = (avdelingEnhet: string) => (dispatch: Dispatch) => dispatch(
  fpLosApi.HENT_OPPGAVER_PER_DATO.makeRestApiRequest()(
    { avdelingEnhet }, { keepData: true },
  ),
);
export const getOppgaverPerDato = fpLosApi.HENT_OPPGAVER_PER_DATO.getRestApiData();

export const fetchOppgaverAvdelingManueltPaVent = (avdelingEnhet: string) => (dispatch: Dispatch) => dispatch(
  fpLosApi.HENT_OPPGAVER_MANUELT_PA_VENT.makeRestApiRequest()(
    { avdelingEnhet }, { keepData: true },
  ),
);
export const getOppgaverAvdelingManueltPaVent = fpLosApi.HENT_OPPGAVER_MANUELT_PA_VENT.getRestApiData();

export const fetchOppgaverPerForsteStonadsdag = (avdelingEnhet: string) => (dispatch: Dispatch) => dispatch(
  fpLosApi.HENT_OPPGAVER_PER_FORSTE_STONADSDAG.makeRestApiRequest()(
    { avdelingEnhet }, { keepData: true },
  ),
);
export const getOppgaverPerForsteStonadsdag = fpLosApi.HENT_OPPGAVER_PER_FORSTE_STONADSDAG.getRestApiData();
