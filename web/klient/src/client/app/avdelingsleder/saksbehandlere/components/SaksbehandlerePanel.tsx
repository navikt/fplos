import React, { FunctionComponent } from 'react';
import { Dispatch } from 'redux';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Saksbehandler from '../saksbehandlerTsType';
import LeggTilSaksbehandlerForm from './LeggTilSaksbehandlerForm';
import SaksbehandlereTabell from './SaksbehandlereTabell';

interface OwnProps {
  saksbehandlere: Saksbehandler[];
  finnSaksbehandler: (brukerIdent: string) => (dispatch: Dispatch) => Promise<string>;
  resetSaksbehandlerSok: () => void;
  leggTilSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => (dispatch: Dispatch) => Promise<string>;
  fjernSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => (dispatch: Dispatch) => Promise<string>;
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
}) => (
  <>
    <SaksbehandlereTabell saksbehandlere={saksbehandlere} fjernSaksbehandler={fjernSaksbehandler} />
    <VerticalSpacer sixteenPx />
    <LeggTilSaksbehandlerForm
      finnSaksbehandler={finnSaksbehandler}
      leggTilSaksbehandler={leggTilSaksbehandler}
      resetSaksbehandlerSok={resetSaksbehandlerSok}
    />
  </>
);

export default SaksbehandlerePanel;
