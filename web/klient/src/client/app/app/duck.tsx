import fpLosApi from 'data/fpLosApi';
import { Dispatch } from 'redux';

<<<<<<< HEAD
/* Action types */
const SET_AVDELING_ENHET = 'SET_AVDELING_ENHET';
const RESET_AVDELING_ENHET = 'RESET_AVDELING_ENHET';

/* Action creators */
export const setAvdelingEnhet = (avdelingEnhet: string): { type: string; data: string } => ({
  type: SET_AVDELING_ENHET,
  data: avdelingEnhet,
});

export const resetAvdelingEnhet = (): { type: string } => ({
  type: RESET_AVDELING_ENHET,
});

export const fetchAvdelingeneTilAvdelingsleder = fpLosApi.AVDELINGER.makeRestApiRequest();
export const getAvdelingeneTilAvdelingslederResultat = fpLosApi.AVDELINGER.getRestApiData();
export const resetAvdelingeneTilAvdelingslederData = fpLosApi.AVDELINGER.resetRestApi();

/* Reducers */
const initialState = {
  valgtAvdelingEnhet: undefined,
};

interface Action {
  type: string;
  data?: any;
}
interface State {
  valgtAvdelingEnhet?: string;
}

export const appReducer = (state: State = initialState, action: Action = { type: '' }) => {
  switch (action.type) {
    case SET_AVDELING_ENHET:
      return {
        ...state,
        valgtAvdelingEnhet: action.data,
      };
    case RESET_AVDELING_ENHET:
      return {
        ...state,
        valgtAvdelingEnhet: undefined,
      };
    default:
      return state;
  }
};

/* Selectors */
const getAppContext = (state) => state.default.appContext;

export const getValgtAvdelingEnhet = createSelector([getAppContext], (appContext) => appContext.valgtAvdelingEnhet);
export const getNavAnsattName = createSelector([fpLosApi.NAV_ANSATT.getRestApiData()], (navAnsatt: NavAnsatt = NavAnsattDefault) => navAnsatt.navn);
export const getNavAnsattKanSaksbehandle = createSelector([fpLosApi.NAV_ANSATT.getRestApiData()], (navAnsatt: NavAnsatt = NavAnsattDefault) => navAnsatt
  .kanSaksbehandle);
export const getNavAnsattKanOppgavestyre = createSelector([fpLosApi.NAV_ANSATT.getRestApiData()], (navAnsatt: NavAnsatt = NavAnsattDefault) => navAnsatt
  .kanOppgavestyre);
export const getNavAnsattKanBehandleKode6 = createSelector([fpLosApi.NAV_ANSATT.getRestApiData()], (navAnsatt: NavAnsatt = NavAnsattDefault) => navAnsatt
  .kanBehandleKode6);
export const getFunksjonellTid = createSelector([fpLosApi.NAV_ANSATT.getRestApiData()], (navAnsatt: NavAnsatt = NavAnsattDefault) => navAnsatt.funksjonellTid);
export const getFpsakUrl = createSelector([fpLosApi.FPSAK_URL.getRestApiData()], (fpsakUrl: {verdi: undefined } = { verdi: undefined }) => fpsakUrl.verdi);
export const getFptilbakeUrl = createSelector([fpLosApi.FPTILBAKE_URL.getRestApiData()],
  (fptilbakeUrl: {verdi: undefined } = { verdi: undefined }) => fptilbakeUrl.verdi);
export const hentFpsakInternBehandlingId = (uuid: string) => (dispatch: Dispatch<any>) => dispatch(
=======
const hentFpsakInternBehandlingId = (uuid: string) => (dispatch: Dispatch<any>) => dispatch(
>>>>>>> FIX div
  fpLosApi.FPSAK_BEHANDLING_ID.makeRestApiRequest()(
    { uuid },
  ),
);

export default hentFpsakInternBehandlingId;
