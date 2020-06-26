import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import { RestApiPathsKeys } from 'data/restApiPaths';
import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import NokkeltallIndex from './NokkeltallIndex';
import NokkeltallPanel from './components/NokkeltallPanel';

describe('<NokkeltallIndex>', () => {
  it('skal hente statistikk ved lasting av komponent', () => {
    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING, [])
      .withRestCall(RestApiPathsKeys.HENT_OPPGAVER_PER_DATO, [])
      .withRestCall(RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT, [])
      .withRestCall(RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG, [])
      .runTest(() => {
        const wrapper = shallow(<NokkeltallIndex
          valgtAvdelingEnhet="2"
        />);

        expect(wrapper.find(NokkeltallPanel)).to.have.length(1);
      });
  });
});
