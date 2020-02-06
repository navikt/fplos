import sinon from 'sinon';
import { intlMock, shallowWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import React from 'react';
import { OppgaveReservasjonEndringDatoModal } from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';
import { expect } from 'chai';
import { DatepickerField } from 'form/FinalFields';
import { Form } from 'react-final-form';

describe('<OppgaveReservasjonEndringDatoModal>', () => {
  it('skal rendre modal for å gi mulighet for valg av dato', () => {
    const wrapper = shallowWithIntl(
      <OppgaveReservasjonEndringDatoModal
        intl={intlMock}
        showModal
        endreOppgaveReservasjon={sinon.spy()}
        closeModal={sinon.spy()}
        reserverTilDefault="2020-08-02T00:54:25.455"
      />,
    );
    const form = wrapper.find(Form);
    expect(form).has.length(1);

    const handleSubmitFn = sinon.spy();
    const formWrapper = shallowWithIntl(form.prop('render')({
      handleSubmit: handleSubmitFn,
    }));
    const datepickerField = formWrapper.find(DatepickerField);
    expect(datepickerField).to.have.length(1);
    formWrapper.find('form').simulate('submit');
    expect(handleSubmitFn.calledOnce).to.be.true;
  });
});
