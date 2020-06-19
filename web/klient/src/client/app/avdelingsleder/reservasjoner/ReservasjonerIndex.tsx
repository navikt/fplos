import React, { FunctionComponent, useEffect, useCallback } from 'react';

import { useRestApiRunner } from 'data/rest-api-hooks';
import { RestApiPathsKeys } from 'data/restApiPaths';

import ReservasjonerTabell from './components/ReservasjonerTabell';

const EMPTY_ARRAY = [];

interface OwnProps {
  valgtAvdelingEnhet: string;
}

export const ReservasjonerIndex: FunctionComponent<OwnProps> = ({
  valgtAvdelingEnhet,
}) => {
  const { startRequest: endreOppgavereservasjon } = useRestApiRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON);
  const { startRequest: flyttOppgavereservasjon } = useRestApiRunner(RestApiPathsKeys.FLYTT_RESERVASJON);
  const {
    startRequest: finnSaksbehandler, resetRequestData: nullstillSaksbehandler,
  } = useRestApiRunner<string>(RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK);

  const { data: reservasjoner = EMPTY_ARRAY, startRequest: hentAvdelingensReservasjoner } = useRestApiRunner(RestApiPathsKeys.RESERVASJONER_FOR_AVDELING);
  const { startRequest: opphevOppgaveReservasjon } = useRestApiRunner(RestApiPathsKeys.AVDELINGSLEDER_OPPHEVER_RESERVASJON);

  useEffect(() => {
    hentAvdelingensReservasjoner({ avdelingEnhet: valgtAvdelingEnhet });
  }, []);

  const opphevOppgaveReservasjonFn = useCallback((oppgaveId: number): Promise<any> => opphevOppgaveReservasjon({ oppgaveId })
    .then(() => hentAvdelingensReservasjoner({ avdelingEnhet: valgtAvdelingEnhet })),
  [valgtAvdelingEnhet]);

  const endreOppgaveReservasjonFn = useCallback((oppgaveId: number, reserverTil: string): Promise<any> => endreOppgavereservasjon({ oppgaveId, reserverTil })
    .then(() => hentAvdelingensReservasjoner({ avdelingEnhet: valgtAvdelingEnhet })),
  [valgtAvdelingEnhet]);

  const flyttReservasjonFn = useCallback((oppgaveId: number, brukerident: string, begrunnelse: string): Promise<any> => flyttOppgavereservasjon({
    oppgaveId, brukerident, begrunnelse,
  }).then(() => hentAvdelingensReservasjoner({ avdelingEnhet: valgtAvdelingEnhet })),
  [valgtAvdelingEnhet]);

  return (
    <ReservasjonerTabell
      opphevReservasjon={opphevOppgaveReservasjonFn}
      endreOppgaveReservasjon={endreOppgaveReservasjonFn}
      flyttReservasjon={flyttReservasjonFn}
      finnSaksbehandler={finnSaksbehandler}
      nullstillSaksbehandler={nullstillSaksbehandler}
      reservasjoner={reservasjoner}
    />
  );
};

export default ReservasjonerIndex;
