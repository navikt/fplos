import {
    DatepickerField,
} from 'form/FinalFields';
import { injectIntl, intlShape } from 'react-intl';
import PropTypes from 'prop-types';
import styles from 'saksbehandler/behandlingskoer/components/menu/oppgaveReservasjonEndringDatoModal.less';
import Modal from 'sharedComponents/Modal';
import React, { Component } from 'react';
import { Column, Row } from 'nav-frontend-grid';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { dateAfterOrEqual, dateBeforeOrEqual, hasValidDate } from 'utils/validation/validators';
import { Form } from 'react-final-form';
import Panel from 'nav-frontend-paneler';

const buildInitialValues = (reserverTil: string) => ({
    reserverTil: (reserverTil && reserverTil.length >= 10) ? reserverTil.substr(0, 10) : '',
});

const thirtyDaysFromNow = () => {
    const result = new Date();
    result.setDate(new Date().getDate() + 30);
    return result;
};

interface TsProps {
    intl: any;
    showModal: boolean;
    endreOppgaveReservasjon: (reserverTil: string) => void;
    closeModal: (event: Event) => void;
    reserverTilDefault: string;
}

interface TsState {
    reserverTil: string;
}
/**
 * OppgaveReservasjonEndringDatoModal.
 */
export class OppgaveReservasjonEndringDatoModal extends Component<TsProps, TsState> {
    static propTypes = {
        intl: intlShape.isRequired,
        showModal: PropTypes.bool.isRequired,
        endreOppgaveReservasjon: PropTypes.func.isRequired,
        closeModal: PropTypes.func.isRequired,
        reserverTilDefault: PropTypes.string.isRequired,
    };

    constructor() {
        super();

        this.state = {
            reserverTil: '',
        };
    }

    setValue = (e: any) => {
        this.setState({ reserverTil: (e.target.value && e.target.value.length >= 10) ? e.target.value.substr(0, 10) : '' });
    }

    render = () => {
        const {
            intl, showModal, endreOppgaveReservasjon, closeModal, reserverTilDefault,
        } = this.props;
        const {
            reserverTil,
        } = this.state;

        return (

          <Modal
            className={styles.modal}
            isOpen={showModal}
            closeButton={false}
            contentLabel={intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Header' })}
            onRequestClose={closeModal}
          >
            <Form

              onSubmit={() => undefined}
              initialValues={buildInitialValues(reserverTilDefault)}
              render={() => (
                <Panel className={styles.panel}>

                  <Row>
                    <Column xs="8">
                      <DatepickerField
                        name="reserverTil"
                        label={{ id: 'OppgaveReservasjonEndringDatoModal.Header' }}
                        onBlurValidation
                        validate={[hasValidDate, dateAfterOrEqual(new Date()), dateBeforeOrEqual(thirtyDaysFromNow())]}
                        onBlur={this.setValue}
                        alwaysShowCalendar
                        firstDate={new Date()}
                        lastDate={thirtyDaysFromNow()}
                      />
                    </Column>
                    <Column xs="1">
                      <div className={styles.divider} />
                    </Column>
                    <Column xs="3" className={styles.buttonCol}>
                      <div className={styles.buttonBox}>
                        <Hovedknapp
                          mini
                          className={styles.button}
                          onClick={() => { endreOppgaveReservasjon(reserverTil); }}
                          autoFocus
                        >
                          {intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Ok' })}
                        </Hovedknapp>

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
           )}
            />
          </Modal>
        );
    }
}

export default injectIntl(OppgaveReservasjonEndringDatoModal);
