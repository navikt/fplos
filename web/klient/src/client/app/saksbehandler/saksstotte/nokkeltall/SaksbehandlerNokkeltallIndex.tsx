import React, { FunctionComponent } from 'react';

import { RestApiPathsKeys } from 'data/restApiPaths';
import useRestApi from 'data/rest-api-hooks/useRestApi';

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
  const { data: nyeOgFerdigstilteOppgaver } = useRestApi<NyeOgFerdigstilteOppgaver[]>(
    RestApiPathsKeys.HENT_NYE_OG_FERDIGSTILTE_OPPGAVER, { sakslisteId: valgtSakslisteId }, [valgtSakslisteId],
  );

  return (
    <SaksbehandlerNokkeltallPanel nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver} />
  );
};

export default SaksbehandlerNokkeltallIndex;
