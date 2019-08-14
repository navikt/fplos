import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import { getValgtAvdelingEnhet } from 'app/duck';
import {
  fetchOppgaverForAvdeling as fetchOppgaverForAvdelingActionCreator,
  fetchOppgaverPerDato as fetchOppgaverPerDatoActionCreator,
  fetchOppgaverAvdelingManueltPaVent as fetchOppgaverAvdelingManueltPaVentActionCreator,
  fetchOppgaverPerForsteStonadsdag as fetchOppgaverPerForsteStonadsdagActionCreator,
} from './duck';
import NokkeltallPanel from './components/NokkeltallPanel';

interface TsProps {
  fetchOppgaverForAvdeling: (avdelingEnhet: string) => void;
  fetchOppgaverPerDato: (avdelingEnhet: string) => void;
  fetchOppgaverAvdelingManueltPaVent: (avdelingEnhet: string) => void;
  fetchOppgaverPerForsteStonadsdag: (avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
}

/**
 * NokkeltallIndex
 */
export class NokkeltallIndex extends Component<TsProps> {
  static propTypes = {
    fetchOppgaverForAvdeling: PropTypes.func.isRequired,
    fetchOppgaverPerDato: PropTypes.func.isRequired,
    fetchOppgaverAvdelingManueltPaVent: PropTypes.func.isRequired,
    fetchOppgaverPerForsteStonadsdag: PropTypes.func.isRequired,
    valgtAvdelingEnhet: PropTypes.string.isRequired,
  };

  componentDidMount = () => {
    const {
      fetchOppgaverForAvdeling, fetchOppgaverPerDato, fetchOppgaverAvdelingManueltPaVent, fetchOppgaverPerForsteStonadsdag, valgtAvdelingEnhet,
    } = this.props;
    fetchOppgaverForAvdeling(valgtAvdelingEnhet);
    fetchOppgaverPerDato(valgtAvdelingEnhet);
    fetchOppgaverAvdelingManueltPaVent(valgtAvdelingEnhet);
    fetchOppgaverPerForsteStonadsdag(valgtAvdelingEnhet);
  }

  render = () => (
    <NokkeltallPanel />
  )
}

const mapStateToProps = state => ({
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  ...bindActionCreators({
    fetchOppgaverForAvdeling: fetchOppgaverForAvdelingActionCreator,
    fetchOppgaverPerDato: fetchOppgaverPerDatoActionCreator,
    fetchOppgaverAvdelingManueltPaVent: fetchOppgaverAvdelingManueltPaVentActionCreator,
    fetchOppgaverPerForsteStonadsdag: fetchOppgaverPerForsteStonadsdagActionCreator,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(NokkeltallIndex);
