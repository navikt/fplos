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
    const formwrapper = wrapper.find(Form).drill(props => props.render()).shallow();
    const datepickerField = formwrapper.find(DatepickerField);
    expect(datepickerField).to.have.length(1);
  });
});
