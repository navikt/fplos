import React, { FunctionComponent, useCallback } from 'react';
import { FormattedMessage, useIntl } from 'react-intl';
import { useForm } from 'react-hook-form';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { Undertittel } from 'nav-frontend-typografi';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import Oppgave from 'types/saksbehandler/oppgaveTsType';
import {
  hasValidText, maxLength, minLength, required,
} from '@navikt/ft-form-validators';
import { Form, TextAreaField } from '@navikt/ft-form-hooks';
import Modal from 'app/Modal';
import styles from './opphevReservasjonModal.less';

const minLength3 = minLength(3);
const maxLength500 = maxLength(500);

type FormValues = {
  begrunnelse: string;
}

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
const OpphevReservasjonModal: FunctionComponent<OwnProps> = ({
  showModal,
  cancel,
  oppgave,
  toggleMenu,
  hentReserverteOppgaver,
}) => {
  const intl = useIntl();
  const { startRequest: opphevOppgavereservasjon } = restApiHooks.useRestApiRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON);

  const opphevReservasjonFn = useCallback((begrunnelse: string) => opphevOppgavereservasjon({ oppgaveId: oppgave.id, begrunnelse })
    .then(() => {
      toggleMenu();
      hentReserverteOppgaver({}, true);
    }),
  [oppgave.id]);

  const formMethods = useForm<FormValues>();

  return (
    <Modal
      className={styles.modal}
      isOpen={showModal}
      closeButton={false}
      contentLabel={intl.formatMessage({ id: 'OpphevReservasjonModal.Begrunnelse' })}
      onRequestClose={cancel}
    >
      <Form<FormValues> formMethods={formMethods} onSubmit={(values) => opphevReservasjonFn(values.begrunnelse)}>
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
      </Form>
    </Modal>
  );
};

export default OpphevReservasjonModal;
