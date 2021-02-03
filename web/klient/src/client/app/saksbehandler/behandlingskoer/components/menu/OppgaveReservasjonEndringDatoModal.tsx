import React, { MouseEvent, FunctionComponent, useCallback } from 'react';
import { Form } from 'react-final-form';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Column, Row } from 'nav-frontend-grid';
import { Knapp } from 'nav-frontend-knapper';
import Panel from 'nav-frontend-paneler';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import Oppgave from 'saksbehandler/oppgaveTsType';
import { DatepickerField } from 'form/FinalFields';
import styles from 'saksbehandler/behandlingskoer/components/menu/oppgaveReservasjonEndringDatoModal.less';
import Modal from 'sharedComponents/Modal';
import { dateAfterOrEqual, dateBeforeOrEqual, hasValidDate } from 'utils/validation/validators';

const thirtyDaysFromNow = () => {
  const result = new Date();
  result.setDate(new Date().getDate() + 30);
  return result;
};

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
  const { startRequest: endreOppgavereservasjon } = restApiHooks.useRestApiRunner<Oppgave[]>(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON);

  const endreOppgaveReservasjonFn = useCallback((reserverTil: string) => endreOppgavereservasjon({ oppgaveId, reserverTil })
    .then(() => {
      endreReserverasjonState();
      hentReserverteOppgaver({}, true);
    }),
  []);

  const buildInitialValues = useCallback((reserverTil?: string) => ({
    reserverTil: (reserverTil && reserverTil.length >= 10) ? reserverTil.substr(0, 10) : '',
  }), []);

  return (
    <Modal
      className={styles.modal}
      isOpen={showModal}
      closeButton={false}
      contentLabel={intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Header' })}
      onRequestClose={closeModal as () => void}
    >
      <Form
        onSubmit={(values) => endreOppgaveReservasjonFn(values.reserverTil)}
        initialValues={buildInitialValues(reserverTilDefault)}
        render={({ handleSubmit }) => (
          <form onSubmit={handleSubmit}>
            <Panel className={styles.panel}>
              <h3>
                <FormattedMessage id="OppgaveReservasjonEndringDatoModal.Header" />
              </h3>
              <DatepickerField
                name="reserverTil"
                onBlurValidation
                validate={[hasValidDate, dateAfterOrEqual(new Date()), dateBeforeOrEqual(thirtyDaysFromNow())]}
                alwaysShowCalendar
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
          </form>
        )}
      />
    </Modal>
  );
};

export default injectIntl(OppgaveReservasjonEndringDatoModal);
