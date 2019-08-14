
import { Dispatch } from 'redux';

import fpLosApi from 'data/fpLosApi';

export const fetchAvdelingensSaksbehandlere = (avdelingEnhet: string) => (dispatch: Dispatch) => dispatch(
  fpLosApi.SAKSBEHANDLERE_FOR_AVDELING.makeRestApiRequest()(
    { avdelingEnhet }, { keepData: true },
  ),
);
export const getAvdelingensSaksbehandlere = fpLosApi.SAKSBEHANDLERE_FOR_AVDELING.getRestApiData();

export const findSaksbehandler = (brukerIdent: string) => (dispatch: Dispatch) => dispatch(fpLosApi.SAKSBEHANDLER_SOK.makeRestApiRequest()(
  brukerIdent,
));
export const getSaksbehandler = fpLosApi.SAKSBEHANDLER_SOK.getRestApiData();
export const getSaksbehandlerSokFinished = fpLosApi.SAKSBEHANDLER_SOK.getRestApiFinished();
export const resetSaksbehandlerSok = () => (dispatch: Dispatch) => dispatch(fpLosApi.SAKSBEHANDLER_SOK.resetRestApi()());

export const addSaksbehandler = (brukerIdent: string, avdelingEnhet: string) => (dispatch: Dispatch) => dispatch(
  fpLosApi.OPPRETT_NY_SAKSBEHANDLER.makeRestApiRequest()(
    { brukerIdent, avdelingEnhet },
  ),
).then(() => fetchAvdelingensSaksbehandlere(avdelingEnhet)(dispatch));

export const removeSaksbehandler = (brukerIdent: string, avdelingEnhet: string) => (dispatch: Dispatch) => dispatch(
  fpLosApi.SLETT_SAKSBEHANDLER.makeRestApiRequest()(
    { brukerIdent, avdelingEnhet },
  ),
).then(() => fetchAvdelingensSaksbehandlere(avdelingEnhet)(dispatch));
