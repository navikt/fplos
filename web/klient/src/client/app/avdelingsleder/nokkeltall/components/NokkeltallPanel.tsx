import React, { Component } from 'react';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import FordelingAvBehandlingstypePanel from './fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';
import TilBehandlingPanel from './tilBehandling/TilBehandlingPanel';
import ManueltPaVentPanel from './manueltSattPaVent/ManueltPaVentPanel';
import OppgaverPerForsteStonadsdagPanel from './antallBehandlingerPerForsteStonadsdag/OppgaverPerForsteStonadsdagPanel';

interface StateTsProps {
  width: number;
  height: number;
}

/**
 * NokkeltallPanel.
 */
class NokkeltallPanel extends Component<{}, StateTsProps> {
  node: any

  constructor(props: {}) {
    super(props);

    this.state = {
      width: 0,
      height: 200,
    };
  }

  componentDidMount = () => {
    this.oppdaterGrafStorrelse();
    window.addEventListener('resize', this.oppdaterGrafStorrelse);
  }

  componentWillUnmount = () => {
    window.removeEventListener('resize', this.oppdaterGrafStorrelse);
  }

  oppdaterGrafStorrelse = () => {
    if (this.node) {
      const rect = this.node.getBoundingClientRect();
      this.setState({ width: rect.width });
    }
  }

  render = () => {
    const {
      width, height,
    } = this.state;

    return (
      <div ref={(node) => { this.node = node; }}>
        <TilBehandlingPanel
          width={width}
          height={height}
        />
        <VerticalSpacer twentyPx />
        <FordelingAvBehandlingstypePanel
          width={width}
          height={height}
        />
        <VerticalSpacer twentyPx />
        <ManueltPaVentPanel
          width={width}
          height={height}
        />
        <VerticalSpacer twentyPx />
        <OppgaverPerForsteStonadsdagPanel
          width={width}
          height={height}
        />
      </div>
    );
  }
}

export default NokkeltallPanel;
