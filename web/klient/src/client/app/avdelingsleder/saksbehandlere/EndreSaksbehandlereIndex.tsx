import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

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
  findSaksbehandler: (brukerIdent: string) => Promise<string>;
  resetSaksbehandlerSok: () => void;
  addSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => Promise<string>;
  removeSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => Promise<string>;
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
        removeSaksbehandler: fjernSaksbehandler, valgtAvdelingEnhet,
      } = this.props;
      return (
        <SaksbehandlerePanel
          saksbehandlere={avdelingensSaksbehandlere}
          finnSaksbehandler={finnSaksbehandler}
          resetSaksbehandlerSok={reset}
          leggTilSaksbehandler={leggTilSaksbehandler}
          fjernSaksbehandler={fjernSaksbehandler}
          valgtAvdelingEnhet={valgtAvdelingEnhet}
        />
      );
    }
}

const mapStateToProps = (state) => ({
  avdelingensSaksbehandlere: getAvdelingensSaksbehandlere(state),
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    fetchAvdelingensSaksbehandlere,
    findSaksbehandler,
    resetSaksbehandlerSok,
    addSaksbehandler,
    removeSaksbehandler,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(EndreSaksbehandlereIndex);
