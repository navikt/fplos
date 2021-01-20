import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';

import FagsakSearchIndex from '../fagsakSearch/FagsakSearchIndex';
import BehandlingskoerIndex from '../behandlingskoer/BehandlingskoerIndex';
import SaksstotteIndex from '../saksstotte/SaksstotteIndex';
import { SaksbehandlerDashboard } from './SaksbehandlerDashboard';

describe('<SaksbehandlerDashboard>', () => {
  it('skal vise dashboard uten fagsak-søk', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.FPSAK_URL, 'url');
    requestApi.mock(RestApiGlobalStatePathsKeys.FPTILBAKE_URL, 'url');
    const wrapper = shallow(<SaksbehandlerDashboard
      valgtSakslisteId={1}
      setValgtSakslisteId={() => undefined}
    />);

    expect(wrapper.find(BehandlingskoerIndex)).to.have.length(1);
    expect(wrapper.find(SaksstotteIndex)).to.have.length(1);
  });

  it('skal vise dashboard med fagsak-søk', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.FPSAK_URL, 'url');
    requestApi.mock(RestApiGlobalStatePathsKeys.FPTILBAKE_URL, 'url');
    const wrapper = shallow(<SaksbehandlerDashboard
      valgtSakslisteId={1}
      setValgtSakslisteId={() => undefined}
    />);

    expect(wrapper.find(FagsakSearchIndex)).to.have.length(1);
    expect(wrapper.find(BehandlingskoerIndex)).to.have.length(1);
    expect(wrapper.find(SaksstotteIndex)).to.have.length(1);
  });
});
