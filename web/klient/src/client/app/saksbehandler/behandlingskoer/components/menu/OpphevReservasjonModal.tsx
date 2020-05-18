import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';

import { Form } from 'react-final-form';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { Undertittel } from 'nav-frontend-typografi';

import Oppgave from 'saksbehandler/oppgaveTsType';
import {
  hasValidText, maxLength, minLength, required,
} from 'utils/validation/validators';
import { TextAreaField } from 'form/FinalFields';
import Modal from 'sharedComponents/Modal';

import styles from './opphevReservasjonModal.less';

const minLength3 = minLength(3);
const maxLength500 = maxLength(500);

type OwnProps = Readonly<{
  showModal: boolean;
  oppgave: Oppgave;
  cancel: () => void;
  submit: (oppgaveId: number, begrunnelse: string) => void;
}>;

/**
 * OpphevReservasjonModal
 *
 * Presentasjonskomponent. Modal som lar en begrunne hvorfor en sak skal frigjøres.
 */
export const OpphevReservasjonModal: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  showModal,
  cancel,
  submit,
  oppgave,
}) => (
  <Modal
    className={styles.modal}
    isOpen={showModal}
    closeButton={false}
    contentLabel={intl.formatMessage({ id: 'OpphevReservasjonModal.Begrunnelse' })}
    onRequestClose={cancel}
  >
    <Form
      onSubmit={(values) => submit(oppgave.id, values.begrunnelse)}
      render={({ handleSubmit }) => (
        <form onSubmit={handleSubmit}>
          <Undertittel><FormattedMessage id="OpphevReservasjonModal.Begrunnelse" /></Undertittel>
          <TextAreaField
            name="begrunnelse"
            label={intl.formatMessage({ id: 'OpphevReservasjonModal.Hjelpetekst' })}
            validate={[required, maxLength500, minLength3, hasValidText]}
            maxLength={500}
          />
          <Hovedknapp
            className={styles.submitButton}
            mini
            htmlType="submit"
            autoFocus
          >
            {intl.formatMessage({ id: 'OpphevReservasjonModal.Ok' })}
          </Hovedknapp>
          <Knapp
            className={styles.cancelButton}
            mini
            htmlType="reset"
            onClick={cancel}
          >
            {intl.formatMessage({ id: 'OpphevReservasjonModal.Avbryt' })}
          </Knapp>
        </form>
      )}
    />
  </Modal>
);

export default injectIntl(OpphevReservasjonModal);
