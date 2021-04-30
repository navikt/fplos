import React from 'react';
import { shallow } from 'enzyme';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import NokkeltallIndex from './NokkeltallIndex';
import NokkeltallPanel from './components/NokkeltallPanel';

describe('<NokkeltallIndex>', () => {
  it('skal hente statistikk ved lasting av komponent', () => {
    requestApi.mock(RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING.name, []);
    requestApi.mock(RestApiPathsKeys.HENT_OPPGAVER_PER_DATO.name, []);
    requestApi.mock(RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT.name, []);
    requestApi.mock(RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG.name, []);
    requestApi.mock(RestApiPathsKeys.HENT_OPPGAVER_APNE_ELLER_PA_VENT.name, []);

    const wrapper = shallow(<NokkeltallIndex
      valgtAvdelingEnhet="2"
    />);

    expect(wrapper.find(NokkeltallPanel)).toHaveLength(1);
  });
});
