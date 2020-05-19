import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import { fetchNyeOgFerdigstilteOppgaverNokkeltall } from './duck';
import SaksbehandlerNokkeltallPanel from './components/SaksbehandlerNokkeltallPanel';

interface OwnProps {
  fetchNyeOgFerdigstilteOppgaverNokkeltall: (sakslisteId: number) => void;
  valgtSakslisteId: number;
}

/**
 * SaksbehandlerNokkeltallIndex
 */
export class SaksbehandlerNokkeltallIndex extends Component<OwnProps> {
  componentDidMount = () => {
    const {
      fetchNyeOgFerdigstilteOppgaverNokkeltall: fetchNyeOgFerdige, valgtSakslisteId,
    } = this.props;
    fetchNyeOgFerdige(valgtSakslisteId);
  }

  componentDidUpdate = (prevProps: OwnProps) => {
    const {
      fetchNyeOgFerdigstilteOppgaverNokkeltall: fetchNyeOgFerdige, valgtSakslisteId,
    } = this.props;
    if (prevProps.valgtSakslisteId !== valgtSakslisteId) {
      fetchNyeOgFerdige(valgtSakslisteId);
    }
  }

  render = () => (
    <SaksbehandlerNokkeltallPanel />
  )
}

const mapStateToProps = () => ({
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  ...bindActionCreators({
    fetchNyeOgFerdigstilteOppgaverNokkeltall,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(SaksbehandlerNokkeltallIndex);
