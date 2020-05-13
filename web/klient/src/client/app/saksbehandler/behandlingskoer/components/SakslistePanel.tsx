
import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Undertittel } from 'nav-frontend-typografi';

import Saksliste from 'saksbehandler/behandlingskoer/sakslisteTsType';
import Oppgave from 'saksbehandler/oppgaveTsType';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import SakslisteVelgerForm from './SakslisteVelgerForm';
import OppgaverTabell from './OppgaverTabell';

import styles from './sakslistePanel.less';

interface OwnProps {
  sakslister: Saksliste[];
  fetchSakslisteOppgaver: (sakslisteId: number) => void;
  reserverOppgave: (oppgaveId: Oppgave) => void;
  opphevOppgaveReservasjon: (oppgaveId: number, begrunnelse: string) => Promise<string>;
  forlengOppgaveReservasjon: (oppgaveId: number) => Promise<string>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
}

/**
 * SakslistePanel
 */
const SakslistePanel: FunctionComponent<OwnProps> = ({
  reserverOppgave,
  opphevOppgaveReservasjon,
  forlengOppgaveReservasjon,
  endreOppgaveReservasjon,
  sakslister,
  fetchSakslisteOppgaver,
  flyttReservasjon,
}) => (
  <>
    <Undertittel><FormattedMessage id="SakslistePanel.StartBehandling" /></Undertittel>
    <div className={styles.container}>
      <SakslisteVelgerForm
        sakslister={sakslister}
        fetchSakslisteOppgaver={fetchSakslisteOppgaver}
      />
      <VerticalSpacer twentyPx />
      <OppgaverTabell
        reserverOppgave={reserverOppgave}
        opphevOppgaveReservasjon={opphevOppgaveReservasjon}
        forlengOppgaveReservasjon={forlengOppgaveReservasjon}
        endreOppgaveReservasjon={endreOppgaveReservasjon}
        flyttReservasjon={flyttReservasjon}
      />
    </div>
  </>
);

export default SakslistePanel;
