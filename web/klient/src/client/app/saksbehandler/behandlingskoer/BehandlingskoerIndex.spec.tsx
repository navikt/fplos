
import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import sinon from 'sinon';

import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import behandlingStatus from 'kodeverk/behandlingStatus';
import { RestApiPathsKeys } from 'data/restApiPaths';
import SakslistePanel from './components/SakslistePanel';
import BehandlingskoerIndex from './BehandlingskoerIndex';
import BehandlingPollingTimoutModal from './components/BehandlingPollingTimoutModal';

describe('<BehandlingskoerIndex>', () => {
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
    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.SAKSLISTE, undefined)
      .withRestCallRunner(RestApiPathsKeys.RESERVERTE_OPPGAVER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FLYTT_RESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<BehandlingskoerIndex
          fpsakUrl="www.fpsak.no"
          fptilbakeUrl="www.fptilbake.no"
          setValgtSakslisteId={sinon.spy()}
        />);

        expect(wrapper.find(SakslistePanel)).to.have.length(0);
        expect(wrapper.find(BehandlingPollingTimoutModal)).to.have.length(0);
      });
  });

  it('skal hente behandlingskøer ved lasting av komponent og så vise desse korrekt', () => {
    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.SAKSLISTE, sakslister)
      .withRestCallRunner(RestApiPathsKeys.RESERVERTE_OPPGAVER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FLYTT_RESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<BehandlingskoerIndex
          fpsakUrl="www.fpsak.no"
          fptilbakeUrl="www.fptilbake.no"
          setValgtSakslisteId={sinon.spy()}
        />);

        expect(wrapper.find(SakslistePanel)).to.have.length(1);
      });
  });

  it('skal reservere og åpne sak i FPSAK når oppgave ikke er reservert fra før', (done) => {
    const reserverOppgave = sinon.stub().withArgs(oppgave.id).resolves({
      payload: {
        erReservert: true,
        erReservertAvInnloggetBruker: true,
      },
    });
    const hentFpsakInternBehandlingId = sinon.spy();

    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.SAKSLISTE, sakslister)
      .withRestCallRunner(RestApiPathsKeys.RESERVERTE_OPPGAVER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FLYTT_RESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<BehandlingskoerIndex
          fpsakUrl="www.fpsak.no"
          fptilbakeUrl="www.fptilbake.no"
          setValgtSakslisteId={sinon.spy()}
        />);

        const panel = wrapper.find(SakslistePanel);
        expect(panel).to.have.length(1);

        panel.prop('reserverOppgave')(oppgave);

        done();

        expect(reserverOppgave.calledOnce).to.be.true;

        expect(hentFpsakInternBehandlingId.calledOnce).to.be.true;

        const { args } = hentFpsakInternBehandlingId.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0]).to.eql(oppgave.behandlingId);
      });
  });

  it('skal ikke reservere men kun åpne sak i FPSAK når oppgave allerede er reservert', () => {
    const reserverOppgave = sinon.spy();
    const hentFpsakInternBehandlingId = sinon.stub().withArgs(oppgave.behandlingId).resolves({
      payload: 1,
    });

    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.SAKSLISTE, sakslister)
      .withRestCallRunner(RestApiPathsKeys.RESERVERTE_OPPGAVER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FLYTT_RESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<BehandlingskoerIndex
          fpsakUrl="www.fpsak.no"
          fptilbakeUrl="www.fptilbake.no"
          setValgtSakslisteId={sinon.spy()}
        />);

        const panel = wrapper.find(SakslistePanel);
        expect(panel).to.have.length(1);

        const reservertOppgave = {
          ...oppgave,
          status: {
            erReservert: true,
            erReservertAvInnloggetBruker: true,
          },
        };
        panel.prop('reserverOppgave')(reservertOppgave);

        expect(reserverOppgave.calledOnce).to.be.false;
        expect(hentFpsakInternBehandlingId.calledOnce).to.be.true;
        const { args } = hentFpsakInternBehandlingId.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0]).to.eql(oppgave.behandlingId);
      });
  });

  it('skal hente sakslistens oppgaver og så starta polling etter endringer', (done) => {
    const sakslisteId = 1;
    const oppgaveIder = [{ id: 1 }, { id: 2 }, { id: 3 }];
    /* const fetchOppgaverTilBehandlingFn = sinon.stub()
      .withArgs(sakslisteId).resolves({
        payload: oppgaveIder,
      }); */
    const fetchOppgaverTilBehandlingOppgaverFn = sinon.stub()
      .onFirstCall().resolves({
        payload: oppgaveIder.concat({ id: 4 }),
      })
      .onSecondCall()
      .throws(); // Avbryter polling
    const fetchReserverteOppgaverFn = sinon.spy();

    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.SAKSLISTE, sakslister)
      .withRestCallRunner(RestApiPathsKeys.RESERVERTE_OPPGAVER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FLYTT_RESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<BehandlingskoerIndex
          fpsakUrl="www.fpsak.no"
          fptilbakeUrl="www.fptilbake.no"
          setValgtSakslisteId={sinon.spy()}
        />);

        const panel = wrapper.find(SakslistePanel);
        expect(panel).to.have.length(1);

        panel.prop('fetchSakslisteOppgaver')(sakslisteId);

        done();

        expect(fetchReserverteOppgaverFn.calledTwice).to.be.true;
        const { args } = fetchReserverteOppgaverFn.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0]).to.eql(sakslisteId);

        expect(fetchOppgaverTilBehandlingOppgaverFn.calledOnce).to.be.true;
        const { args: args2 } = fetchOppgaverTilBehandlingOppgaverFn.getCalls()[0];
        expect(args2).to.have.length(2);
        expect(args2[0]).to.eql(1);
        expect(args2[1]).to.eql(oppgaveIder.map((o) => o.id).join(','));
      });
  });

  it('skal oppheve reservasjon og så hente reserverte oppgaver på nytt', (done) => {
    const opphevOppgaveReservasjonFn = sinon.stub().withArgs(oppgave.id).resolves();
    const fetchReserverteOppgaverFn = sinon.spy();

    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.SAKSLISTE, sakslister)
      .withRestCallRunner(RestApiPathsKeys.RESERVERTE_OPPGAVER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FLYTT_RESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<BehandlingskoerIndex
          fpsakUrl="www.fpsak.no"
          fptilbakeUrl="www.fptilbake.no"
          setValgtSakslisteId={sinon.spy()}
        />);

        const panel = wrapper.find(SakslistePanel);
        expect(panel).to.have.length(1);

        const oppgaveId = 1;
        const begrunnelse = 'Dette er en begrunnelse';
        const sakslisteId = 1;
        wrapper.setState({ sakslisteId });
        panel.prop('opphevOppgaveReservasjon')(oppgaveId, begrunnelse);

        done();

        expect(opphevOppgaveReservasjonFn.calledOnce).to.be.true;
        const { args } = opphevOppgaveReservasjonFn.getCalls()[0];
        expect(args).to.have.length(2);
        expect(args[0]).to.eql(oppgaveId);
        expect(args[1]).to.eql(begrunnelse);

        expect(fetchReserverteOppgaverFn.calledOnce).to.be.true;
        const { args: args2 } = fetchReserverteOppgaverFn.getCalls()[0];
        expect(args2).to.have.length(1);
        expect(args2[0]).to.eql(sakslisteId);
      });
  });

  it('skal forlenge reservasjon og så hente reserverte oppgaver på nytt', (done) => {
    const forlengOppgaveReservasjonFn = sinon.stub().withArgs(oppgave.id).resolves();
    const fetchReserverteOppgaverFn = sinon.spy();

    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.SAKSLISTE, sakslister)
      .withRestCallRunner(RestApiPathsKeys.RESERVERTE_OPPGAVER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FLYTT_RESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<BehandlingskoerIndex
          fpsakUrl="www.fpsak.no"
          fptilbakeUrl="www.fptilbake.no"
          setValgtSakslisteId={sinon.spy()}
        />);

        const panel = wrapper.find(SakslistePanel);
        expect(panel).to.have.length(1);

        const oppgaveId = 1;
        const sakslisteId = 1;
        wrapper.setState({ sakslisteId });
        panel.prop('forlengOppgaveReservasjon')(oppgaveId);

        done();

        expect(forlengOppgaveReservasjonFn.calledOnce).to.be.true;
        const { args } = forlengOppgaveReservasjonFn.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0]).to.eql(oppgaveId);

        expect(fetchReserverteOppgaverFn.calledOnce).to.be.true;
        const { args: args2 } = fetchReserverteOppgaverFn.getCalls()[0];
        expect(args2).to.have.length(1);
        expect(args2[0]).to.eql(sakslisteId);
      });
  });

  it('skal flytte reservasjon og så hente reserverte oppgaver på nytt', (done) => {
    const flyttReservasjonFn = sinon.stub().withArgs(oppgave.id).resolves();
    const fetchReserverteOppgaverFn = sinon.spy();
    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.SAKSLISTE, sakslister)
      .withRestCallRunner(RestApiPathsKeys.RESERVERTE_OPPGAVER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FLYTT_RESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<BehandlingskoerIndex
          fpsakUrl="www.fpsak.no"
          fptilbakeUrl="www.fptilbake.no"
          setValgtSakslisteId={sinon.spy()}
        />);

        const panel = wrapper.find(SakslistePanel);
        expect(panel).to.have.length(1);

        const oppgaveId = 1;
        const brukerIdent = 'T122334';
        const begrunnelse = 'Dette er en begrunnelse';
        const sakslisteId = 1;
        wrapper.setState({ sakslisteId });
        panel.prop('flyttReservasjon')(oppgaveId, brukerIdent, begrunnelse);

        done();

        expect(flyttReservasjonFn.calledOnce).to.be.true;
        const { args } = flyttReservasjonFn.getCalls()[0];
        expect(args).to.have.length(3);
        expect(args[0]).to.eql(oppgaveId);
        expect(args[1]).to.eql(brukerIdent);
        expect(args[2]).to.eql(begrunnelse);

        expect(fetchReserverteOppgaverFn.calledOnce).to.be.true;
        const { args: args2 } = fetchReserverteOppgaverFn.getCalls()[0];
        expect(args2).to.have.length(1);
        expect(args2[0]).to.eql(sakslisteId);
      });
  });

  it('skal vise dialog ved timeout', () => {
    new RestApiTestMocker()
      .withRestCall(RestApiPathsKeys.SAKSLISTE, sakslister)
      .withRestCallRunner(RestApiPathsKeys.RESERVERTE_OPPGAVER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FLYTT_RESERVASJON, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<BehandlingskoerIndex
          fpsakUrl="www.fpsak.no"
          fptilbakeUrl="www.fptilbake.no"
          setValgtSakslisteId={sinon.spy()}
        />);

        expect(wrapper.find(BehandlingPollingTimoutModal)).to.have.length(1);
      });
  });
});
