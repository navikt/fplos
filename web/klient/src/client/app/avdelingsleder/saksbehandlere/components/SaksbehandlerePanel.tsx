import React, { FunctionComponent } from 'react';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Saksbehandler from '../saksbehandlerTsType';
import LeggTilSaksbehandlerForm from './LeggTilSaksbehandlerForm';
import SaksbehandlereTabell from './SaksbehandlereTabell';

interface OwnProps {
  saksbehandlere: Saksbehandler[];
  finnSaksbehandler: (brukerIdent: string) => Promise<string>;
  resetSaksbehandlerSok: () => void;
  leggTilSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => Promise<string>;
  fjernSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => Promise<string>;
  valgtAvdelingEnhet: string;
}

/**
 * SaksbehandlerePanel
 */
const SaksbehandlerePanel: FunctionComponent<OwnProps> = ({
  saksbehandlere,
  finnSaksbehandler,
  resetSaksbehandlerSok,
  leggTilSaksbehandler,
  fjernSaksbehandler,
  valgtAvdelingEnhet,
}) => (
  <>
    <SaksbehandlereTabell saksbehandlere={saksbehandlere} fjernSaksbehandler={fjernSaksbehandler} valgtAvdelingEnhet={valgtAvdelingEnhet} />
    <VerticalSpacer sixteenPx />
    <LeggTilSaksbehandlerForm
      finnSaksbehandler={finnSaksbehandler}
      leggTilSaksbehandler={leggTilSaksbehandler}
      resetSaksbehandlerSok={resetSaksbehandlerSok}
      valgtAvdelingEnhet={valgtAvdelingEnhet}
    />
  </>
);

export default SaksbehandlerePanel;
