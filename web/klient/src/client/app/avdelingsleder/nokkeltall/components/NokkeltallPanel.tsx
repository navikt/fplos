import React, {
  useState, useRef, FunctionComponent, useEffect, useCallback,
} from 'react';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import FordelingAvBehandlingstypePanel from './fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';
import TilBehandlingPanel from './tilBehandling/TilBehandlingPanel';
import ManueltPaVentPanel from './manueltSattPaVent/ManueltPaVentPanel';
import OppgaverPerForsteStonadsdagPanel from './antallBehandlingerPerForsteStonadsdag/OppgaverPerForsteStonadsdagPanel';

/**
 * NokkeltallPanel.
 */
const NokkeltallPanel: FunctionComponent = () => {
  const [width, setWidth] = useState(0);
  const height = 200;

  const ref = useRef(null);

  const oppdaterGrafStorrelse = useCallback(() => {
    if (ref.current) {
      const rect = ref.current.getBoundingClientRect();
      setWidth(rect.width);
    }
  }, [ref.current]);

  useEffect(() => {
    oppdaterGrafStorrelse();
    window.addEventListener('resize', oppdaterGrafStorrelse);

    return () => {
      window.removeEventListener('resize', oppdaterGrafStorrelse);
    };
  }, []);

  return (
    <div ref={ref}>
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
};

export default NokkeltallPanel;
