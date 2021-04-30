import React from 'react';
import sinon from 'sinon';
import Header from '@navikt/nap-header';
import { IntlShape } from 'react-intl';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';

import HeaderWithErrorPanel from './HeaderWithErrorPanel';

const navAnsatt = {
  navn: 'Per',
};

describe('<HeaderWithErrorPanel>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it('skal sjekke at header blir vist korrekt', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT.name, navAnsatt);
    requestApi.mock(RestApiGlobalStatePathsKeys.DRIFTSMELDINGER.name, {});

    const wrapper = shallowWithIntl(<HeaderWithErrorPanel.WrappedComponent
      intl={intl as IntlShape}
      queryStrings={{
        errormessage: 'test',
      }}
      setSiteHeight={sinon.spy()}
      setValgtAvdelingEnhet={sinon.spy()}
    />);

    const header = wrapper.find(Header);
    expect(header.prop('title')).toBe('Svangerskap, fødsel og adopsjon');
  });
});
