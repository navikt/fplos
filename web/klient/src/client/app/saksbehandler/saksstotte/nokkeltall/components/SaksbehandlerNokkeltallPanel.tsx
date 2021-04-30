import React, {
  useState, useRef, FunctionComponent, useEffect, useCallback,
} from 'react';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import NyeOgFerdigstilteOppgaver from 'types/saksbehandler/nyeOgFerdigstilteOppgaverTsType';

import NyeOgFerdigstilteOppgaverForIdagPanel from './nyeOgFerdigstilteOppgaverForIdag/NyeOgFerdigstilteOppgaverForIdagPanel';
import NyeOgFerdigstilteOppgaverForSisteSyvPanel from './nyeOgFerdigstilteOppgaverForSisteSyv/NyeOgFerdigstilteOppgaverForSisteSyvPanel';

interface OwnProps {
  nyeOgFerdigstilteOppgaver?: NyeOgFerdigstilteOppgaver[];
}

/**
 * SaksbehandlerNokkeltallPanel.
 */
const SaksbehandlerNokkeltallPanel: FunctionComponent<OwnProps> = ({
  nyeOgFerdigstilteOppgaver,
}) => {
  const [width, setWidth] = useState(0);
  const HEIGHT = 200;

  const ref = useRef<HTMLDivElement>(null);

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
  }, [oppdaterGrafStorrelse]);

  return (
    <div ref={ref}>
      <NyeOgFerdigstilteOppgaverForIdagPanel
        width={width}
        height={HEIGHT}
        nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
      />
      <VerticalSpacer sixteenPx />
      <NyeOgFerdigstilteOppgaverForSisteSyvPanel
        width={width}
        height={HEIGHT}
        nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
      />
    </div>
  );
};

export default SaksbehandlerNokkeltallPanel;
