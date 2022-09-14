import React, { FunctionComponent, useCallback } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';
import { Button, BodyShort } from '@navikt/ds-react';

import { getDateAndTime } from '@navikt/ft-utils';
import OppgaveStatus from 'types/saksbehandler/oppgaveStatusTsType';
import Oppgave from 'types/saksbehandler/oppgaveTsType';
import { Image } from '@navikt/ft-ui-komponenter';
import Modal from 'app/Modal';

import advarselImageUrl from 'images/advarsel.svg';

import styles from './oppgaveErReservertAvAnnenModal.less';

type OwnProps = Readonly<{
  lukkErReservertModalOgOpneOppgave: (oppgave: Oppgave) => void;
  oppgave: Oppgave;
  oppgaveStatus: OppgaveStatus;
}>

/**
 * OppgaveErReservertAvAnnenModal
 *
 * Presentasjonskomponent. Modal som vises når en åpner oppgave som er reservert av en annen saksbehandler
 */
const OppgaveErReservertAvAnnenModal: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  lukkErReservertModalOgOpneOppgave,
  oppgave,
  oppgaveStatus,
}) => {
  const lukk = useCallback(() => lukkErReservertModalOgOpneOppgave(oppgave), [oppgave.id]);
  return (
    <Modal
      className={styles.modal}
      open
      closeButton={false}
      aria-label={intl.formatMessage({ id: 'OppgaveErReservertAvAnnenModal.ReservertAvEnkel' })}
      onClose={lukk}
    >
      <Row>
        <Column xs="1">
          <Image
            className={styles.image}
            alt={intl.formatMessage({ id: 'OppgaveErReservertAvAnnenModal.ReservertAvEnkel' })}
            src={advarselImageUrl}
          />
          <div className={styles.divider} />
        </Column>
        <Column xs="8" className={styles.text}>
          <BodyShort size="small">
            <FormattedMessage
              id="OppgaveErReservertAvAnnenModal.ReservertAv"
              values={{
                saksbehandlernavn: oppgaveStatus.reservertAvNavn,
                saksbehandlerid: oppgaveStatus.reservertAvUid,
                ...getDateAndTime(oppgaveStatus.reservertTilTidspunkt),
              }}
            />
          </BodyShort>
        </Column>
        <Column xs="2">
          <Button
            className={styles.okButton}
            size="small"
            variant="primary"
            onClick={lukk}
            autoFocus
          >
            {intl.formatMessage({ id: 'OppgaveErReservertAvAnnenModal.Ok' })}
          </Button>
        </Column>
      </Row>
    </Modal>
  );
};

export default injectIntl(OppgaveErReservertAvAnnenModal);
