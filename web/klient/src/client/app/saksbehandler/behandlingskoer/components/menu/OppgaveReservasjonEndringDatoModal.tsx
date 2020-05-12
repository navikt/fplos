import React, { Component } from 'react';
import { Form } from 'react-final-form';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import { Column, Row } from 'nav-frontend-grid';
import { Knapp } from 'nav-frontend-knapper';
import Panel from 'nav-frontend-paneler';

import { DatepickerField } from 'form/FinalFields';
import styles from 'saksbehandler/behandlingskoer/components/menu/oppgaveReservasjonEndringDatoModal.less';
import Modal from 'sharedComponents/Modal';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { dateAfterOrEqual, dateBeforeOrEqual, hasValidDate } from 'utils/validation/validators';

const thirtyDaysFromNow = () => {
  const result = new Date();
  result.setDate(new Date().getDate() + 30);
  return result;
};

interface TsProps {
  showModal: boolean;
  endreOppgaveReservasjon: (reserverTil: string) => void;
  closeModal: (event: Event) => void;
  reserverTilDefault: string;
}

/**
 * OppgaveReservasjonEndringDatoModal.
 */
export class OppgaveReservasjonEndringDatoModal extends Component<TsProps & WrappedComponentProps> {
  buildInitialValues = (reserverTil: string) => ({
    reserverTil: (reserverTil && reserverTil.length >= 10) ? reserverTil.substr(0, 10) : '',
  });

  render = () => {
    const {
      intl, showModal, endreOppgaveReservasjon, closeModal, reserverTilDefault,
    } = this.props;

    return (
      <Modal
        className={styles.modal}
        isOpen={showModal}
        closeButton={false}
        contentLabel={intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Header' })}
        onRequestClose={closeModal}
      >
        <Form
          onSubmit={(values) => endreOppgaveReservasjon(values.reserverTil)}
          initialValues={this.buildInitialValues(reserverTilDefault)}
          render={({ handleSubmit }) => (
            <form onSubmit={handleSubmit}>
              <Panel className={styles.panel}>
                <h3>
                  {intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Header' })}
                </h3>
                <DatepickerField
                  name="reserverTil"
                  onBlurValidation
                  validate={[hasValidDate, dateAfterOrEqual(new Date()), dateBeforeOrEqual(thirtyDaysFromNow())]}
                  alwaysShowCalendar
                  firstDate={new Date()}
                  lastDate={thirtyDaysFromNow()}
                />
                <Row className={styles.buttonRow}>
                  <Column>
                    <div className={styles.buttonBox}>
                      <Knapp
                        mini
                        className={styles.button}
                        autoFocus
                      >
                        {intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Ok' })}
                      </Knapp>

                      <Knapp
                        mini
                        className={styles.button}
                        onClick={closeModal}
                      >
                        {intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Avbryt' })}
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
  }
}

export default injectIntl(OppgaveReservasjonEndringDatoModal);
