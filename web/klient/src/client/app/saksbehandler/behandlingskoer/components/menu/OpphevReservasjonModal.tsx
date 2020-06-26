import React, { FunctionComponent, useCallback } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Form } from 'react-final-form';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { Undertittel } from 'nav-frontend-typografi';

import { useRestApiRunner } from 'data/rest-api-hooks';
import { RestApiPathsKeys } from 'data/restApiPaths';
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
  toggleMenu: () => void;
  hentReserverteOppgaver: (params: any, keepData: boolean) => void;
}>;

/**
 * OpphevReservasjonModal
 *
 * Presentasjonskomponent. Modal som lar en begrunne hvorfor en sak skal frigjøres.
 */
const OpphevReservasjonModal: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  showModal,
  cancel,
  oppgave,
  toggleMenu,
  hentReserverteOppgaver,
}) => {
  const { startRequest: opphevOppgavereservasjon } = useRestApiRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON);

  const opphevReservasjonFn = useCallback((begrunnelse: string) => opphevOppgavereservasjon({ oppgaveId: oppgave.id, begrunnelse })
    .then(() => {
      toggleMenu();
      hentReserverteOppgaver({}, true);
    }),
  [oppgave.id]);

  return (
    <Modal
      className={styles.modal}
      isOpen={showModal}
      closeButton={false}
      contentLabel={intl.formatMessage({ id: 'OpphevReservasjonModal.Begrunnelse' })}
      onRequestClose={cancel}
    >
      <Form
        onSubmit={(values) => opphevReservasjonFn(values.begrunnelse)}
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
              <FormattedMessage id="OpphevReservasjonModal.Ok" />
            </Hovedknapp>
            <Knapp
              className={styles.cancelButton}
              mini
              htmlType="reset"
              onClick={cancel}
            >
              <FormattedMessage id="OpphevReservasjonModal.Avbryt" />
            </Knapp>
          </form>
        )}
      />
    </Modal>
  );
};

export default injectIntl(OpphevReservasjonModal);
