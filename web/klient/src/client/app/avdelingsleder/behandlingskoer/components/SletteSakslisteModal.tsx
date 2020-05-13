import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { Normaltekst } from 'nav-frontend-typografi';

import Image from 'sharedComponents/Image';
import Modal from 'sharedComponents/Modal';

import advarselImageUrl from 'images/advarsel.svg';
import { Saksliste } from '../sakslisteTsType';

import styles from './sletteSakslisteModal.less';

type OwnProps = Readonly<{
  intl: any;
  valgtSaksliste: Saksliste;
  cancel: () => void;
  submit: (saksliste: Saksliste) => void;
}>;

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
    isOpen
    contentLabel={intl.formatMessage({ id: 'SletteSakslisteModal.SletteModal' })}
    onRequestClose={cancel}
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
        <Normaltekst>
          <FormattedMessage id="SletteSakslisteModal.SletteSaksliste" values={{ sakslisteNavn: valgtSaksliste.navn }} />
        </Normaltekst>
      </Column>
      <Column xs="4">
        <Hovedknapp
          className={styles.submitButton}
          mini
          htmlType="submit"
          onClick={() => submit(valgtSaksliste)}
          autoFocus
        >
          {intl.formatMessage({ id: 'SletteSakslisteModal.Ja' })}
        </Hovedknapp>
        <Knapp
          className={styles.cancelButton}
          mini
          htmlType="reset"
          onClick={cancel}
        >
          {intl.formatMessage({ id: 'SletteSakslisteModal.Nei' })}
        </Knapp>
      </Column>
    </Row>
  </Modal>
);

export default injectIntl(SletteSakslisteModal);
