import sinon from 'sinon';
import React from 'react';
import { expect } from 'chai';
import { IntlShape } from 'react-intl';
import { Form } from 'react-final-form';

import { intlMock, shallowWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import { OppgaveReservasjonEndringDatoModal } from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';
import { DatepickerField } from 'form/FinalFields';

describe('<OppgaveReservasjonEndringDatoModal>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
  it('skal rendre modal for å gi mulighet for valg av dato', () => {
    const wrapper = shallowWithIntl(
      <OppgaveReservasjonEndringDatoModal
        intl={intl as IntlShape}
        showModal
        endreOppgaveReservasjon={sinon.spy()}
        closeModal={sinon.spy()}
        reserverTilDefault="2020-08-02T00:54:25.455"
      />,
    );
    const form = wrapper.find(Form);
    expect(form).has.length(1);

    const handleSubmitFn = sinon.spy();
    const func = form.prop('render') as ({ handleSubmit: any }) => void;
    const formWrapper = shallowWithIntl(func({
      handleSubmit: handleSubmitFn,
    }));
    const datepickerField = formWrapper.find(DatepickerField);
    expect(datepickerField).to.have.length(1);
    formWrapper.find('form').simulate('submit');
    expect(handleSubmitFn.calledOnce).to.be.true;
  });
});
