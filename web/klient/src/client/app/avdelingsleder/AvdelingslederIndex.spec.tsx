import React from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';
import Tabs from 'nav-frontend-tabs';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import * as useTrackRouteParam from 'app/data/useTrackRouteParam';
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

const location = {
  hash: '23',
  pathname: '/test/',
  state: {},
  search: '',
};

describe('<AvdelingslederIndex>', () => {
  it('skal vise lasteikon før valgt avdeling er satt', () => {
    const contextStub = sinon.stub(useTrackRouteParam, 'default').callsFake(() => ({
      selected: AvdelingslederPanels.BEHANDLINGSKOER, location,
    }));

    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
    requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, []);

    const wrapper = shallow(<AvdelingslederIndex />);
    expect(wrapper.find(LoadingPanel)).toHaveLength(1);
    contextStub.restore();
  });

  it(
    'skal vise avdelingsleder dashboard etter at valgt avdeling er satt',
    () => {
      const contextStub = sinon.stub(useTrackRouteParam, 'default').callsFake(() => ({
        selected: AvdelingslederPanels.BEHANDLINGSKOER, location,
      }));

      requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
      requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, []);
      const wrapper = shallow(<AvdelingslederIndex
        valgtAvdelingEnhet="1"
      />);
      expect(wrapper.find(AvdelingslederDashboard)).toHaveLength(1);
      contextStub.restore();
    },
  );

  it('skal vise alle fire panelene', () => {
    const contextStub = sinon.stub(useTrackRouteParam, 'default').callsFake(() => ({
      selected: AvdelingslederPanels.BEHANDLINGSKOER, location,
    }));

    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
    requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, []);
    const wrapper = shallow(<AvdelingslederIndex
      valgtAvdelingEnhet="1"
    />);

    const tab = wrapper.find(Tabs);
    expect(tab).toHaveLength(1);

    const tabs = tab.prop('tabs') as { label: any, aktiv: true }[];
    expect(tabs).toHaveLength(4);
    expect(tabs[0].label.props.children.props.id).toEqual('AvdelingslederIndex.Behandlingskoer');
    expect(tabs[0].aktiv).toBe(true);
    expect(tabs[1].label.props.children.props.id).toEqual('AvdelingslederIndex.Nokkeltall');
    expect(tabs[1].aktiv).toBe(false);
    expect(tabs[2].label.props.children.props.id).toEqual('AvdelingslederIndex.Saksbehandlere');
    expect(tabs[2].aktiv).toBe(false);
    expect(tabs[3].label.props.children.props.id).toEqual('AvdelingslederIndex.Reservasjoner');
    expect(tabs[3].aktiv).toBe(false);
    contextStub.restore();
  });

  it('skal rendre panel for sakslister når dette er valgt', () => {
    const contextStub = sinon.stub(useTrackRouteParam, 'default').callsFake(() => ({
      selected: AvdelingslederPanels.BEHANDLINGSKOER, location,
    }));

    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
    requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, []);
    const wrapper = shallow(<AvdelingslederIndex
      valgtAvdelingEnhet="1"
    />);

    expect(wrapper.find(EndreBehandlingskoerIndex)).toHaveLength(1);
    contextStub.restore();
  });

  it('skal rendre panel for saksbehandlere når dette er valgt', () => {
    const contextStub = sinon.stub(useTrackRouteParam, 'default').callsFake(() => ({
      selected: AvdelingslederPanels.SAKSBEHANDLERE, location,
    }));
    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
    requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, []);
    const wrapper = shallow(<AvdelingslederIndex
      valgtAvdelingEnhet="1"
    />);

    expect(wrapper.find(EndreSaksbehandlereIndex)).toHaveLength(1);
    contextStub.restore();
  });

  it('skal rendre panel for nøkkeltall når dette er valgt', () => {
    const contextStub = sinon.stub(useTrackRouteParam, 'default').callsFake(() => ({
      selected: AvdelingslederPanels.NOKKELTALL, location,
    }));
    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
    requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, []);
    const wrapper = shallow(<AvdelingslederIndex
      valgtAvdelingEnhet="1"
    />);

    expect(wrapper.find(NokkeltallIndex)).toHaveLength(1);
    contextStub.restore();
  });

  it('skal vise at en ikke har tilgang til avdelingsleder-siden', () => {
    const navAnsattIkkeOppgavestyrer = {
      kanOppgavestyre: false,
      kanBehandleKode6: true,
    };
    const contextStub = sinon.stub(useTrackRouteParam, 'default').callsFake(() => ({
      selected: AvdelingslederPanels.RESERVASJONER, location,
    }));
    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsattIkkeOppgavestyrer);
    requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, []);
    const wrapper = shallow(<AvdelingslederIndex />);

    expect(wrapper.find(IkkeTilgangTilAvdelingslederPanel)).toHaveLength(1);
    contextStub.restore();
  });
});
