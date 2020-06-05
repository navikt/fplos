import React, { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';
import {
  endreOppgaveReservasjon, flyttReservasjon, finnSaksbehandler as getSaksbehandler, resetSaksbehandler,
} from 'saksbehandler/behandlingskoer/duck';
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
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
  finnSaksbehandler: (brukerIdent: string) => Promise<string>;
  nullstillSaksbehandler: () => Promise<string>;
}

export class ReservasjonerIndex extends Component<DispatchProps & OwnProps> {
  componentDidMount = (): void => {
    const { fetchAvdelingensReservasjoner: hentAvdelingensReservasjoner, valgtAvdelingEnhet } = this.props;
    hentAvdelingensReservasjoner(valgtAvdelingEnhet);
  }

  opphevOppgaveReservasjon = (oppgaveId: number): Promise<any> => {
    const { opphevReservasjon: opphevOppgaveReservasjon, fetchAvdelingensReservasjoner: fetchReserverte, valgtAvdelingEnhet } = this.props;
    return opphevOppgaveReservasjon(oppgaveId)
      .then(() => fetchReserverte(valgtAvdelingEnhet));
  }

  endreOppgaveReservasjon = (oppgaveId: number, reserverTil: string): Promise<any> => {
    const { endreOppgaveReservasjon: endreReservasjon, fetchAvdelingensReservasjoner: fetchReserverte, valgtAvdelingEnhet } = this.props;
    return endreReservasjon(oppgaveId, reserverTil)
      .then(() => fetchReserverte(valgtAvdelingEnhet));
  }

  flyttReservasjon = (oppgaveId: number, brukerident: string, begrunnelse: string): Promise<any> => {
    const { flyttReservasjon: flytt, fetchAvdelingensReservasjoner: fetchReserverte, valgtAvdelingEnhet } = this.props;
    return flytt(oppgaveId, brukerident, begrunnelse)
      .then(() => fetchReserverte(valgtAvdelingEnhet));
  }

  render = (): ReactNode => {
    const { finnSaksbehandler, nullstillSaksbehandler, reservasjoner } = this.props;
    return (
      <ReservasjonerTabell
        opphevReservasjon={this.opphevOppgaveReservasjon}
        endreOppgaveReservasjon={this.endreOppgaveReservasjon}
        flyttReservasjon={this.flyttReservasjon}
        finnSaksbehandler={finnSaksbehandler}
        nullstillSaksbehandler={nullstillSaksbehandler}
        reservasjoner={reservasjoner}
      />
    );
  }
}

const mapStateToProps = (state) => ({
  reservasjoner: getAvdelingensReservasjoner(state) || [],
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    fetchAvdelingensReservasjoner,
    opphevReservasjon,
    endreOppgaveReservasjon,
    flyttReservasjon,
    finnSaksbehandler: getSaksbehandler,
    nullstillSaksbehandler: resetSaksbehandler,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(ReservasjonerIndex);
