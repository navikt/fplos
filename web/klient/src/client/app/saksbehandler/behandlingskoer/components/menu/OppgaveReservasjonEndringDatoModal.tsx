import {
    DatepickerField,
} from 'form/FinalFields';
import { injectIntl, intlShape } from 'react-intl';
import PropTypes from 'prop-types';
import styles from 'saksbehandler/behandlingskoer/components/menu/oppgaveReservasjonEndringDatoModal.less';
import Modal from 'sharedComponents/Modal';
import React, { Component } from 'react';
import { Column, Row } from 'nav-frontend-grid';
import { Hovedknapp } from 'nav-frontend-knapper';
import { hasValidDate } from 'utils/validation/validators';
import moment from 'moment';
import { DDMMYYYY_DATE_FORMAT, ISO_DATE_FORMAT } from 'utils/formats';
import { Form } from 'react-final-form';
import Panel from 'nav-frontend-paneler';

const buildInitialValues = (reserverTil: string) => ({
    reserverTil: (reserverTil && reserverTil.length >= 19) ? moment(reserverTil.substr(0, 19), 'YYYY-MM-DDTHH:mm:ss', true).format(ISO_DATE_FORMAT) : '',
});

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
        this.setState({ reserverTil: e.target.value.substr(0, 10) });
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
                    <Column xs="10">
                      <DatepickerField
                        name="reserverTil"
                        label={{ id: 'OppgaveReservasjonEndringDatoModal.Header' }}
                        onBlurValidation
                        validate={[hasValidDate]}
                        onBlur={this.setValue}
                      />
                    </Column>
                    <Column xs="2">
                      <Hovedknapp
                        mini
                        className={styles.button}
                        onClick={() => { endreOppgaveReservasjon(reserverTil); }}
                      >
                        {intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Ok' })}
                      </Hovedknapp>
                    </Column>
                    <Column xs="2">
                      <Hovedknapp
                        mini
                        className={styles.button}
                        onClick={closeModal}
                        autoFocus
                      >
                        {intl.formatMessage({ id: 'OppgaveReservasjonEndringDatoModal.Avbryt' })}
                      </Hovedknapp>
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
