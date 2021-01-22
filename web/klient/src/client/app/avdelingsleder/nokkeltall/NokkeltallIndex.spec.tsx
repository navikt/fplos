import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import NokkeltallIndex from './NokkeltallIndex';
import NokkeltallPanel from './components/NokkeltallPanel';

describe('<NokkeltallIndex>', () => {
  it('skal hente statistikk ved lasting av komponent', () => {
    requestApi.mock(RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING, []);
    requestApi.mock(RestApiPathsKeys.HENT_OPPGAVER_PER_DATO, []);
    requestApi.mock(RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT, []);
    requestApi.mock(RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG, []);

    const wrapper = shallow(<NokkeltallIndex
      valgtAvdelingEnhet="2"
    />);

    expect(wrapper.find(NokkeltallPanel)).to.have.length(1);
  });
});
