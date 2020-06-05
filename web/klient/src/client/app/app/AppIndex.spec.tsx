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
      showCrashMessage={sinon.spy()}
      removeErrorMessage={sinon.spy()}
      errorMessages={[]}
      location={{ search: undefined, state: {} }}
    />);

    const headerComp = wrapper.find(HeaderWithErrorPanel);
    expect(headerComp).to.have.length(1);

    const homeComp = wrapper.find('Home');
    expect(homeComp).to.have.length(1);
  });

  it('skal vise hjem-skjermbilde inkludert header og feilmelding', () => {
    const wrapper = shallowWithIntl(<AppIndex
      showCrashMessage={sinon.spy()}
      removeErrorMessage={sinon.spy()}
      errorMessages={[{
        type: EventType.REQUEST_ERROR,
      }]}
      location={{ search: undefined, state: {} }}
    />);

    const headerComp = wrapper.find(HeaderWithErrorPanel);
    expect(headerComp).to.have.length(1);

    const homeComp = wrapper.find('Home');
    expect(homeComp).to.have.length(1);
  });

  it('skal vise query-feilmelding', () => {
    const location = {
      search: '?errormessage=Det+finnes+ingen+sak+med+denne+referansen%3A+266',
      state: {},
    };

    const wrapper = shallowWithIntl(<AppIndex
      removeErrorMessage={sinon.spy()}
      showCrashMessage={sinon.spy()}
      errorMessages={[{
        type: EventType.REQUEST_ERROR,
      }]}
      location={location}
    />);

    const headerComp = wrapper.find(HeaderWithErrorPanel);
    expect(headerComp.prop('queryStrings')).to.eql({ errormessage: 'Det finnes ingen sak med denne referansen: 266' });
  });
});
