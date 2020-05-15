import React, { Component } from 'react';
import { getValgtAvdelingEnhet } from 'app/duck';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';
import { endreOppgaveReservasjon, flyttReservasjon } from 'saksbehandler/behandlingskoer/duck';
import {
  fetchAvdelingensReservasjoner, opphevReservasjon,
} from './duck';
import ReservasjonerPanel from './components/ReservasjonerPanel';

interface OwnProps {
  valgtAvdelingEnhet: string;
}

interface DispatchProps {
  fetchAvdelingensReservasjoner: (avdelingEnhet: string) => void;
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
}

export class ReservasjonerIndex extends Component<DispatchProps & OwnProps> {
  componentDidMount = () => {
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

  render = () => (
    <ReservasjonerPanel
      opphevReservasjon={this.opphevOppgaveReservasjon}
      endreOppgaveReservasjon={this.endreOppgaveReservasjon}
      flyttReservasjon={this.flyttReservasjon}
    />
  )
}

const mapStateToProps = (state) => ({
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    fetchAvdelingensReservasjoner,
    opphevReservasjon,
    endreOppgaveReservasjon,
    flyttReservasjon,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(ReservasjonerIndex);
