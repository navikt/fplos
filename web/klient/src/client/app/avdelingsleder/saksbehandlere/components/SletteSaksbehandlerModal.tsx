import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';
import { Button, BodyShort } from '@navikt/ds-react';

import { Image } from '@navikt/ft-ui-komponenter';
import Modal from 'app/Modal';
import Saksbehandler from 'types/avdelingsleder/saksbehandlerAvdelingTsType';

import advarselImageUrl from 'images/advarsel.svg';

import styles from './sletteSaksbehandlerModal.less';

type OwnProps = Readonly<{
  valgtSaksbehandler: Saksbehandler;
  closeSletteModal: () => void;
  fjernSaksbehandler: (saksbehandler: Saksbehandler) => void;
}>;

/**
 * SletteSaksbehandlerModal
 *
 * Presentasjonskomponent. Modal som lar en avdelingsleder fjerne tilgjengelige saksbehandlere.
 */
const SletteSaksbehandlerModal: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtSaksbehandler,
  closeSletteModal,
  fjernSaksbehandler,
}) => (
  <Modal
    className={styles.modal}
    closeButton={false}
    open
    aria-label={intl.formatMessage({ id: 'SletteSaksbehandlerModal.SletteModal' })}
    onClose={closeSletteModal}
  >
    <Row>
      <Column xs="1">
        <Image
          className={styles.image}
          alt={intl.formatMessage({ id: 'SletteSaksbehandlerModal.SletteModal' })}
          src={advarselImageUrl}
        />
        <div className={styles.divider} />
      </Column>
      <Column xs="6" className={styles.text}>
        <BodyShort size="small">
          <FormattedMessage id="SletteSaksbehandlerModal.SletteSaksbehandler" values={{ saksbehandlerNavn: valgtSaksbehandler.navn }} />
        </BodyShort>
      </Column>
      <Column xs="4">
        <Button
          className={styles.submitButton}
          size="small"
          variant="primary"
          onClick={() => fjernSaksbehandler(valgtSaksbehandler)}
          autoFocus
        >
          {intl.formatMessage({ id: 'SletteSaksbehandlerModal.Ja' })}
        </Button>
        <Button
          className={styles.cancelButton}
          size="small"
          variant="secondary"
          onClick={closeSletteModal}
        >
          {intl.formatMessage({ id: 'SletteSaksbehandlerModal.Nei' })}
        </Button>
      </Column>
    </Row>
  </Modal>
);

export default injectIntl(SletteSaksbehandlerModal);
