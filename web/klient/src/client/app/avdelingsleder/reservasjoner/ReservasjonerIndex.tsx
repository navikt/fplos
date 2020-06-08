import React, { FunctionComponent, useEffect } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import useRestApiRunner from 'data/rest-api-hooks/useRestApiRunner';
import { RestApiPathsKeys } from 'data/restApiPaths';
import Reservasjon from 'avdelingsleder/reservasjoner/reservasjonTsType';

import {
  fetchAvdelingensReservasjoner, opphevReservasjon, getAvdelingensReservasjoner,
} from './duck';
import ReservasjonerTabell from './components/ReservasjonerTabell';

interface OwnProps {
  reservasjoner: Reservasjon[];
  valgtAvdelingEnhet: string;
}

interface DispatchProps {
  fetchAvdelingensReservasjoner: (avdelingEnhet: string) => void;
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
}

export const ReservasjonerIndex: FunctionComponent<OwnProps & DispatchProps> = ({
  reservasjoner,
  fetchAvdelingensReservasjoner: hentAvdelingensReservasjoner,
  valgtAvdelingEnhet,
  opphevReservasjon: opphevOppgaveReservasjon,
}) => {
  const { startRequest: endreOppgavereservasjon } = useRestApiRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON);
  const { startRequest: flyttOppgavereservasjon } = useRestApiRunner(RestApiPathsKeys.FLYTT_RESERVASJON);
  const {
    startRequest: finnSaksbehandler, resetRequestData: nullstillSaksbehandler,
  } = useRestApiRunner<string>(RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK);

  useEffect(() => {
    hentAvdelingensReservasjoner(valgtAvdelingEnhet);
  }, []);

  const opphevOppgaveReservasjonFn = (oppgaveId: number): Promise<any> => opphevOppgaveReservasjon(oppgaveId)
    .then(() => hentAvdelingensReservasjoner(valgtAvdelingEnhet));

  const endreOppgaveReservasjonFn = (oppgaveId: number, reserverTil: string): Promise<any> => endreOppgavereservasjon({ oppgaveId, reserverTil })
    .then(() => hentAvdelingensReservasjoner(valgtAvdelingEnhet));

  const flyttReservasjonFn = (oppgaveId: number, brukerident: string, begrunnelse: string): Promise<any> => flyttOppgavereservasjon({
    oppgaveId, brukerident, begrunnelse,
  }).then(() => hentAvdelingensReservasjoner(valgtAvdelingEnhet));

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

const mapStateToProps = (state) => ({
  reservasjoner: getAvdelingensReservasjoner(state) || [],
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    fetchAvdelingensReservasjoner,
    opphevReservasjon,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(ReservasjonerIndex);
