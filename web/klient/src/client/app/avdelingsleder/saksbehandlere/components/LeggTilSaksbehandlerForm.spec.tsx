import React from 'react';
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

  it(
    'skal vise form for å søke opp saksbehandlere men ikke knapper for å legge til og nullstille',
    () => {
      const formProps = { handleSubmit: sinon.spy() };

      requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK);
      requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER);

      const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
        intl={intl as IntlShape}
        valgtAvdelingEnhet="2"
        avdelingensSaksbehandlere={[]}
        hentAvdelingensSaksbehandlere={sinon.spy()}
        // @ts-ignore
      />).find(Form).renderProp('render')(formProps);

      expect(wrapper.find(Knapp)).toHaveLength(1);
      expect(wrapper.find(Hovedknapp)).toHaveLength(0);
    },
  );

  it(
    'skal vise form etter at saksbehandler er søkt opp, med knapp for å legge til og nullstille',
    async () => {
      const saksbehandler = {
        brukerIdent: 'TEST1',
        navn: 'Espen Utvikler',
        avdelingsnavn: ['NAV Oslo'],
      };
      const formProps = { handleSubmit: sinon.spy() };

      requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
      requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER);

      const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
        intl={intl as IntlShape}
        valgtAvdelingEnhet="2"
        avdelingensSaksbehandlere={[]}
        hentAvdelingensSaksbehandlere={sinon.spy()}
      />);

      // @ts-ignore
      await wrapper.find(Form).prop('onSubmit')({ brukerIdent: saksbehandler.brukerIdent });

      // @ts-ignore
      const innerWrapper = wrapper.find(Form).renderProp('render')(formProps);

      expect(innerWrapper.find(Knapp)).toHaveLength(2);
      expect(innerWrapper.find(Hovedknapp)).toHaveLength(1);

      const tekst = innerWrapper.find(Normaltekst);
      expect(tekst).toHaveLength(1);
      expect(tekst.childAt(0).text()).toEqual('Espen Utvikler, NAV Oslo');
    },
  );

  it(
    'skal nullstille form state og funnet saksbehandler ved trykk på nullstill',
    async () => {
      const saksbehandler = {
        brukerIdent: 'TEST1',
        navn: 'Espen Utvikler',
        avdelingsnavn: ['NAV Oslo'],
      };
      const resetFormFn = sinon.spy();
      const formProps = { handleSubmit: sinon.spy(), form: { reset: resetFormFn } };

      requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
      requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER);

      const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
        intl={intl as IntlShape}
        valgtAvdelingEnhet="2"
        avdelingensSaksbehandlere={[]}
        hentAvdelingensSaksbehandlere={sinon.spy()}
      />);

      // @ts-ignore
      await wrapper.find(Form).prop('onSubmit')({ brukerIdent: saksbehandler.brukerIdent });

      // @ts-ignore
      const innerWrapper = wrapper.find(Form).renderProp('render')(formProps);

      const nullstillKnapp = innerWrapper.find(Knapp).last();

      const func = nullstillKnapp.prop('onClick') as () => void;
      func();

      const lagreSakslisteAndreKriterierCallData = requestApi.getRequestMockData(RestApiPathsKeys.SAKSBEHANDLER_SOK);
      expect(lagreSakslisteAndreKriterierCallData).toHaveLength(1);

      expect(resetFormFn.calledOnce).toBe(true);
    },
  );

  it(
    'skal legge til saksbehandler ved trykk på knapp for legg til',
    async () => {
      const saksbehandler = {
        brukerIdent: 'TEST1',
        navn: 'Espen Utvikler',
        avdelingsnavn: ['NAV Oslo'],
      };
      const resetFormFn = sinon.spy();
      const formProps = { handleSubmit: sinon.spy(), form: { reset: resetFormFn } };

      requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
      requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER);

      const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
        intl={intl as IntlShape}
        valgtAvdelingEnhet="2"
        avdelingensSaksbehandlere={[]}
        hentAvdelingensSaksbehandlere={sinon.spy()}
      />);

      // @ts-ignore
      await wrapper.find(Form).prop('onSubmit')({ brukerIdent: saksbehandler.brukerIdent });

      // @ts-ignore
      const innerWrapper = wrapper.find(Form).renderProp('render')(formProps);

      const leggTilKnapp = innerWrapper.find(Hovedknapp);

      const func = leggTilKnapp.prop('onClick') as () => void;
      await func();

      const lagreSakslisteAndreKriterierCallData = requestApi.getRequestMockData(RestApiPathsKeys.SAKSBEHANDLER_SOK);
      expect(lagreSakslisteAndreKriterierCallData).toHaveLength(1);

      expect(resetFormFn.calledOnce).toBe(true);
    },
  );

  it('skal vise tekst som viser funnet brukerinformasjon', async () => {
    const saksbehandler = {
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    };
    const formProps = { handleSubmit: sinon.spy() };

    requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
    requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER);

    const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
      intl={intl as IntlShape}
      valgtAvdelingEnhet="2"
      avdelingensSaksbehandlere={[]}
      hentAvdelingensSaksbehandlere={sinon.spy()}
    />);

    // @ts-ignore
    await wrapper.find(Form).prop('onSubmit')({ brukerIdent: saksbehandler.brukerIdent });

    // @ts-ignore
    const innerWrapper = wrapper.find(Form).renderProp('render')(formProps);

    const tekstKomp = innerWrapper.find(Normaltekst);
    expect(tekstKomp).toHaveLength(1);
    expect(tekstKomp.childAt(0).text()).toEqual('Espen Utvikler, NAV Oslo');
  });

  it(
    'skal vise tekst som viser at brukerident ikke finnes etter søk på ugyldig bruker',
    async () => {
      const formProps = { handleSubmit: sinon.spy() };

      requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK);
      requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER);

      const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
        intl={intl as IntlShape}
        valgtAvdelingEnhet="2"
        avdelingensSaksbehandlere={[]}
        hentAvdelingensSaksbehandlere={sinon.spy()}
      />);

      // @ts-ignore
      await wrapper.find(Form).prop('onSubmit')({ brukerIdent: 'TEST1' });

      // @ts-ignore
      const innerWrapper = wrapper.find(Form).renderProp('render')(formProps);

      const tekstKomp = innerWrapper.find(Normaltekst);
      expect(tekstKomp).toHaveLength(1);
      expect(tekstKomp.childAt(0).text()).toEqual('Kan ikke finne brukerident');
    },
  );

  it(
    'skal vise tekst som viser at brukerident allerede er lagt til',
    async () => {
      const saksbehandler = {
        brukerIdent: 'TEST1',
        navn: 'Espen Utvikler',
        avdelingsnavn: ['NAV Oslo'],
      };
      const formProps = { handleSubmit: sinon.spy() };

      requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);
      requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER);

      const wrapper = shallowWithIntl(<LeggTilSaksbehandlerForm.WrappedComponent
        intl={intl as IntlShape}
        valgtAvdelingEnhet="2"
        avdelingensSaksbehandlere={[saksbehandler]}
        hentAvdelingensSaksbehandlere={sinon.spy()}
      />);

      // @ts-ignore
      await wrapper.find(Form).prop('onSubmit')({ brukerIdent: saksbehandler.brukerIdent });

      // @ts-ignore
      const innerWrapper = wrapper.find(Form).renderProp('render')(formProps);

      const tekstKomp = innerWrapper.find(Normaltekst);
      expect(tekstKomp).toHaveLength(1);
      expect(tekstKomp.childAt(0).text()).toEqual('Espen Utvikler, NAV Oslo (Brukerident finnes allerede i listen)');
    },
  );
});
