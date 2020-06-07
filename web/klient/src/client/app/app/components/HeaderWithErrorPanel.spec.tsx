import React from 'react';
import sinon from 'sinon';
import { expect } from 'chai';
import Header from '@navikt/nap-header';
import { IntlShape } from 'react-intl';

import * as useRestApiData from 'data/rest-api-hooks/useGlobalStateRestApiData';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';

import HeaderWithErrorPanel from './HeaderWithErrorPanel';

const navAnsatt = {
  navn: 'Per',
};

describe('<HeaderWithErrorPanel>', () => {
  let contextStub;
  before(() => {
    contextStub = sinon.stub(useRestApiData, 'default').callsFake(() => navAnsatt);
  });

  after(() => {
    contextStub.restore();
  });

  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it('skal sjekke at header blir vist korrekt', () => {
    const wrapper = shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
      intl={intl as IntlShape}
      removeErrorMessage={sinon.spy()}
      queryStrings={{
        errormessage: 'test',
      }}
      setSiteHeight={sinon.spy()}
      setValgtAvdelingEnhet={sinon.spy()}
    />);

    const header = wrapper.find(Header);
    expect(header.prop('title')).to.eq('Svangerskap, fødsel og adopsjon');
  });
});
