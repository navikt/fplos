import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';
import { Button, BodyShort } from '@navikt/ds-react';

import { Image } from '@navikt/ft-ui-komponenter';
import Modal from 'app/Modal';

import advarselImageUrl from 'images/advarsel.svg';

import styles from './behandlingPollingTimoutModal.less';

/**
 * BehandlingPollingTimoutModal
 *
 * Presentasjonskomponent. Modal som vises når en har pollet etter behandlinger et gitt antall ganger (uten oppdateringer)
 */
const BehandlingPollingTimoutModal: FunctionComponent<WrappedComponentProps> = ({
  intl,
}) => (
  <Modal
    className={styles.modal}
    open
    closeButton={false}
    aria-label={intl.formatMessage({ id: 'BehandlingPollingTimoutModal.TimeoutMelding' })}
    onClose={() => window.location.reload()}
  >
    <Row>
      <Column xs="1">
        <Image
          className={styles.image}
          alt={intl.formatMessage({ id: 'BehandlingPollingTimoutModal.TimeoutMelding' })}
          src={advarselImageUrl}
        />
        <div className={styles.divider} />
      </Column>
      <Column xs="9" className={styles.text}>
        <BodyShort size="small"><FormattedMessage id="BehandlingPollingTimoutModal.TimeoutMelding" /></BodyShort>
      </Column>
    </Row>
    <Row>
      <Column xs="7" />
      <Column xs="5">
        <Button
          className={styles.submitButton}
          size="small"
          variant="secondary"
          onClick={() => window.location.reload()}
          autoFocus
        >
          {intl.formatMessage({ id: 'BehandlingPollingTimoutModal.Oppfrisk' })}
        </Button>
      </Column>
    </Row>
  </Modal>
);

export default injectIntl(BehandlingPollingTimoutModal);
