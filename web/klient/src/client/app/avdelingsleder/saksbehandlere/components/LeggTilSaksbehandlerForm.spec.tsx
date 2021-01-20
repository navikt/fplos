import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';
import { Form } from 'react-final-form';
import { Normaltekst } from 'nav-frontend-typografi';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import LeggTilSaksbehandlerForm from './LeggTilSaksbehandlerForm';

describe('<LeggTilSaksbehandlerForm>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
  it('skal vise form for å søke opp saksbehandlere men ikke knapper for å legge til og nullstille', () => {
    const formProps = { handleSubmit: sinon.spy() };

    requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, undefined);
    requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER, undefined);
    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
      intl={intl as IntlShape}
      valgtAvdelingEnhet="2"
      avdelingensSaksbehandlere={[]}
      hentAvdelingensSaksbehandlere={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')(formProps);

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

    requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
    requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER, undefined);

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
      intl={intl as IntlShape}
      valgtAvdelingEnhet="2"
      avdelingensSaksbehandlere={[]}
      hentAvdelingensSaksbehandlere={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')(formProps);

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

    requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
    requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER, undefined);

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
      intl={intl as IntlShape}
      valgtAvdelingEnhet="2"
      avdelingensSaksbehandlere={[]}
      hentAvdelingensSaksbehandlere={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')(formProps);

    const nullstillKnapp = wrapper.find(Knapp).last();

    const func = nullstillKnapp.prop('onClick') as () => void;
    func();

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

    requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
    requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER, undefined);

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
      intl={intl as IntlShape}
      valgtAvdelingEnhet="2"
      avdelingensSaksbehandlere={[]}
      hentAvdelingensSaksbehandlere={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')(formProps);

    const leggTilKnapp = wrapper.find(Hovedknapp);

    const func = leggTilKnapp.prop('onClick') as () => void;
    await func();

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

    requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
    requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER, undefined);

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
      intl={intl as IntlShape}
      valgtAvdelingEnhet="2"
      avdelingensSaksbehandlere={[]}
      hentAvdelingensSaksbehandlere={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')(formProps);

    const tekstKomp = wrapper.find(Normaltekst);
    expect(tekstKomp).to.have.length(1);
    expect(tekstKomp.childAt(0).text()).is.eql('Espen Utvikler, NAV Oslo');
  });

  it('skal vise tekst som viser at brukerident ikke finnes etter søk på ugyldig bruker', () => {
    const formProps = { handleSubmit: sinon.spy() };

    requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, undefined);
    requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER, undefined);

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
      intl={intl as IntlShape}
      valgtAvdelingEnhet="2"
      avdelingensSaksbehandlere={[]}
      hentAvdelingensSaksbehandlere={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')(formProps);

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

    requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
    requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER, undefined);

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
      intl={intl as IntlShape}
      valgtAvdelingEnhet="2"
      avdelingensSaksbehandlere={[saksbehandler]}
      hentAvdelingensSaksbehandlere={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')(formProps);

    const tekstKomp = wrapper.find(Normaltekst);
    expect(tekstKomp).to.have.length(1);
    expect(tekstKomp.childAt(0).text()).is.eql('Espen Utvikler, NAV Oslo (Brukerident finnes allerede i listen)');
  });
});
