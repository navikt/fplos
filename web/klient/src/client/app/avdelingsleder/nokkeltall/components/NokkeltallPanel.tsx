import React, {
  useRef, FunctionComponent,
} from 'react';

import { getValueFromLocalStorage } from 'utils/localStorageHelper';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import OppgaverForAvdeling from 'types/avdelingsleder/oppgaverForAvdelingTsType';
import OppgaveForDato from 'types/avdelingsleder/oppgaverForDatoTsType';
import OppgaverForForsteStonadsdag from 'types/avdelingsleder/oppgaverForForsteStonadsdagTsType';
import OppgaverManueltPaVent from 'types/avdelingsleder/oppgaverManueltPaVentTsType';
import OppgaverSomErApneEllerPaVent from 'types/avdelingsleder/oppgaverSomErApneEllerPaVentTsType';

import FordelingAvBehandlingstypePanel from './fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';
import TilBehandlingPanel from './tilBehandling/TilBehandlingPanel';
import ManueltPaVentPanel from './manueltSattPaVent/ManueltPaVentPanel';
import OppgaverPerForsteStonadsdagPanel from './antallBehandlingerPerForsteStonadsdag/OppgaverPerForsteStonadsdagPanel';
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
  const height = 300;

  const ref = useRef<HTMLDivElement>(null);

  return (
    <div ref={ref}>
      <TilBehandlingPanel
        height={height}
        oppgaverPerDato={oppgaverPerDato}
        getValueFromLocalStorage={getValueFromLocalStorage}
      />
      <VerticalSpacer twentyPx />
      <FordelingAvBehandlingstypePanel
        height={height}
        oppgaverForAvdeling={oppgaverForAvdeling}
        getValueFromLocalStorage={getValueFromLocalStorage}
      />
      <VerticalSpacer twentyPx />
      <ManueltPaVentPanel
        height={height}
        oppgaverManueltPaVent={oppgaverManueltPaVent}
        getValueFromLocalStorage={getValueFromLocalStorage}
      />
      <VerticalSpacer twentyPx />
      <OppgaverPerForsteStonadsdagPanel
        height={height}
        oppgaverPerForsteStonadsdag={oppgaverPerForsteStonadsdag}
      />
      <VerticalSpacer twentyPx />
      <OppgaverSomErApneEllerPaVentPanel
        height={height}
        oppgaverApneEllerPaVent={oppgaverApneEllerPaVent}
        getValueFromLocalStorage={getValueFromLocalStorage}
      />
    </div>
  );
};

export default NokkeltallPanel;
