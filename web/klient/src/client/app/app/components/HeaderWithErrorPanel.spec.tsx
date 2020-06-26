import React from 'react';
import sinon from 'sinon';
import { expect } from 'chai';
import Header from '@navikt/nap-header';
import { IntlShape } from 'react-intl';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import RestApiTestMocker from 'testHelpers/RestApiTestMocker';

import HeaderWithErrorPanel from './HeaderWithErrorPanel';

const navAnsatt = {
  navn: 'Per',
};

describe('<HeaderWithErrorPanel>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it('skal sjekke at header blir vist korrekt', () => {
    new RestApiTestMocker()
      .withGlobalData(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt)
      .runTest(() => {
        const wrapper = shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
          intl={intl as IntlShape}
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
});
