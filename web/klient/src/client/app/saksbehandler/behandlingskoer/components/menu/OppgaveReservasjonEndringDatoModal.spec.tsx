import sinon from 'sinon';
import React from 'react';
import { IntlShape } from 'react-intl';
import { Form } from 'react-final-form';

import { intlMock, shallowWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import OppgaveReservasjonEndringDatoModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';
import { DatepickerField } from 'form/FinalFields';

describe('<OppgaveReservasjonEndringDatoModal>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
  it('skal rendre modal for å gi mulighet for valg av dato', () => {
    const wrapper = shallowWithIntl(
      <OppgaveReservasjonEndringDatoModal.WrappedComponent
        intl={intl as IntlShape}
        showModal
        closeModal={sinon.spy()}
        reserverTilDefault="2020-08-02T00:54:25.455"
        oppgaveId={1}
        endreReserverasjonState={sinon.spy()}
        hentReserverteOppgaver={sinon.spy()}
      />,
    );
    const form = wrapper.find(Form);
    expect(form).toHaveLength(1);

    const handleSubmitFn = sinon.spy();
    const func = form.prop('render') as ({ handleSubmit: any }) => void;
    // @ts-ignore Fiks
    const formWrapper = shallowWithIntl(func({
      handleSubmit: handleSubmitFn,
    }));
    const datepickerField = formWrapper.find(DatepickerField);
    expect(datepickerField).toHaveLength(1);
    formWrapper.find('form').simulate('submit');
    expect(handleSubmitFn.calledOnce).toBe(true);
  });
});
