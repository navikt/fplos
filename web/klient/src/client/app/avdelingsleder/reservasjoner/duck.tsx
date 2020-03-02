import { Dispatch } from 'redux';

import fpLosApi from 'data/fpLosApi';

export const fetchAvdelingensReservasjoner = (avdelingEnhet: string) => (dispatch: Dispatch) => dispatch(
  fpLosApi.RESERVASJONER_FOR_AVDELING.makeRestApiRequest()(
    { avdelingEnhet }, { keepData: true },
  ),
);

export const opphevReservasjon = (oppgaveId: number) => (dispatch: Dispatch) => dispatch(
  fpLosApi.AVDELINGSLEDER_OPPHEVER_RESERVASJON.makeRestApiRequest()(
    { oppgaveId },
  ),
);

export const getAvdelingensReservasjoner = fpLosApi.RESERVASJONER_FOR_AVDELING.getRestApiData();
