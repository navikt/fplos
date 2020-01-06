import React from 'react';
import sinon from 'sinon';
import { expect } from 'chai';
import Header from '@navikt/nap-header';
import BoxedListWithSelection from '@navikt/boxed-list-with-selection';
import BoxedListWithLinks from '@navikt/boxed-list-with-links';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { RETTSKILDE_URL, SYSTEMRUTINE_URL } from 'data/eksterneLenker';

import HeaderWithErrorPanel from './HeaderWithErrorPanel';

describe('<HeaderWithErrorPanel>', () => {
  it('skal vise lenker for rettskilde og systemrutine i header men ingen avdelinger når det ikke er noen', () => {
    const avdelinger = [];

    const wrapper = shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
      intl={intlMock}
      navAnsattName="Per"
      removeErrorMessage={() => undefined}
      queryStrings={{}}
      avdelinger={avdelinger}
      setValgtAvdeling={sinon.spy()}
    />);

    const header = wrapper.find(Header);
    expect(header).has.length(1);

    expect(header.prop('renderUserPopoverContent')).is.undefined;
    const boxedList = header.renderProp('renderLinksPopoverContent')().find(BoxedListWithLinks);

    expect(boxedList.prop('items')).to.eql([{
      name: 'Rettskildene',
      href: RETTSKILDE_URL,
      isExternal: true,
    }, {
      name: 'Systemrutine',
      href: SYSTEMRUTINE_URL,
      isExternal: true,
    }]);
  });

  it('skal vise to avdelinger i header', () => {
    const avdelinger = [{
      avdelingEnhet: '2323',
      navn: 'NAV Drammen',
      kreverKode6: false,
    }, {
      avdelingEnhet: '4323',
      navn: 'NAV Oslo',
      kreverKode6: false,
    }];

    const wrapper = shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
      intl={intlMock}
      navAnsattName="Per"
      removeErrorMessage={() => undefined}
      queryStrings={{}}
      avdelinger={avdelinger}
      setValgtAvdeling={() => undefined}
      valgtAvdelingEnhet={avdelinger[0].avdelingEnhet}
    />);

    const header = wrapper.find(Header);
    expect(header).has.length(1);

    const boxedList = header.renderProp('renderUserPopoverContent')().find(BoxedListWithSelection);

    expect(boxedList).has.length(1);
    expect(boxedList.prop('items')).to.eql([{
      name: `${avdelinger[0].avdelingEnhet} ${avdelinger[0].navn}`,
<<<<<<< HEAD
<<<<<<< HEAD
      selected: true,
=======
      selected: false,
>>>>>>> FIX div
=======
      selected: true,
>>>>>>> FIX div
    }, {
      name: `${avdelinger[1].avdelingEnhet} ${avdelinger[1].navn}`,
      selected: false,
    }]);
  });

  it('skal sette valgt avdeling til første avdeling i listen når ingenting er valgt fra før og en har avdelinger', () => {
    const setValgtAvdelingFn = sinon.spy();
    const avdelinger = [{
      avdelingEnhet: '2323',
      navn: 'NAV Drammen',
      kreverKode6: false,
    }, {
      avdelingEnhet: '4323',
      navn: 'NAV Oslo',
      kreverKode6: false,
    }];

    shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
      intl={intlMock}
      navAnsattName="Per"
      removeErrorMessage={() => undefined}
      queryStrings={{}}
      avdelinger={avdelinger}
      setValgtAvdeling={setValgtAvdelingFn}
    />);

    expect(setValgtAvdelingFn.calledOnce).to.be.true;
    const { args } = setValgtAvdelingFn.getCalls()[0];
    expect(args).to.have.length(1);
    expect(args[0]).to.eql('2323');
  });

  it('skal ikke sette valgt avdeling når en ikke har avdelinger', () => {
    const setValgtAvdelingFn = sinon.spy();
    const avdelinger = [];

    shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
      intl={intlMock}
      navAnsattName="Per"
      removeErrorMessage={() => undefined}
      queryStrings={{}}
      avdelinger={avdelinger}
      setValgtAvdeling={setValgtAvdelingFn}
    />);

    expect(setValgtAvdelingFn.calledOnce).to.be.false;
  });

  it('skal ikke sette valgt avdeling når den allerede er satt fra før', () => {
    const setValgtAvdelingFn = sinon.spy();
    const avdelinger = [{
      avdelingEnhet: '2323',
      navn: 'NAV Drammen',
      kreverKode6: false,
    }, {
      avdelingEnhet: '4323',
      navn: 'NAV Oslo',
      kreverKode6: false,
    }];

    shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
      intl={intlMock}
      navAnsattName="Per"
      removeErrorMessage={() => undefined}
      queryStrings={{}}
      avdelinger={avdelinger}
      setValgtAvdeling={setValgtAvdelingFn}
      valgtAvdelingEnhet={avdelinger[0].avdelingEnhet}
    />);

    expect(setValgtAvdelingFn.calledOnce).to.be.false;
  });
});
