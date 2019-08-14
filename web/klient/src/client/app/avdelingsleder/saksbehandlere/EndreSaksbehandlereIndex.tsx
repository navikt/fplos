import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import { getValgtAvdelingEnhet } from 'app/duck';
import { Saksbehandler } from './saksbehandlerTsType';
import saksbehandlerPropType from './saksbehandlerPropType';
import SaksbehandlerePanel from './components/SaksbehandlerePanel';
import {
  fetchAvdelingensSaksbehandlere, getAvdelingensSaksbehandlere, findSaksbehandler, addSaksbehandler, resetSaksbehandlerSok, removeSaksbehandler,
} from './duck';

interface TsProps {
    fetchAvdelingensSaksbehandlere: (avdelingEnhet: string) => void;
    findSaksbehandler: (brukerIdent: string) => Promise<string>;
    resetSaksbehandlerSok: () => void;
    addSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => Promise<string>;
    avdelingensSaksbehandlere: Saksbehandler[];
    removeSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => Promise<string>;
    valgtAvdelingEnhet: string;
}

/**
 * EndreSaksbehandlereIndex
 */
export class EndreSaksbehandlereIndex extends Component<TsProps> {
    static propTypes = {
      fetchAvdelingensSaksbehandlere: PropTypes.func.isRequired,
      findSaksbehandler: PropTypes.func.isRequired,
      addSaksbehandler: PropTypes.func.isRequired,
      resetSaksbehandlerSok: PropTypes.func.isRequired,
      removeSaksbehandler: PropTypes.func.isRequired,
      avdelingensSaksbehandlere: PropTypes.arrayOf(saksbehandlerPropType),
      valgtAvdelingEnhet: PropTypes.string.isRequired,
    };

    static defaultProps = {
      avdelingensSaksbehandlere: [],
    }

    componentDidMount = () => {
      const { fetchAvdelingensSaksbehandlere: fetchSaksbehandlere, valgtAvdelingEnhet } = this.props;
      fetchSaksbehandlere(valgtAvdelingEnhet);
    }

    render = () => {
      const {
        avdelingensSaksbehandlere, findSaksbehandler: finnSaksbehandler, addSaksbehandler: leggTilSaksbehandler, resetSaksbehandlerSok: reset,
        removeSaksbehandler: fjernSaksbehandler,
      } = this.props;
      return (
        <SaksbehandlerePanel
          saksbehandlere={avdelingensSaksbehandlere}
          finnSaksbehandler={finnSaksbehandler}
          resetSaksbehandlerSok={reset}
          leggTilSaksbehandler={leggTilSaksbehandler}
          fjernSaksbehandler={fjernSaksbehandler}
        />
      );
    }
}

const mapStateToProps = state => ({
  avdelingensSaksbehandlere: getAvdelingensSaksbehandlere(state),
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  ...bindActionCreators({
    fetchAvdelingensSaksbehandlere,
    findSaksbehandler,
    resetSaksbehandlerSok,
    addSaksbehandler,
    removeSaksbehandler,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(EndreSaksbehandlereIndex);
