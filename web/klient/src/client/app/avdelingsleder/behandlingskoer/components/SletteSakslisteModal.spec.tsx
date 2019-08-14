
import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { SletteSakslisteModal } from './SletteSakslisteModal';

describe('<SletteSakslisteModal>', () => {
  it('skal vise slette-modal med knapper for om en vil slette eller ikke', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-01-01',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
    };

    const wrapper = shallowWithIntl(<SletteSakslisteModal
      intl={intlMock}
      valgtSaksliste={saksliste}
      cancel={sinon.spy()}
      submit={sinon.spy()}
    />);

    expect(wrapper.find(Hovedknapp)).to.have.length(1);
    expect(wrapper.find(Knapp)).to.have.length(1);
  });

  it('skal kjøre slettefunksjon ved trykk på Ja-knapp', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-01-01',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
    };
    const submitFn = sinon.spy();

    const wrapper = shallowWithIntl(<SletteSakslisteModal
      intl={intlMock}
      valgtSaksliste={saksliste}
      cancel={sinon.spy()}
      submit={submitFn}
    />);

    const sletteknapp = wrapper.find(Hovedknapp);
    expect(sletteknapp).to.have.length(1);

    sletteknapp.prop('onClick')();

    expect(submitFn.calledOnce).to.be.true;
    const { args } = submitFn.getCalls()[0];
    expect(args).to.have.length(1);
    expect(args[0]).to.eql(saksliste);
  });
});
