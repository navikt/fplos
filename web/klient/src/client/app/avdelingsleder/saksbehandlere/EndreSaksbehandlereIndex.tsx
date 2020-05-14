import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import { getValgtAvdelingEnhet } from 'app/duck';
import Saksbehandler from './saksbehandlerTsType';
import SaksbehandlerePanel from './components/SaksbehandlerePanel';
import {
  fetchAvdelingensSaksbehandlere, getAvdelingensSaksbehandlere, findSaksbehandler, addSaksbehandler, resetSaksbehandlerSok, removeSaksbehandler,
} from './duck';

interface OwnProps {
  avdelingensSaksbehandlere: Saksbehandler[];
  valgtAvdelingEnhet: string;
}

interface DispatchProps {
  fetchAvdelingensSaksbehandlere: (avdelingEnhet: string) => void;
  findSaksbehandler: (brukerIdent: string) => (dispatch: Dispatch) => Promise<string>;
  resetSaksbehandlerSok: () => void;
  addSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => (dispatch: Dispatch) => Promise<string>;
  removeSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => (dispatch: Dispatch) => Promise<string>;
}

/**
 * EndreSaksbehandlereIndex
 */
export class EndreSaksbehandlereIndex extends Component<OwnProps & DispatchProps> {
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

const mapStateToProps = (state) => ({
  avdelingensSaksbehandlere: getAvdelingensSaksbehandlere(state),
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators({
    fetchAvdelingensSaksbehandlere,
    findSaksbehandler,
    resetSaksbehandlerSok,
    addSaksbehandler,
    removeSaksbehandler,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(EndreSaksbehandlereIndex);
