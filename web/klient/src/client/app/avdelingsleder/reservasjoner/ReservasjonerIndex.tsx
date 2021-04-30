import React, { FunctionComponent, useEffect, useCallback } from 'react';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';

import Reservasjon from 'types/avdelingsleder/reservasjonTsType';
import ReservasjonerTabell from './components/ReservasjonerTabell';

const EMPTY_ARRAY: Reservasjon[] = [];

interface OwnProps {
  valgtAvdelingEnhet: string;
}

export const ReservasjonerIndex: FunctionComponent<OwnProps> = ({
  valgtAvdelingEnhet,
}) => {
  const { data: reservasjoner = EMPTY_ARRAY, startRequest: hentAvdelingensReservasjoner } = restApiHooks.useRestApiRunner<Reservasjon[]>(
    RestApiPathsKeys.RESERVASJONER_FOR_AVDELING,
  );
  const { startRequest: opphevOppgaveReservasjon } = restApiHooks.useRestApiRunner(RestApiPathsKeys.AVDELINGSLEDER_OPPHEVER_RESERVASJON);

  useEffect(() => {
    hentAvdelingensReservasjoner({ avdelingEnhet: valgtAvdelingEnhet });
  }, []);

  const opphevOppgaveReservasjonFn = useCallback((oppgaveId: number): Promise<any> => opphevOppgaveReservasjon({ oppgaveId })
    .then(() => hentAvdelingensReservasjoner({ avdelingEnhet: valgtAvdelingEnhet })),
  [valgtAvdelingEnhet]);

  const endreOppgaveReservasjonFn = useCallback(() => hentAvdelingensReservasjoner({ avdelingEnhet: valgtAvdelingEnhet }), [valgtAvdelingEnhet]);

  return (
    <ReservasjonerTabell
      opphevReservasjon={opphevOppgaveReservasjonFn}
      reservasjoner={reservasjoner}
      hentAvdelingensReservasjoner={endreOppgaveReservasjonFn}
    />
  );
};

export default ReservasjonerIndex;
