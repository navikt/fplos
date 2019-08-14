import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { SletteSaksbehandlerModal } from './SletteSaksbehandlerModal';

describe('<SletteSaksbehandlerModal>', () => {
  it('skal vise slette-modal med knapper for om en vil slette eller ikke', () => {
    const saksbehandler = {
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    };

    const wrapper = shallowWithIntl(<SletteSaksbehandlerModal
      intl={intlMock}
      valgtSaksbehandler={saksbehandler}
      closeSletteModal={sinon.spy()}
      fjernSaksbehandler={sinon.spy()}
    />);

    expect(wrapper.find(Hovedknapp)).to.have.length(1);
    expect(wrapper.find(Knapp)).to.have.length(1);
  });

  it('skal kjøre slettefunksjon ved trykk på Ja-knapp', () => {
    const saksbehandler = {
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    };
    const submitFn = sinon.spy();

    const wrapper = shallowWithIntl(<SletteSaksbehandlerModal
      intl={intlMock}
      valgtSaksbehandler={saksbehandler}
      closeSletteModal={sinon.spy()}
      fjernSaksbehandler={submitFn}
    />);

    const sletteknapp = wrapper.find(Hovedknapp);
    expect(sletteknapp).to.have.length(1);

    sletteknapp.prop('onClick')();

    expect(submitFn.calledOnce).to.be.true;
    const { args } = submitFn.getCalls()[0];
    expect(args).to.have.length(1);
    expect(args[0]).to.eql(saksbehandler);
  });
});
