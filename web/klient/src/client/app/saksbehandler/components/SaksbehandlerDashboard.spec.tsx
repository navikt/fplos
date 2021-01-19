import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import * as useRestApiData from 'data/rest-api-hooks/src/global-data/useGlobalStateRestApiData';

import FagsakSearchIndex from '../fagsakSearch/FagsakSearchIndex';
import BehandlingskoerIndex from '../behandlingskoer/BehandlingskoerIndex';
import SaksstotteIndex from '../saksstotte/SaksstotteIndex';
import { SaksbehandlerDashboard } from './SaksbehandlerDashboard';

describe('<SaksbehandlerDashboard>', () => {
  let contextStub;
  beforeEach(() => {
    contextStub = sinon.stub(useRestApiData, 'default').callsFake(() => 'url');
  });

  afterEach(() => {
    contextStub.restore();
  });

  it('skal vise dashboard uten fagsak-søk', () => {
    const wrapper = shallow(<SaksbehandlerDashboard
      valgtSakslisteId={1}
      setValgtSakslisteId={() => undefined}
    />);

    expect(wrapper.find(BehandlingskoerIndex)).to.have.length(1);
    expect(wrapper.find(SaksstotteIndex)).to.have.length(1);
  });

  it('skal vise dashboard med fagsak-søk', () => {
    const wrapper = shallow(<SaksbehandlerDashboard
      valgtSakslisteId={1}
      setValgtSakslisteId={() => undefined}
    />);

    expect(wrapper.find(FagsakSearchIndex)).to.have.length(1);
    expect(wrapper.find(BehandlingskoerIndex)).to.have.length(1);
    expect(wrapper.find(SaksstotteIndex)).to.have.length(1);
  });
});
