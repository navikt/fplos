import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';
import { Button, BodyShort } from '@navikt/ds-react';

import { Image } from '@navikt/ft-ui-komponenter';
import Modal from 'app/Modal';
import Saksliste from 'types/avdelingsleder/sakslisteAvdelingTsType';

import advarselImageUrl from 'images/advarsel.svg';

import styles from './sletteSakslisteModal.less';

interface OwnProps {
  intl: any;
  valgtSaksliste: Saksliste;
  cancel: () => void;
  submit: (saksliste: Saksliste) => void;
}

/**
 * SletteSakslisteModal
 *
 * Presentasjonskomponent. Modal som lar en avdelingsleder fjerne sakslister.
 */
export const SletteSakslisteModal: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtSaksliste,
  cancel,
  submit,
}) => (
  <Modal
    className={styles.modal}
    closeButton={false}
    open
    aria-label={intl.formatMessage({ id: 'SletteSakslisteModal.SletteModal' })}
    onClose={cancel}
  >
    <Row>
      <Column xs="1">
        <Image
          className={styles.image}
          alt={intl.formatMessage({ id: 'SletteSakslisteModal.SletteModal' })}
          src={advarselImageUrl}
        />
        <div className={styles.divider} />
      </Column>
      <Column xs="6" className={styles.text}>
        <BodyShort size="small">
          <FormattedMessage id="SletteSakslisteModal.SletteSaksliste" values={{ sakslisteNavn: valgtSaksliste.navn }} />
        </BodyShort>
      </Column>
      <Column xs="4">
        <Button
          className={styles.submitButton}
          size="small"
          variant="primary"
          onClick={() => submit(valgtSaksliste)}
          autoFocus
        >
          {intl.formatMessage({ id: 'SletteSakslisteModal.Ja' })}
        </Button>
        <Button
          className={styles.cancelButton}
          size="small"
          variant="secondary"
          onClick={cancel}
        >
          {intl.formatMessage({ id: 'SletteSakslisteModal.Nei' })}
        </Button>
      </Column>
    </Row>
  </Modal>
);

export default injectIntl(SletteSakslisteModal);
