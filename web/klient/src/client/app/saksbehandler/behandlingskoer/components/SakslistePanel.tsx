import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Undertittel } from 'nav-frontend-typografi';

import { getValueFromLocalStorage, setValueInLocalStorage, removeValueFromLocalStorage } from 'data/localStorageHelper';
import Saksliste from 'types/saksbehandler/sakslisteTsType';
import Oppgave from 'types/saksbehandler/oppgaveTsType';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import { VerticalSpacer } from '@navikt/ft-ui-komponenter';
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
  const { startRequest: fetchAntallOppgaver, data: antallOppgaver } = restApiHooks.useRestApiRunner(RestApiPathsKeys.BEHANDLINGSKO_OPPGAVE_ANTALL);

  return (
    <>
      <Undertittel><FormattedMessage id="SakslistePanel.StartBehandling" /></Undertittel>
      <div className={styles.container}>
        <SakslisteVelgerForm
          sakslister={sakslister}
          setValgtSakslisteId={setValgtSakslisteId}
          fetchAntallOppgaver={fetchAntallOppgaver}
          getValueFromLocalStorage={getValueFromLocalStorage}
          setValueInLocalStorage={setValueInLocalStorage}
          removeValueFromLocalStorage={removeValueFromLocalStorage}
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
