import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';
import { Button, BodyShort } from '@navikt/ds-react';

import { getDateAndTime } from '@navikt/ft-utils';
import { Image } from '@navikt/ft-ui-komponenter';
import Modal from 'app/Modal';
import Oppgave from 'types/saksbehandler/oppgaveTsType';

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
    open={showModal}
    closeButton={false}
    aria-label={intl.formatMessage({ id: 'OppgaveReservasjonForlengetModal.Reservert' })}
    onClose={closeModal as () => void}
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
        <BodyShort size="small">
          <FormattedMessage id="OppgaveReservasjonForlengetModal.Reservert" />
        </BodyShort>
        <BodyShort size="small">
          <FormattedMessage id="OppgaveReservasjonForlengetModal.Til" values={getDateAndTime(oppgave.status.reservertTilTidspunkt)} />
        </BodyShort>
      </Column>
      <Column xs="2">
        <Button
          size="small"
          variant="secondary"
          className={styles.button}
          onClick={closeModal}
          autoFocus
        >
          {intl.formatMessage({ id: 'OppgaveReservasjonForlengetModal.Ok' })}
        </Button>
      </Column>

    </Row>
  </Modal>
);

export default injectIntl(OppgaveReservasjonForlengetModal);
