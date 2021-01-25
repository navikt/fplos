
import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';
import { Normaltekst } from 'nav-frontend-typografi';
import { Hovedknapp } from 'nav-frontend-knapper';

import { getDateAndTime } from 'utils/dateUtils';
import Modal from 'sharedComponents/Modal';
import Image from 'sharedComponents/Image';
import Oppgave from 'saksbehandler/oppgaveTsType';

import innvilgetImageUrl from 'images/sharedComponents/innvilget_valgt.svg';

import styles from './oppgaveReservasjonForlengetModal.less';

interface OwnProps {
  oppgave: Oppgave;
  showModal: boolean;
  closeModal: (event: React.MouseEvent<HTMLButtonElement>) => void;
}

/**
 * OppgaveReservasjonForlengetModal.
 */
export const OppgaveReservasjonForlengetModal: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  oppgave,
  showModal,
  closeModal,
}) => (
  <Modal
    className={styles.modal}
    isOpen={showModal}
    closeButton={false}
    contentLabel={intl.formatMessage({ id: 'OppgaveReservasjonForlengetModal.Reservert' })}
    onRequestClose={closeModal as () => void}
  >
    <Row>
      <Column xs="1">
        <Image
          className={styles.image}
          alt={intl.formatMessage({ id: 'OppgaveReservasjonForlengetModal.Reservert' })}
          src={innvilgetImageUrl}
        />
        <div className={styles.divider} />
      </Column>
      <Column xs="9">
        <Normaltekst>
          <FormattedMessage id="OppgaveReservasjonForlengetModal.Reservert" />
        </Normaltekst>
        <Normaltekst>
          <FormattedMessage id="OppgaveReservasjonForlengetModal.Til" values={getDateAndTime(oppgave.status.reservertTilTidspunkt)} />
        </Normaltekst>
      </Column>
      <Column xs="2">
        <Hovedknapp
          mini
          className={styles.button}
          onClick={closeModal}
          autoFocus
        >
          {intl.formatMessage({ id: 'OppgaveReservasjonForlengetModal.Ok' })}
        </Hovedknapp>
      </Column>

    </Row>
  </Modal>
);

export default injectIntl(OppgaveReservasjonForlengetModal);
