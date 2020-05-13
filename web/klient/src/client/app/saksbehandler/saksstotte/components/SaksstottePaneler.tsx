
import React, { FunctionComponent } from 'react';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Oppgave from '../../oppgaveTsType';
import SistBehandledeSaker from './SistBehandledeSaker';
import SaksbehandlerNokkeltallIndex from '../nokkeltall/SaksbehandlerNokkeltallIndex';

interface OwnProps {
  sistBehandledeSaker: Oppgave[];
  valgtSakslisteId?: number;
}

/**
 * SaksstottePaneler
 */
const SaksstottePaneler: FunctionComponent<OwnProps> = ({
  sistBehandledeSaker,
  valgtSakslisteId,
}) => (
  <>
    <SistBehandledeSaker sistBehandledeSaker={sistBehandledeSaker} />
    <VerticalSpacer twentyPx />
    {valgtSakslisteId
      && <SaksbehandlerNokkeltallIndex valgtSakslisteId={valgtSakslisteId} />}
  </>
);

export default SaksstottePaneler;
