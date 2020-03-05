import React, { Component } from 'react';
import { getValgtAvdelingEnhet } from 'app/duck';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';
import reservasjonPropType from 'avdelingsleder/reservasjoner/reservasjonPropType';
import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import { endreOppgaveReservasjon, flyttReservasjon } from 'saksbehandler/behandlingskoer/duck';
import {
  fetchAvdelingensReservasjoner, getAvdelingensReservasjoner, opphevReservasjon,
} from './duck';
import ReservasjonerPanel from './components/ReservasjonerPanel';

interface TsProps {
  fetchAvdelingensReservasjoner: (avdelingEnhet: string) => void;
  avdelingensReservasjoner: Reservasjon[];
  valgtAvdelingEnhet: string;
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
}
export class ReservasjonerIndex extends Component<TsProps> {
  static propTypes = {
    fetchAvdelingensReservasjoner: PropTypes.func.isRequired,
    avdelingensReservasjoner: PropTypes.arrayOf(reservasjonPropType),
    valgtAvdelingEnhet: PropTypes.string.isRequired,
    opphevReservasjon: PropTypes.func.isRequired,
    endreOppgaveReservasjon: PropTypes.func.isRequired,
    flyttReservasjon: PropTypes.func.isRequired,
  };

  static defaultProps = {
    avdelingensReservasjoner: [],
  }

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

  render = () => {
    const {
      avdelingensReservasjoner,
    } = this.props;

    return (
      <ReservasjonerPanel
        reservasjoner={avdelingensReservasjoner}
        opphevReservasjon={this.opphevOppgaveReservasjon}
        endreOppgaveReservasjon={this.endreOppgaveReservasjon}
        flyttReservasjon={this.flyttReservasjon}
      />
    );
  }
}

const mapStateToProps = state => ({
  avdelingensReservasjoner: getAvdelingensReservasjoner(state),
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  ...bindActionCreators({
    fetchAvdelingensReservasjoner,
    opphevReservasjon,
    endreOppgaveReservasjon,
    flyttReservasjon,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(ReservasjonerIndex);
