import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';

import EventType from 'data/rest-api/src/requestApi/eventType';
import { shallowWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import HeaderWithErrorPanel from './components/HeaderWithErrorPanel';

import { AppIndex } from './AppIndex';

describe('<AppIndex>', () => {
  it('skal vise hjem-skjermbilde inkludert header men ikke feilmelding', () => {
    const wrapper = shallowWithIntl(<AppIndex
      navAnsattName="Peder"
      showCrashMessage={sinon.spy()}
      removeErrorMessage={sinon.spy()}
      errorMessages={[]}
      location={{ search: undefined, state: {} }}
      fetchAvdelingeneTilAvdelingsleder={sinon.spy()}
      setAvdelingEnhet={sinon.spy()}
      resetAvdelingEnhet={sinon.spy()}
      resetAvdelingeneTilAvdelingslederData={sinon.spy()}
    />);

    const headerComp = wrapper.find(HeaderWithErrorPanel);
    expect(headerComp).to.have.length(1);
    expect(headerComp.prop('navAnsattName')).to.eql('Peder');

    const homeComp = wrapper.find('Home');
    expect(homeComp).to.have.length(1);
  });

  it('skal vise hjem-skjermbilde inkludert header og feilmelding', () => {
    const wrapper = shallowWithIntl(<AppIndex
      navAnsattName="Peder"
      showCrashMessage={sinon.spy()}
      removeErrorMessage={sinon.spy()}
      errorMessages={[{
        type: EventType.REQUEST_ERROR,
      }]}
      location={{ search: undefined, state: {} }}
      fetchAvdelingeneTilAvdelingsleder={sinon.spy()}
      setAvdelingEnhet={sinon.spy()}
      resetAvdelingEnhet={sinon.spy()}
      resetAvdelingeneTilAvdelingslederData={sinon.spy()}
    />);

    const headerComp = wrapper.find(HeaderWithErrorPanel);
    expect(headerComp).to.have.length(1);
    expect(headerComp.prop('navAnsattName')).to.eql('Peder');

    const homeComp = wrapper.find('Home');
    expect(homeComp).to.have.length(1);
  });

  it('skal vise query-feilmelding', () => {
    const location = {
      search: '?errormessage=Det+finnes+ingen+sak+med+denne+referansen%3A+266',
      state: {},
    };

    const wrapper = shallowWithIntl(<AppIndex
      navAnsattName="Peder"
      removeErrorMessage={sinon.spy()}
      showCrashMessage={sinon.spy()}
      errorMessages={[{
        type: EventType.REQUEST_ERROR,
      }]}
      location={location}
      fetchAvdelingeneTilAvdelingsleder={sinon.spy()}
      setAvdelingEnhet={sinon.spy()}
      resetAvdelingEnhet={sinon.spy()}
      resetAvdelingeneTilAvdelingslederData={sinon.spy()}
    />);

    const headerComp = wrapper.find(HeaderWithErrorPanel);
    expect(headerComp.prop('queryStrings')).to.eql({ errormessage: 'Det finnes ingen sak med denne referansen: 266' });
  });

  it('skal hente avdelinger når en er i avdelingsleders skjermbilde', () => {
    const fetchAvdelingerFn = sinon.spy();
    const location = {
      pathname: 'avdelingsleder',
      state: {},
    };

    shallowWithIntl(<AppIndex
      navAnsattName="Peder"
      removeErrorMessage={sinon.spy()}
      showCrashMessage={sinon.spy()}
      errorMessagesLength={1}
      location={location}
      fetchAvdelingeneTilAvdelingsleder={fetchAvdelingerFn}
      setAvdelingEnhet={sinon.spy()}
      resetAvdelingEnhet={sinon.spy()}
      resetAvdelingeneTilAvdelingslederData={sinon.spy()}
      kanOppgavestyre
    />);

    expect(fetchAvdelingerFn.calledOnce).to.be.true;
  });

  it('skal ikke hente avdelinger når en ikke har rettigheter til å se avdelingsleders skjermbilde', () => {
    const fetchAvdelingerFn = sinon.spy();
    const location = {
      pathname: 'avdelingsleder',
      state: {},
    };

    shallowWithIntl(<AppIndex
      navAnsattName="Peder"
      removeErrorMessage={sinon.spy()}
      showCrashMessage={sinon.spy()}
      errorMessagesLength={1}
      location={location}
      fetchAvdelingeneTilAvdelingsleder={fetchAvdelingerFn}
      setAvdelingEnhet={sinon.spy()}
      resetAvdelingEnhet={sinon.spy()}
      resetAvdelingeneTilAvdelingslederData={sinon.spy()}
      kanOppgavestyre={false}
    />);

    expect(fetchAvdelingerFn.calledOnce).to.be.false;
  });

  it('skal resette avdelinger når en går fra avdelingsleders skjermbilde til saksbehandlers skjermbilde', () => {
    const fetchAvdelingerFn = sinon.spy();
    const resetAvdelingFn = sinon.spy();
    const resetAvdelingeneFn = sinon.spy();
    const location = {
      pathname: 'annet',
      state: {},
    };

    shallowWithIntl(<AppIndex
      navAnsattName="Peder"
      removeErrorMessage={sinon.spy()}
      showCrashMessage={sinon.spy()}
      errorMessagesLength={1}
      location={location}
      fetchAvdelingeneTilAvdelingsleder={fetchAvdelingerFn}
      setAvdelingEnhet={sinon.spy()}
      resetAvdelingEnhet={resetAvdelingFn}
      resetAvdelingeneTilAvdelingslederData={resetAvdelingeneFn}
      avdelinger={[{
        avdelingEnhet: 'test',
        navn: 'test',
        kreverKode6: false,
      }]}
    />);

    expect(fetchAvdelingerFn.calledOnce).to.be.false;
    expect(resetAvdelingFn.calledOnce).to.be.true;
    expect(resetAvdelingeneFn.calledOnce).to.be.true;
  });
});
