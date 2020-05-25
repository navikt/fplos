import React, {
  useState, useRef, FunctionComponent, useEffect, useCallback,
} from 'react';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import NyeOgFerdigstilteOppgaverForIdagPanel from './nyeOgFerdigstilteOppgaverForIdag/NyeOgFerdigstilteOppgaverForIdagPanel';
import NyeOgFerdigstilteOppgaverForSisteSyvPanel from './nyeOgFerdigstilteOppgaverForSisteSyv/NyeOgFerdigstilteOppgaverForSisteSyvPanel';

interface StateProps {
  width: number;
  height: number;
}

/**
 * SaksbehandlerNokkeltallPanel.
 */
const SaksbehandlerNokkeltallPanel: FunctionComponent = () => {
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
      <NyeOgFerdigstilteOppgaverForIdagPanel
        width={width}
        height={height}
      />
      <VerticalSpacer sixteenPx />
      <NyeOgFerdigstilteOppgaverForSisteSyvPanel
        width={width}
        height={height}
      />
    </div>
  );
};

export default SaksbehandlerNokkeltallPanel;
