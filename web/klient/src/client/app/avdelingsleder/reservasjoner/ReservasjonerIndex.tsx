import React, { Component } from 'react';
import { getValgtAvdelingEnhet } from 'app/duck';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';
import reservasjonPropType from 'avdelingsleder/reservasjoner/reservasjonPropType';
import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import {
  fetchAvdelingensReservasjoner, getAvdelingensReservasjoner, opphevReservasjon,
} from './duck';
import ReservasjonerPanel from './components/ReservasjonerPanel';

interface TsProps {
  fetchAvdelingensReservasjoner: (avdelingEnhet: string) => void;
  avdelingensReservasjoner: Reservasjon[];
  valgtAvdelingEnhet: string;

}
export class ReservasjonerIndex extends Component<TsProps> {
  static propTypes = {
    fetchAvdelingensReservasjoner: PropTypes.func.isRequired,
    avdelingensReservasjoner: PropTypes.arrayOf(reservasjonPropType),
    valgtAvdelingEnhet: PropTypes.string.isRequired,
  };

  static defaultProps = {
    avdelingensReservasjoner: [],
  }

  componentDidMount = () => {
    const { fetchAvdelingensReservasjoner: hentAvdelingensReservasjoner, valgtAvdelingEnhet } = this.props;
    hentAvdelingensReservasjoner(valgtAvdelingEnhet);
  }

  render = () => {
    const {
      avdelingensReservasjoner,
    } = this.props;

    return (
      <ReservasjonerPanel reservasjoner={avdelingensReservasjoner} />
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
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(ReservasjonerIndex);
