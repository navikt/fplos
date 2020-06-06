
import { createSelector } from 'reselect';
import { Dispatch } from 'redux';

import fpLosApi from 'data/fpLosApi';

/* Action types */
const SET_SAKSLISTE_ID = 'SET_SAKSLISTE_ID';

/* Action creators */
export const setValgtSakslisteId = (setSakslisteId: number) => ({
  type: SET_SAKSLISTE_ID,
  data: setSakslisteId,
});

export const hentReservasjonsstatus = (oppgaveId: number) => (dispatch: Dispatch) => dispatch(
  fpLosApi.HENT_RESERVASJONSSTATUS.makeRestApiRequest()(
    { oppgaveId },
  ),
);

export const isSaksbehandlerSokStartet = fpLosApi.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK.getRestApiStarted();
export const isSaksbehandlerSokFerdig = fpLosApi.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK.getRestApiFinished();
export const getSaksbehandler = fpLosApi.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK.getRestApiData();
export const resetSaksbehandler = () => (dispatch: Dispatch) => dispatch(fpLosApi.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK.resetRestApi()());

export const fetchSakslistensSaksbehandlere = (sakslisteId: number) => (dispatch: Dispatch) => dispatch(
  fpLosApi.SAKSLISTE_SAKSBEHANDLERE.makeRestApiRequest()(
    { sakslisteId }, { keepData: false },
  ),
);
export const getSakslistensSaksbehandlere = fpLosApi.SAKSLISTE_SAKSBEHANDLERE.getRestApiData();

export const fetchAntallOppgaverForBehandlingsko = (sakslisteId: number) => (dispatch: Dispatch) => dispatch(
  fpLosApi.BEHANDLINGSKO_OPPGAVE_ANTALL.makeRestApiRequest()({ sakslisteId }),
);
export const getAntallOppgaverForBehandlingskoResultat = fpLosApi.BEHANDLINGSKO_OPPGAVE_ANTALL.getRestApiData();


/* Reducers */
const initialState = {
  valgtSakslisteId: undefined,
};

interface Action {
  type: string;
  data?: any;
}
interface State {
  valgtSakslisteId?: number;
}

export const behandlingskoerReducer = (state: State = initialState, action: Action = { type: '' }) => {
  switch (action.type) {
    case SET_SAKSLISTE_ID:
      return {
        ...state,
        valgtSakslisteId: action.data,
      };
    default:
      return state;
  }
};

/* Selectors */
const getBehandlingskoerContext = (state) => state.default.behandlingskoerContext;
export const getValgtSakslisteId = createSelector([getBehandlingskoerContext], (behandlingskoerContext) => behandlingskoerContext.valgtSakslisteId);
