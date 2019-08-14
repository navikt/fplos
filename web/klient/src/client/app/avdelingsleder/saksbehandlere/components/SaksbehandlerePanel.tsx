import React from 'react';
import PropTypes from 'prop-types';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { Saksbehandler } from '../saksbehandlerTsType';
import saksbehandlerPropType from '../saksbehandlerPropType';
import LeggTilSaksbehandlerForm from './LeggTilSaksbehandlerForm';
import SaksbehandlereTabell from './SaksbehandlereTabell';

interface TsProps {
  saksbehandlere: Saksbehandler[];
  finnSaksbehandler: (brukerIdent: string) => Promise<string>;
  resetSaksbehandlerSok: () => void;
  leggTilSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => Promise<string>;
  fjernSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => Promise<string>;
}

/**
 * SaksbehandlerePanel
 */
const SaksbehandlerePanel = ({
  saksbehandlere,
  finnSaksbehandler,
  resetSaksbehandlerSok,
  leggTilSaksbehandler,
  fjernSaksbehandler,
}: TsProps) => (
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

SaksbehandlerePanel.propTypes = {
  saksbehandlere: PropTypes.arrayOf(saksbehandlerPropType).isRequired,
  finnSaksbehandler: PropTypes.func.isRequired,
  resetSaksbehandlerSok: PropTypes.func.isRequired,
  leggTilSaksbehandler: PropTypes.func.isRequired,
  fjernSaksbehandler: PropTypes.func.isRequired,
};

export default SaksbehandlerePanel;
