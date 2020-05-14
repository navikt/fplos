import React from 'react';
import sinon from 'sinon';
import { expect } from 'chai';
import Header from '@navikt/nap-header';
import { IntlShape } from 'react-intl';
import Popover from '@navikt/nap-popover';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';

import HeaderWithErrorPanel from './HeaderWithErrorPanel';

describe('<HeaderWithErrorPanel>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it('skal sjekke at header blir vist korrekt', () => {
    const wrapper = shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
      intl={intl as IntlShape}
      navAnsattName="Per"
      removeErrorMessage={sinon.spy()}
      queryStrings={{
        errormessage: 'test',
      }}
      avdelinger={[]}
      setSiteHeight={sinon.spy()}
      setValgtAvdeling={sinon.spy()}
    />);
    const header = wrapper.find(Header);
    expect(header.prop('title')).to.eq('Svangerskap, fødsel og adopsjon');
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
      intl={intl as IntlShape}
      navAnsattName="Per"
      removeErrorMessage={() => undefined}
      queryStrings={{}}
      avdelinger={avdelinger}
      setValgtAvdeling={() => undefined}
      valgtAvdelingEnhet={avdelinger[0].avdelingEnhet}
      setSiteHeight={sinon.spy()}
    />);

    const header = wrapper.find(Header);
    expect(header).has.length(1);

    const popovers = header.find(Popover);
    expect(popovers).has.length(2);

    const boxedList = popovers.last().prop('popperProps').children();

    expect(boxedList.props.items).to.eql([{
      name: `${avdelinger[0].avdelingEnhet} ${avdelinger[0].navn}`,
      selected: true,
    }, {
      name: `${avdelinger[1].avdelingEnhet} ${avdelinger[1].navn}`,
      selected: false,
    }]);
  });

  it('skal velge ny avdeling', () => {
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

    const wrapper = shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
      intl={intl as IntlShape}
      navAnsattName="Per"
      removeErrorMessage={() => undefined}
      queryStrings={{}}
      avdelinger={avdelinger}
      valgtAvdelingEnhet={avdelinger[0].avdelingEnhet}
      setValgtAvdeling={setValgtAvdelingFn}
      setSiteHeight={sinon.spy()}
    />);

    const header = wrapper.find(Header);
    expect(header).has.length(1);

    const popovers = header.find(Popover);
    expect(popovers).has.length(2);

    const boxedList = popovers.last().prop('popperProps').children();
    boxedList.props.onClick(1);
    expect(setValgtAvdelingFn.calledOnce).to.be.true;
    const { args } = setValgtAvdelingFn.getCalls()[0];
    expect(args).to.have.length(1);
    expect(args[0]).to.eql('4323');
  });
});
