
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
  valgtSakslisteId?: number;
  setValgtSakslisteId: (sakslisteId: number) => void;
  sakslister: Saksliste[];
  reserverOppgave: (oppgaveId: Oppgave) => void;
}

/**
 * SakslistePanel
 */
const SakslistePanel: FunctionComponent<OwnProps> = ({
  reserverOppgave,
  sakslister,
  setValgtSakslisteId,
  valgtSakslisteId,
}) => {
  const { startRequest: fetchAntallOppgaver, data: antallOppgaver } = useRestApiRunner<number>(RestApiPathsKeys.BEHANDLINGSKO_OPPGAVE_ANTALL);

  return (
    <>
      <Undertittel><FormattedMessage id="SakslistePanel.StartBehandling" /></Undertittel>
      <div className={styles.container}>
        <SakslisteVelgerForm
          sakslister={sakslister}
          setValgtSakslisteId={setValgtSakslisteId}
          fetchAntallOppgaver={fetchAntallOppgaver}
        />
        <VerticalSpacer twentyPx />
        {valgtSakslisteId && (
          <OppgaverTabell
            reserverOppgave={reserverOppgave}
            antallOppgaver={antallOppgaver}
            valgtSakslisteId={valgtSakslisteId}
          />
        )}
      </div>
    </>
  );
};

export default SakslistePanel;
