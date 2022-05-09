import React, { MouseEvent, FunctionComponent, useCallback } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { useForm } from 'react-hook-form';
import { Column, Row } from 'nav-frontend-grid';
import { Knapp } from 'nav-frontend-knapper';
import Panel from 'nav-frontend-paneler';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import styles from 'saksbehandler/behandlingskoer/components/menu/oppgaveReservasjonEndringDatoModal.less';
import Modal from 'app/Modal';
import { dateAfterOrEqual, dateBeforeOrEqual, hasValidDate } from '@navikt/ft-form-validators';
import { Form, Datepicker } from '@navikt/ft-form-hooks';

const thirtyDaysFromNow = () => {
  const result = new Date();
  result.setDate(new Date().getDate() + 30);
  return result;
};

type FormValues = {
  reserverTil: string;
}

interface OwnProps {
  showModal: boolean;
  closeModal: (event: MouseEvent<HTMLButtonElement>) => void;
  reserverTilDefault?: string;
  oppgaveId: number;
  endreReserverasjonState: () => void;
  hentReserverteOppgaver: (params: any, keepData: boolean) => void;
}

/**
 * OppgaveReservasjonEndringDatoModal.
 */
const OppgaveReservasjonEndringDatoModal: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  showModal,
  closeModal,
  reserverTilDefault,
  oppgaveId,
  hentReserverteOppgaver,
  endreReserverasjonState,
}) => {
  const { startRequest: endreOppgavereservasjon } = restApiHooks.useRestApiRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON);

  const endreOppgaveReservasjonFn = useCallback((reserverTil: string) => endreOppgavereservasjon({ oppgaveId, reserverTil })
    .then(() => {
      endreReserverasjonState();
      hentReserverteOppgaver({}, true);
    }),
  []);

  const lagDefaultValues = useCallback((reserverTil?: string) => ({
    reserverTil: (reserverTil && reserverTil.length >= 10) ? reserverTil.substr(0, 10) : '',
  }), []);

  const søkFormMethods = useForm<FormValues>({
    defaultValues: lagDefaultValues(reserverTilDefault),
  });

  return (
    <Modal
      className={styles.modal}
      isOpen={showModal}
      closeButton={false}
      contentLabel={intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Header' })}
      onRequestClose={closeModal as () => void}
    >
      <Form<FormValues> formMethods={søkFormMethods} onSubmit={(values) => endreOppgaveReservasjonFn(values.reserverTil)}>
        <Panel className={styles.panel}>
          <h3>
            <FormattedMessage id="OppgaveReservasjonEndringDatoModal.Header" />
          </h3>
          <Datepicker
            label=""
            name="reserverTil"
            validate={[hasValidDate, dateAfterOrEqual(new Date()), dateBeforeOrEqual(thirtyDaysFromNow())]}
            disabledDays={{ before: new Date(), after: thirtyDaysFromNow() }}
          />
          <Row className={styles.buttonRow}>
            <Column>
              <div className={styles.buttonBox}>
                <Knapp
                  mini
                  className={styles.button}
                  autoFocus
                >
                  <FormattedMessage id="OppgaveReservasjonEndringDatoModal.Ok" />
                </Knapp>

                <Knapp
                  mini
                  className={styles.button}
                  onClick={closeModal}
                >
                  <FormattedMessage id="OppgaveReservasjonEndringDatoModal.Avbryt" />
                </Knapp>
              </div>
            </Column>
          </Row>
        </Panel>
      </Form>
    </Modal>
  );
};

export default injectIntl(OppgaveReservasjonEndringDatoModal);
