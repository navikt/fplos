import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';
import Tabs from 'nav-frontend-tabs';

import * as useRestApiData from 'data/rest-api-hooks/useGlobalStateRestApiData';
import LoadingPanel from 'sharedComponents/LoadingPanel';
import IkkeTilgangTilAvdelingslederPanel from './components/IkkeTilgangTilAvdelingslederPanel';
import NokkeltallIndex from './nokkeltall/NokkeltallIndex';
import EndreSaksbehandlereIndex from './saksbehandlere/EndreSaksbehandlereIndex';
import EndreBehandlingskoerIndex from './behandlingskoer/EndreBehandlingskoerIndex';
import AvdelingslederDashboard from './components/AvdelingslederDashboard';
import AvdelingslederPanels from './avdelingslederPanels';
import { AvdelingslederIndex } from './AvdelingslederIndex';

const navAnsatt = {
  kanOppgavestyre: true,
  kanBehandleKode6: true,
};

const stubUseRestApi = (ansatt) => {
  const contextStub = sinon.stub(useRestApiData, 'default');
  contextStub.callsFake(() => ansatt);
  return contextStub;
};

describe('<AvdelingslederIndex>', () => {
  it('skal vise lasteikon før valgt avdeling er satt', () => {
    const contextStub = stubUseRestApi(navAnsatt);
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.BEHANDLINGSKOER}
      getAvdelingslederPanelLocation={() => location}
    />);
    expect(wrapper.find(LoadingPanel)).to.have.length(1);
    contextStub.restore();
  });

  it('skal vise avdelingsleder dashboard etter at valgt avdeling er satt', () => {
    const contextStub = stubUseRestApi(navAnsatt);
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.BEHANDLINGSKOER}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
    />);
    expect(wrapper.find(AvdelingslederDashboard)).to.have.length(1);
    contextStub.restore();
  });

  it('skal vise alle fire panelene', () => {
    const contextStub = stubUseRestApi(navAnsatt);
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.BEHANDLINGSKOER}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
    />);

    const tab = wrapper.find(Tabs);
    expect(tab).to.have.length(1);

    const tabs = tab.prop('tabs');
    expect(tabs).to.have.length(4);
    expect(tabs[0].label.props.children.props.id).to.eql('AvdelingslederIndex.Behandlingskoer');
    expect(tabs[0].aktiv).is.true;
    expect(tabs[1].label.props.children.props.id).to.eql('AvdelingslederIndex.Nokkeltall');
    expect(tabs[1].aktiv).is.false;
    expect(tabs[2].label.props.children.props.id).to.eql('AvdelingslederIndex.Saksbehandlere');
    expect(tabs[2].aktiv).is.false;
    expect(tabs[3].label.props.children.props.id).to.eql('AvdelingslederIndex.Reservasjoner');
    expect(tabs[3].aktiv).is.false;

    contextStub.restore();
  });

  it('skal rendre panel for sakslister når dette er valgt', () => {
    const contextStub = stubUseRestApi(navAnsatt);
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.BEHANDLINGSKOER}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
    />);

    expect(wrapper.find(EndreBehandlingskoerIndex)).to.have.length(1);
    contextStub.restore();
  });

  it('skal rendre panel for saksbehandlere når dette er valgt', () => {
    const contextStub = stubUseRestApi(navAnsatt);
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.SAKSBEHANDLERE}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
    />);

    expect(wrapper.find(EndreSaksbehandlereIndex)).to.have.length(1);
    contextStub.restore();
  });

  it('skal rendre panel for nøkkeltall når dette er valgt', () => {
    const contextStub = stubUseRestApi(navAnsatt);
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.NOKKELTALL}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
    />);

    expect(wrapper.find(NokkeltallIndex)).to.have.length(1);
    contextStub.restore();
  });

  it('skal vise at en ikke har tilgang til avdelingsleder-siden', () => {
    const navAnsattIkkeOppgavestyrer = {
      kanOppgavestyre: false,
      kanBehandleKode6: true,
    };
    const contextStub = stubUseRestApi(navAnsattIkkeOppgavestyrer);
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.NOKKELTALL}
      getAvdelingslederPanelLocation={() => location}
    />);

    expect(wrapper.find(IkkeTilgangTilAvdelingslederPanel)).to.have.length(1);
    contextStub.restore();
  });
});
