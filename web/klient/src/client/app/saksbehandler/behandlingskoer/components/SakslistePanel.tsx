
import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Undertittel } from 'nav-frontend-typografi';

import Saksliste from 'saksbehandler/behandlingskoer/sakslisteTsType';
import Oppgave from 'saksbehandler/oppgaveTsType';
import { useRestApiRunner } from 'data/rest-api-hooks';
import { RestApiPathsKeys } from 'data/restApiPaths';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import SakslisteVelgerForm from './SakslisteVelgerForm';
import OppgaverTabell from './OppgaverTabell';

import styles from './sakslistePanel.less';

interface OwnProps {
  sakslister: Saksliste[];
  fetchSakslisteOppgaver: (sakslisteId: number) => void;
  reserverOppgave: (oppgaveId: Oppgave) => void;
  reserverteOppgaver: Oppgave[];
  oppgaverTilBehandling: Oppgave[];
  hentReserverteOppgaver: (params: any, keepData: boolean) => void;
}

/**
 * SakslistePanel
 */
const SakslistePanel: FunctionComponent<OwnProps> = ({
  reserverOppgave,
  sakslister,
  fetchSakslisteOppgaver,
  reserverteOppgaver,
  oppgaverTilBehandling,
  hentReserverteOppgaver,
}) => {
  const { startRequest: fetchAntallOppgaver, data: antallOppgaver } = useRestApiRunner<number>(RestApiPathsKeys.BEHANDLINGSKO_OPPGAVE_ANTALL);

  return (
    <>
      <Undertittel><FormattedMessage id="SakslistePanel.StartBehandling" /></Undertittel>
      <div className={styles.container}>
        <SakslisteVelgerForm
          sakslister={sakslister}
          fetchSakslisteOppgaver={fetchSakslisteOppgaver}
          fetchAntallOppgaver={fetchAntallOppgaver}
        />
        <VerticalSpacer twentyPx />
        <OppgaverTabell
          reserverOppgave={reserverOppgave}
          reserverteOppgaver={reserverteOppgaver}
          oppgaverTilBehandling={oppgaverTilBehandling}
          antallOppgaver={antallOppgaver}
          hentReserverteOppgaver={hentReserverteOppgaver}
        />
      </div>
    </>
  );
};

export default SakslistePanel;
