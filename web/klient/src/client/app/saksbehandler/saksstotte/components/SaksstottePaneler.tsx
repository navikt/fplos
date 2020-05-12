
import React from 'react';
import PropTypes from 'prop-types';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import oppgavePropType from '../../oppgavePropType';
import { Oppgave } from '../../oppgaveTsType';
import SistBehandledeSaker from './SistBehandledeSaker';
import SaksbehandlerNokkeltallIndex from '../nokkeltall/SaksbehandlerNokkeltallIndex';

interface TsProps {
  sistBehandledeSaker: Oppgave[];
  valgtSakslisteId?: number;
}

/**
 * SaksstottePaneler
 */
const SaksstottePaneler = ({
  sistBehandledeSaker,
  valgtSakslisteId,
}: TsProps) => (
  <>
    <SistBehandledeSaker sistBehandledeSaker={sistBehandledeSaker} />
    <VerticalSpacer twentyPx />
    {valgtSakslisteId
      && <SaksbehandlerNokkeltallIndex valgtSakslisteId={valgtSakslisteId} />}
  </>
);

SaksstottePaneler.propTypes = {
  sistBehandledeSaker: PropTypes.arrayOf(oppgavePropType).isRequired,
  valgtSakslisteId: PropTypes.number,
};

SaksstottePaneler.defaultProps = {
  valgtSakslisteId: undefined,
};

export default SaksstottePaneler;
