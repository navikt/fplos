import React, { FunctionComponent } from 'react';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';

import SaksbehandlerNokkeltallPanel from './components/SaksbehandlerNokkeltallPanel';
import NyeOgFerdigstilteOppgaver from './nyeOgFerdigstilteOppgaverTsType';

interface OwnProps {
  valgtSakslisteId: number;
}

/**
 * SaksbehandlerNokkeltallIndex
 */
const SaksbehandlerNokkeltallIndex: FunctionComponent<OwnProps> = ({
  valgtSakslisteId,
}) => {
  const { data: nyeOgFerdigstilteOppgaver } = restApiHooks.useRestApi<NyeOgFerdigstilteOppgaver[]>(
    RestApiPathsKeys.HENT_NYE_OG_FERDIGSTILTE_OPPGAVER, { sakslisteId: valgtSakslisteId }, {
      updateTriggers: [valgtSakslisteId],
    },
  );

  return (
    <SaksbehandlerNokkeltallPanel nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver} />
  );
};

export default SaksbehandlerNokkeltallIndex;
