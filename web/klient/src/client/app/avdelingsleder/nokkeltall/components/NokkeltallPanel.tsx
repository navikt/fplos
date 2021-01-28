import React, {
  useState, useRef, FunctionComponent, useEffect, useCallback,
} from 'react';

import { getValueFromLocalStorage } from 'utils/localStorageHelper';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import FordelingAvBehandlingstypePanel from './fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';
import TilBehandlingPanel from './tilBehandling/TilBehandlingPanel';
import ManueltPaVentPanel from './manueltSattPaVent/ManueltPaVentPanel';
import OppgaverPerForsteStonadsdagPanel from './antallBehandlingerPerForsteStonadsdag/OppgaverPerForsteStonadsdagPanel';
import OppgaverForAvdeling from './fordelingAvBehandlingstype/oppgaverForAvdelingTsType';
import OppgaveForDato from './tilBehandling/oppgaverForDatoTsType';
import OppgaverForForsteStonadsdag from './antallBehandlingerPerForsteStonadsdag/oppgaverForForsteStonadsdagTsType';
import OppgaverManueltPaVent from './manueltSattPaVent/oppgaverManueltPaVentTsType';
import OppgaverSomErApneEllerPaVent from './apneOgPaVentBehandlinger/oppgaverSomErApneEllerPaVentTsType';
import OppgaverSomErApneEllerPaVentPanel from './apneOgPaVentBehandlinger/OppgaverSomErApneEllerPaVentPanel';

interface OwnProps {
  oppgaverForAvdeling: OppgaverForAvdeling[];
  oppgaverPerDato: OppgaveForDato[];
  oppgaverManueltPaVent: OppgaverManueltPaVent[];
  oppgaverPerForsteStonadsdag: OppgaverForForsteStonadsdag[];
  oppgaverApneEllerPaVent: OppgaverSomErApneEllerPaVent[];
}

/**
 * NokkeltallPanel.
 */
const NokkeltallPanel: FunctionComponent<OwnProps> = ({
  oppgaverForAvdeling,
  oppgaverPerDato,
  oppgaverManueltPaVent,
  oppgaverPerForsteStonadsdag,
  oppgaverApneEllerPaVent,
}) => {
  const [width, setWidth] = useState(0);
  const height = 200;

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
  }, []);

  return (
    <div ref={ref}>
      <TilBehandlingPanel
        width={width}
        height={height}
        oppgaverPerDato={oppgaverPerDato}
        getValueFromLocalStorage={getValueFromLocalStorage}
      />
      <VerticalSpacer twentyPx />
      <FordelingAvBehandlingstypePanel
        width={width}
        height={height}
        oppgaverForAvdeling={oppgaverForAvdeling}
        getValueFromLocalStorage={getValueFromLocalStorage}
      />
      <VerticalSpacer twentyPx />
      <ManueltPaVentPanel
        width={width}
        height={height}
        oppgaverManueltPaVent={oppgaverManueltPaVent}
        getValueFromLocalStorage={getValueFromLocalStorage}
      />
      <VerticalSpacer twentyPx />
      <OppgaverPerForsteStonadsdagPanel
        width={width}
        height={height}
        oppgaverPerForsteStonadsdag={oppgaverPerForsteStonadsdag}
      />
      <VerticalSpacer twentyPx />
      <OppgaverSomErApneEllerPaVentPanel
        width={width}
        height={height}
        oppgaverApneEllerPaVent={oppgaverApneEllerPaVent}
        getValueFromLocalStorage={getValueFromLocalStorage}
      />
    </div>
  );
};

export default NokkeltallPanel;
