import sinon from 'sinon';
import { intlMock, shallowWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import React from 'react';
import { OppgaveReservasjonEndringDatoModal } from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';
import { expect } from 'chai';
import { DatepickerField } from 'form/FinalFields';
import Modal from 'sharedComponents/Modal';
import { Field, Form } from 'react-final-form';
import { Panel } from 'nav-frontend-paneler';
import Datepicker from 'sharedComponents/datepicker/Datepicker';
import { RenderDatepickerField } from 'form/finalFields/DatepickerField';

describe('<OppgaveReservasjonEndringDatoModal>', () => {
  it('skal rendre modal for å gi mulighet for valg av dato', () => {
    const formwrapper = shallowWithIntl(
      <OppgaveReservasjonEndringDatoModal
        intl={intlMock}
        showModal
        endreOppgaveReservasjon={sinon.spy()}
        closeModal={sinon.spy()}
        reserverTilDefault="2017-08-02T00:54:25.455"
      />,
    ).find(Form).drill(props => props.render()).shallow();
    const datepickerField = formwrapper.find(DatepickerField);
    expect(datepickerField).to.have.length(1);
    /*const datepickerFieldWrappper = datepickerField.drill(props => props.render()).shallow();
    const field = datepickerFieldWrappper.find(Field);
    expect(field).to.have.length(1);*/
  });
});
