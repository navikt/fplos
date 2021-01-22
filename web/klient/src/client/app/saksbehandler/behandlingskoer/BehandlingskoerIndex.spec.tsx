import React from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import behandlingStatus from 'kodeverk/behandlingStatus';
import SakslistePanel from './components/SakslistePanel';
import BehandlingskoerIndex from './BehandlingskoerIndex';
import BehandlingPollingTimoutModal from './components/BehandlingPollingTimoutModal';

describe('<BehandlingskoerIndex>', () => {
  // @ts-ignore
  const oldWindow = global.window;

  beforeEach(() => {
    // @ts-ignore
    global.window = {
      location: {
        assign: () => undefined,
      },
    };
  });

  afterEach(() => {
    // @ts-ignore
    global.window = oldWindow;
  });

  const sakslister = [{
    sakslisteId: 1,
    navn: 'test',
    behandlingTyper: [{
      kode: 'test',
      navn: 'test',
    }],
    fagsakYtelseTyper: [{
      kode: 'test',
      navn: 'test',
    }],
    andreKriterier: [{
      andreKriterierType: {
        kode: 'test',
        navn: 'test',
      },
      inkluder: true,
    }],
    sortering: {
      sorteringType: {
        kode: 'test',
        navn: 'test',
      },
      fra: 1,
      til: 2,
      fomDato: '2019-01-01',
      tomDato: '2019-01-10',
      erDynamiskPeriode: false,
    },
  }];

  const oppgave = {
    id: 1,
    status: {
      erReservert: false,
    },
    saksnummer: 12343,
    behandlingId: 'd10e592c-e5bd-4f24-95a6-8eb1ed48f068',
    personnummer: '1234567891',
    navn: 'Espen Uteligger',
    system: 'FPSAK',
    behandlingstype: {
      kode: 'TEST',
      navn: 'test',
    },
    behandlingStatus: {
      kode: behandlingStatus.OPPRETTET,
      navn: '',
    },
    opprettetTidspunkt: '2018-01-12',
    behandlingsfrist: '2018-01-12',
    fagsakYtelseType: {
      kode: 'TEST',
      navn: 'test',
    },
    erTilSaksbehandling: true,
    href: '',
  };

  it('skal ikke vise behandlingskøer når det ikke finnes sakslister', () => {
    requestApi.mock(RestApiPathsKeys.SAKSLISTE, []);
    requestApi.mock(RestApiPathsKeys.RESERVER_OPPGAVE);
    requestApi.mock(RestApiPathsKeys.FPSAK_BEHANDLING_ID);

    const wrapper = shallow(<BehandlingskoerIndex
      fpsakUrl="www.fpsak.no"
      fptilbakeUrl="www.fptilbake.no"
      setValgtSakslisteId={sinon.spy()}
    />);

    expect(wrapper.find(SakslistePanel)).toHaveLength(0);
    expect(wrapper.find(BehandlingPollingTimoutModal)).toHaveLength(0);
  });

  it(
    'skal hente behandlingskøer ved lasting av komponent og så vise desse korrekt',
    () => {
      requestApi.mock(RestApiPathsKeys.SAKSLISTE, sakslister);
      requestApi.mock(RestApiPathsKeys.RESERVER_OPPGAVE, undefined);
      requestApi.mock(RestApiPathsKeys.FPSAK_BEHANDLING_ID, undefined);

      const wrapper = shallow(<BehandlingskoerIndex
        fpsakUrl="www.fpsak.no"
        fptilbakeUrl="www.fptilbake.no"
        setValgtSakslisteId={sinon.spy()}
      />);

      expect(wrapper.find(SakslistePanel)).toHaveLength(1);
    },
  );

  it(
    'skal reservere og åpne sak i FPSAK når oppgave ikke er reservert fra før',
    async () => {
      requestApi.mock(RestApiPathsKeys.SAKSLISTE, sakslister);
      requestApi.mock(RestApiPathsKeys.RESERVER_OPPGAVE, {
        erReservert: true,
        erReservertAvInnloggetBruker: true,
      });
      requestApi.mock(RestApiPathsKeys.FPSAK_BEHANDLING_ID, 1);

      const wrapper = shallow(<BehandlingskoerIndex
        fpsakUrl="www.fpsak.no"
        fptilbakeUrl="www.fptilbake.no"
        setValgtSakslisteId={sinon.spy()}
      />);

      const panel = wrapper.find(SakslistePanel);
      expect(panel).toHaveLength(1);

      await panel.prop('reserverOppgave')(oppgave);

      const reserverOppgaveCallData = requestApi.getRequestMockData(RestApiPathsKeys.RESERVER_OPPGAVE);
      expect(reserverOppgaveCallData).toHaveLength(1);
      expect(reserverOppgaveCallData[0].params.oppgaveId).toEqual(1);

      const hentFpsakInternBehandlingIdCallData = requestApi.getRequestMockData(RestApiPathsKeys.FPSAK_BEHANDLING_ID);
      expect(hentFpsakInternBehandlingIdCallData).toHaveLength(1);
      expect(hentFpsakInternBehandlingIdCallData[0].params.uuid).toEqual('d10e592c-e5bd-4f24-95a6-8eb1ed48f068');
    },
  );

  it(
    'skal ikke reservere men kun åpne sak i FPSAK når oppgave allerede er reservert',
    () => {
      requestApi.mock(RestApiPathsKeys.SAKSLISTE, sakslister);
      requestApi.mock(RestApiPathsKeys.RESERVER_OPPGAVE);
      requestApi.mock(RestApiPathsKeys.FPSAK_BEHANDLING_ID, {});

      const wrapper = shallow(<BehandlingskoerIndex
        fpsakUrl="www.fpsak.no"
        fptilbakeUrl="www.fptilbake.no"
        setValgtSakslisteId={sinon.spy()}
      />);

      const panel = wrapper.find(SakslistePanel);
      expect(panel).toHaveLength(1);

      const reservertOppgave = {
        ...oppgave,
        status: {
          erReservert: true,
          erReservertAvInnloggetBruker: true,
        },
      };
      panel.prop('reserverOppgave')(reservertOppgave);

      const reserverOppgaveCallData = requestApi.getRequestMockData(RestApiPathsKeys.RESERVER_OPPGAVE);
      expect(reserverOppgaveCallData).toHaveLength(0);

      const hentFpsakInternBehandlingIdCallData = requestApi.getRequestMockData(RestApiPathsKeys.FPSAK_BEHANDLING_ID);
      expect(hentFpsakInternBehandlingIdCallData).toHaveLength(1);
      expect(hentFpsakInternBehandlingIdCallData[0].params.uuid).toEqual('d10e592c-e5bd-4f24-95a6-8eb1ed48f068');
    },
  );
});
