
import { Dispatch } from 'redux';

import fpLosApi from 'data/fpLosApi';
import Kodeverk from 'kodeverk/kodeverkTsType';

import KoSorteringType from './KoSorteringTsType';

/* Action types */
const actionType = (name) => `saksliste/${name}`;
const SET_VALGT_SAKSLISTE_ID = actionType('SET_VALGT_SAKSLISTE_ID');
const RESET_VALGT_SAKSLISTE_ID = actionType('RESET_VALGT_SAKSLISTE_ID');

/* Action creators */
export const setValgtSakslisteId = (valgtSakslisteId: number) => ({
  type: SET_VALGT_SAKSLISTE_ID,
  payload: valgtSakslisteId,
});


export const resetValgtSakslisteId = () => ({
  type: RESET_VALGT_SAKSLISTE_ID,
});

export const fetchAvdelingensSakslister = (avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.SAKSLISTER_FOR_AVDELING.makeRestApiRequest()(
    { avdelingEnhet }, { keepData: true },
  ),
);

export const getAvdelingensSakslister = fpLosApi.SAKSLISTER_FOR_AVDELING.getRestApiData();

export const fetchAntallOppgaverForAvdeling = (avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.OPPGAVE_AVDELING_ANTALL.makeRestApiRequest()({ avdelingEnhet }),
);

export const fetchAntallOppgaverForSaksliste = (sakslisteId: number, avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.OPPGAVE_ANTALL.makeRestApiRequest()({ sakslisteId, avdelingEnhet }),
).then(() => dispatch(fetchAntallOppgaverForAvdeling(avdelingEnhet)));


export const getAntallOppgaverForAvdelingResultat = fpLosApi.OPPGAVE_AVDELING_ANTALL.getRestApiData();

export const getAntallOppgaverForSakslisteResultat = fpLosApi.OPPGAVE_ANTALL.getRestApiData();

// fpLosApi.OPPGAVE_ANTALL.getRestApiData();

export const lagNySaksliste = (avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(fpLosApi
  .OPPRETT_NY_SAKSLISTE.makeRestApiRequest()({ avdelingEnhet }))
  .then(() => dispatch(resetValgtSakslisteId()))
  .then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));
export const getNySakslisteId = fpLosApi.OPPRETT_NY_SAKSLISTE.getRestApiData();

export const fjernSaksliste = (sakslisteId: number, avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.SLETT_SAKSLISTE.makeRestApiRequest()({ sakslisteId, avdelingEnhet }),
)
  .then(() => dispatch(resetValgtSakslisteId()))
  .then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));

export const lagreSakslisteNavn = (saksliste: {sakslisteId: number; navn: string}, avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.LAGRE_SAKSLISTE_NAVN.makeRestApiRequest()({ sakslisteId: saksliste.sakslisteId, navn: saksliste.navn, avdelingEnhet }),
).then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));

export const lagreSakslisteBehandlingstype = (sakslisteId: number, behandlingType: {}, isChecked: boolean,
  avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.LAGRE_SAKSLISTE_BEHANDLINGSTYPE.makeRestApiRequest()({
    sakslisteId,
    avdelingEnhet,
    behandlingType,
    checked: isChecked,
  }),
).then(() => dispatch(fetchAntallOppgaverForSaksliste(sakslisteId, avdelingEnhet)))
  .then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));

export const lagreSakslisteFagsakYtelseType = (sakslisteId: number, fagsakYtelseType: string, avdelingEnhet: string) => (dispatch: Dispatch<any>) => {
  const data = fagsakYtelseType !== '' ? { sakslisteId, avdelingEnhet, fagsakYtelseType } : { sakslisteId, avdelingEnhet };
  return dispatch(fpLosApi.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE.makeRestApiRequest()(data))
    .then(() => dispatch(fetchAntallOppgaverForSaksliste(sakslisteId, avdelingEnhet)))
    .then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));
};

export const lagreSakslisteSortering = (sakslisteId: number, sakslisteSorteringValg: KoSorteringType,
  avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.LAGRE_SAKSLISTE_SORTERING.makeRestApiRequest()({ sakslisteId, sakslisteSorteringValg, avdelingEnhet }),
).then(() => dispatch(fetchAntallOppgaverForSaksliste(sakslisteId, avdelingEnhet)))
  .then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));

export const lagreSakslisteSorteringErDynamiskPeriode = (sakslisteId: number, avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE.makeRestApiRequest()({ sakslisteId, avdelingEnhet }),
).then(() => dispatch(fetchAntallOppgaverForSaksliste(sakslisteId, avdelingEnhet)))
  .then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));

export const lagreSakslisteSorteringTidsintervallDato = (sakslisteId: number, fomDato: string, tomDato: string,
  avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO.makeRestApiRequest()({
    sakslisteId, avdelingEnhet, fomDato, tomDato,
  }),
).then(() => dispatch(fetchAntallOppgaverForSaksliste(sakslisteId, avdelingEnhet)))
  .then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));

export const lagreSakslisteSorteringNumeriskIntervall = (sakslisteId: number, fra: number, til: number,
  avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DAGER.makeRestApiRequest()({
    sakslisteId, fra, til, avdelingEnhet,
  }),
).then(() => dispatch(fetchAntallOppgaverForSaksliste(sakslisteId, avdelingEnhet)))
  .then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));

export const lagreSakslisteAndreKriterier = (sakslisteId: number, andreKriterierType: Kodeverk, isChecked: boolean, skalInkludere: boolean,
  avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.LAGRE_SAKSLISTE_ANDRE_KRITERIER.makeRestApiRequest()({
    sakslisteId,
    avdelingEnhet,
    andreKriterierType,
    checked: isChecked,
    inkluder: skalInkludere,
  }),
).then(() => dispatch(fetchAntallOppgaverForSaksliste(sakslisteId, avdelingEnhet)))
  .then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));

export const knyttSaksbehandlerTilSaksliste = (sakslisteId: number, brukerIdent: string, isChecked: boolean,
  avdelingEnhet: string) => (dispatch: Dispatch<any>) => dispatch(
  fpLosApi.LAGRE_SAKSLISTE_SAKSBEHANDLER.makeRestApiRequest()({
    sakslisteId,
    brukerIdent,
    checked: isChecked,
    avdelingEnhet,
  }),
).then(() => dispatch(fetchAvdelingensSakslister(avdelingEnhet)));

/* Reducer */
const initialState = {
  valgtSakslisteId: undefined,
};

interface State {
  valgtSakslisteId?: number;
}

interface Action {
  type: string;
  payload?: any;
}

export const organiseringAvSakslisterReducer = (state: State = initialState, action: Action = { type: '' }) => {
  switch (action.type) {
    case SET_VALGT_SAKSLISTE_ID:
      return {
        ...state,
        valgtSakslisteId: action.payload,
      };
    case RESET_VALGT_SAKSLISTE_ID:
      return initialState;
    default:
      return state;
  }
};

const getOrganiseringAvSakslisterContext = (state) => state.default.organiseringAvSakslisterContext;
export const getValgtSakslisteId = (state: any) => getOrganiseringAvSakslisterContext(state).valgtSakslisteId;
