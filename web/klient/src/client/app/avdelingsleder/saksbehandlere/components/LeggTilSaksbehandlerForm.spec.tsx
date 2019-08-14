import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';

import { Form } from 'react-final-form';
import { Normaltekst } from 'nav-frontend-typografi';

import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { LeggTilSaksbehandlerForm } from './LeggTilSaksbehandlerForm';

describe('<LeggTilSaksbehandlerForm>', () => {
  it('skal vise form for å søke opp saksbehandlere men ikke knapper for å legge til og nullstille', () => {
    const formProps = { handleSubmit: sinon.spy() };

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm
      intl={intlMock}
      finnSaksbehandler={sinon.spy()}
      leggTilSaksbehandler={sinon.spy()}
      resetSaksbehandlerSok={sinon.spy()}
      erLagtTilAllerede={false}
      erSokFerdig={false}
      valgtAvdelingEnhet="2"
    />).find(Form).drill(props => props.render(formProps)).shallow();

    expect(wrapper.find(Knapp)).to.have.length(1);
    expect(wrapper.find(Hovedknapp)).to.have.length(0);
  });

  it('skal vise form etter at saksbehandler er søkt opp, med knapp for å legge til og nullstille', () => {
    const saksbehandler = {
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    };
    const formProps = { handleSubmit: sinon.spy() };

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm
      intl={intlMock}
      finnSaksbehandler={sinon.spy()}
      leggTilSaksbehandler={sinon.spy()}
      resetSaksbehandlerSok={sinon.spy()}
      saksbehandler={saksbehandler}
      erLagtTilAllerede={false}
      erSokFerdig
      valgtAvdelingEnhet="2"
    />).find(Form).drill(props => props.render(formProps)).shallow();

    expect(wrapper.find(Knapp)).to.have.length(2);
    expect(wrapper.find(Hovedknapp)).to.have.length(1);

    const tekst = wrapper.find(Normaltekst);
    expect(tekst).to.have.length(1);
    expect(tekst.childAt(0).text()).to.eql('Espen Utvikler, NAV Oslo');
  });

  it('skal nullstille form state og funnet saksbehandler ved trykk på nullstill', () => {
    const saksbehandler = {
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    };
    const resetFormFn = sinon.spy();
    const formProps = { handleSubmit: sinon.spy(), form: { reset: resetFormFn } };
    const resetSaksbehandlerFn = sinon.spy();

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm
      intl={intlMock}
      finnSaksbehandler={sinon.spy()}
      leggTilSaksbehandler={sinon.spy()}
      resetSaksbehandlerSok={resetSaksbehandlerFn}
      saksbehandler={saksbehandler}
      erLagtTilAllerede={false}
      erSokFerdig
      valgtAvdelingEnhet="2"
    />).find(Form).drill(props => props.render(formProps)).shallow();

    const nullstillKnapp = wrapper.find(Knapp).last();

    nullstillKnapp.prop('onClick')();

    expect(resetFormFn.calledOnce).to.be.true;
    expect(resetSaksbehandlerFn.calledOnce).to.be.true;
  });

  it('skal legge til saksbehandler ved trykk på knapp for legg til', async () => {
    const saksbehandler = {
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    };
    const resetFormFn = sinon.spy();
    const formProps = { handleSubmit: sinon.spy(), form: { reset: resetFormFn } };
    let brukerIdent;
    const leggTilSaksbehandlerFn = (ident) => {
      brukerIdent = ident;
      return Promise.resolve('');
    };
    const resetSaksbehandlerFn = sinon.spy();

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm
      intl={intlMock}
      finnSaksbehandler={sinon.spy()}
      leggTilSaksbehandler={leggTilSaksbehandlerFn}
      resetSaksbehandlerSok={resetSaksbehandlerFn}
      saksbehandler={saksbehandler}
      erLagtTilAllerede={false}
      erSokFerdig
      valgtAvdelingEnhet="2"
    />).find(Form).drill(props => props.render(formProps)).shallow();

    const leggTilKnapp = wrapper.find(Hovedknapp);

    await leggTilKnapp.prop('onClick')();

    expect(brukerIdent).is.eql(saksbehandler.brukerIdent);
    expect(resetFormFn.calledOnce).to.be.true;
    expect(resetSaksbehandlerFn.calledOnce).to.be.true;
  });

  it('skal vise tekst som viser funnet brukerinformasjon', () => {
    const saksbehandler = {
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    };
    const formProps = { handleSubmit: sinon.spy() };
    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm
      intl={intlMock}
      saksbehandler={saksbehandler}
      finnSaksbehandler={sinon.spy()}
      leggTilSaksbehandler={sinon.spy()}
      resetSaksbehandlerSok={sinon.spy()}
      erLagtTilAllerede={false}
      erSokFerdig
      valgtAvdelingEnhet="2"
    />).find(Form).drill(props => props.render(formProps)).shallow();

    const tekstKomp = wrapper.find(Normaltekst);
    expect(tekstKomp).to.have.length(1);
    expect(tekstKomp.childAt(0).text()).is.eql('Espen Utvikler, NAV Oslo');
  });

  it('skal vise tekst som viser at brukerident ikke finnes etter søk på ugyldig bruker', () => {
    const formProps = { handleSubmit: sinon.spy() };

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm
      intl={intlMock}
      finnSaksbehandler={sinon.spy()}
      leggTilSaksbehandler={sinon.spy()}
      resetSaksbehandlerSok={sinon.spy()}
      erLagtTilAllerede={false}
      erSokFerdig
      valgtAvdelingEnhet="2"
    />).find(Form).drill(props => props.render(formProps)).shallow();

    const tekstKomp = wrapper.find(Normaltekst);
    expect(tekstKomp).to.have.length(1);
    expect(tekstKomp.childAt(0).text()).is.eql('Kan ikke finne brukerident');
  });

  it('skal vise tekst som viser at brukerident allerede er lagt til', () => {
    const saksbehandler = {
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    };
    const formProps = { handleSubmit: sinon.spy() };
    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm
      intl={intlMock}
      saksbehandler={saksbehandler}
      finnSaksbehandler={sinon.spy()}
      leggTilSaksbehandler={sinon.spy()}
      resetSaksbehandlerSok={sinon.spy()}
      erLagtTilAllerede
      erSokFerdig
      valgtAvdelingEnhet="2"
    />).find(Form).drill(props => props.render(formProps)).shallow();

    const tekstKomp = wrapper.find(Normaltekst);
    expect(tekstKomp).to.have.length(1);
    expect(tekstKomp.childAt(0).text()).is.eql('Espen Utvikler, NAV Oslo (Brukerident finnes allerede i listen)');
  });
});
