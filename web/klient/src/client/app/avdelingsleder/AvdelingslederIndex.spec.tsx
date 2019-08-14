import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import Tabs from 'nav-frontend-tabs';

import LoadingPanel from 'sharedComponents/LoadingPanel';
import IkkeTilgangTilAvdelingslederPanel from './components/IkkeTilgangTilAvdelingslederPanel';
import NokkeltallIndex from './nokkeltall/NokkeltallIndex';
import EndreSaksbehandlereIndex from './saksbehandlere/EndreSaksbehandlereIndex';
import EndreBehandlingskoerIndex from './behandlingskoer/EndreBehandlingskoerIndex';
import AvdelingslederDashboard from './components/AvdelingslederDashboard';
import AvdelingslederPanels from './avdelingslederPanels';
import { AvdelingslederIndex } from './AvdelingslederIndex';

describe('<AvdelingslederIndex>', () => {
  it('skal vise lasteikon før valgt avdeling er satt', () => {
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.BEHANDLINGSKOER}
      getAvdelingslederPanelLocation={() => location}
      kanOppgavestyre
    />);
    expect(wrapper.find(LoadingPanel)).to.have.length(1);
  });

  it('skal vise avdelingsleder dashboard etter at valgt avdeling er satt', () => {
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.BEHANDLINGSKOER}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
      kanOppgavestyre
    />);
    expect(wrapper.find(AvdelingslederDashboard)).to.have.length(1);
  });

  it('skal vise alle tre panelene', () => {
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.BEHANDLINGSKOER}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
      kanOppgavestyre
    />);

    const tab = wrapper.find(Tabs);
    expect(tab).to.have.length(1);

    const tabs = tab.prop('tabs');
    expect(tabs).to.have.length(3);
    expect(tabs[0].label.props.children.props.id).to.eql('AvdelingslederIndex.Behandlingskoer');
    expect(tabs[0].aktiv).is.true;
    expect(tabs[1].label.props.children.props.id).to.eql('AvdelingslederIndex.Nokkeltall');
    expect(tabs[1].aktiv).is.false;
    expect(tabs[2].label.props.children.props.id).to.eql('AvdelingslederIndex.Saksbehandlere');
    expect(tabs[2].aktiv).is.false;
  });

  it('skal rendre panel for sakslister når dette er valgt', () => {
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.BEHANDLINGSKOER}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
      kanOppgavestyre
    />);

    expect(wrapper.find(EndreBehandlingskoerIndex)).to.have.length(1);
  });

  it('skal rendre panel for saksbehandlere når dette er valgt', () => {
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.SAKSBEHANDLERE}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
      kanOppgavestyre
    />);

    expect(wrapper.find(EndreSaksbehandlereIndex)).to.have.length(1);
  });

  it('skal rendre panel for nøkkeltall når dette er valgt', () => {
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.NOKKELTALL}
      getAvdelingslederPanelLocation={() => location}
      valgtAvdelingEnhet="1"
      kanOppgavestyre
    />);

    expect(wrapper.find(NokkeltallIndex)).to.have.length(1);
  });

  it('skal vise at en ikke har tilgang til avdelingsleder-siden', () => {
    const location = {
      hash: '23',
      pathname: '/test/',
      state: {},
    };
    const wrapper = shallow(<AvdelingslederIndex
      activeAvdelingslederPanel={AvdelingslederPanels.NOKKELTALL}
      getAvdelingslederPanelLocation={() => location}
      kanOppgavestyre={false}
    />);

    expect(wrapper.find(IkkeTilgangTilAvdelingslederPanel)).to.have.length(1);
  });
});
