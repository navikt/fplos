import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import fpLosApi from 'data/fpLosApi';
import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import Saksliste from 'saksbehandler/behandlingskoer/sakslisteTsType';
import {
  getFpsakUrl,
  getFptilbakeUrl,
  hentFpsakInternBehandlingId as hentFpsakInternBehandlingIdActionCreator,
} from 'app/duck';
import OppgaveStatus from 'saksbehandler/oppgaveStatusTsType';
import Oppgave from 'saksbehandler/oppgaveTsType';
import OppgaveErReservertAvAnnenModal from 'saksbehandler/components/OppgaveErReservertAvAnnenModal';
import {
  fetchAlleSakslister, getSakslisteResult, fetchOppgaverTilBehandling, fetchReserverteOppgaver, reserverOppgave, opphevOppgaveReservasjon,
  forlengOppgaveReservasjon, endreOppgaveReservasjon, fetchOppgaverTilBehandlingOppgaver, flyttReservasjon, setValgtSakslisteId,
  harOppgaverTilBehandlingTimeout,
} from './duck';
import SakslistePanel from './components/SakslistePanel';
import BehandlingPollingTimoutModal from './components/BehandlingPollingTimoutModal';

interface OwnProps {
  sakslister: Saksliste[];
  fpsakUrl: string;
  fptilbakeUrl: string;
  goToUrl: (url: string) => void;
  harTimeout: boolean;
}

interface DispatchProps {
  fetchAlleSakslister: () => void;
  fetchOppgaverTilBehandling: (sakslisteId: number) => Promise<{payload: any }>;
  fetchOppgaverTilBehandlingOppgaver: (sakslisteId: number, oppgaveIder?: string) => Promise<{payload: any }>;
  fetchReserverteOppgaver: (sakslisteId: number) => Promise<{payload: any }>;
  reserverOppgave: (oppgaveId: number) => Promise<{payload: OppgaveStatus }>;
  opphevOppgaveReservasjon: (oppgaveId: number, begrunnelse: string) => Promise<string>;
  forlengOppgaveReservasjon: (oppgaveId: number) => Promise<string>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
  setValgtSakslisteId: (sakslisteId: number) => void;
  hentFpsakInternBehandlingId: (uuid: string) => Promise<{payload: number }>;
}

interface StateProps {
  sakslisteId?: number;
  reservertAvAnnenSaksbehandler: boolean;
  reservertOppgave?: Oppgave;
  reservertOppgaveStatus?: OppgaveStatus;
}
/**
 * BehandlingskoerIndex
 */
export class BehandlingskoerIndex extends Component<OwnProps & DispatchProps, StateProps> {
  state = {
    sakslisteId: undefined, reservertAvAnnenSaksbehandler: false, reservertOppgave: undefined, reservertOppgaveStatus: undefined,
  };

  static defaultProps = {
    sakslister: [],
  }

  componentDidMount = () => {
    const { fetchAlleSakslister: getSakslister } = this.props;
    getSakslister();
  }

  componentWillUnmount = () => {
    const { sakslisteId: id } = this.state;
    if (id) {
      fpLosApi.OPPGAVER_TIL_BEHANDLING.cancelRestApiRequest();
    }
  }

  fetchSakslisteOppgaverPolling = (sakslisteId: number, oppgaveIder?: string) => {
    const { fetchOppgaverTilBehandlingOppgaver: fetchTilBehandling, fetchReserverteOppgaver: fetchReserverte } = this.props;
    fetchReserverte(sakslisteId);
    fetchTilBehandling(sakslisteId, oppgaveIder).then((response) => {
      const { sakslisteId: id } = this.state;
      return sakslisteId === id ? this.fetchSakslisteOppgaverPolling(sakslisteId, response.payload.map((o) => o.id).join(',')) : Promise.resolve();
    }).catch(() => undefined);
  }

  fetchSakslisteOppgaver = (sakslisteId: number) => {
    this.setState((prevState) => ({ ...prevState, sakslisteId }));
    const { fetchOppgaverTilBehandling: fetchTilBehandling, fetchReserverteOppgaver: fetchReserverte, setValgtSakslisteId: setSakslisteId } = this.props;
    setSakslisteId(sakslisteId);
    fetchReserverte(sakslisteId);
    fetchTilBehandling(sakslisteId).then((response) => {
      const { sakslisteId: id } = this.state;
      return sakslisteId === id ? this.fetchSakslisteOppgaverPolling(sakslisteId, response.payload.map((o) => o.id).join(',')) : Promise.resolve();
    });
  }

  openSak = (oppgave: Oppgave) => {
    if (oppgave.system === 'FPSAK') this.openFagsak(oppgave);
    else if (oppgave.system === 'FPTILBAKE') this.openTilbakesak(oppgave);
    else throw new Error('Fagsystemet for oppgaven er ukjent');
  }

  openFagsak = (oppgave: Oppgave) => {
    const { fpsakUrl, goToUrl, hentFpsakInternBehandlingId } = this.props;
    hentFpsakInternBehandlingId(oppgave.behandlingId).then((data: {payload: number }) => {
      goToUrl(getFpsakHref(fpsakUrl, oppgave.saksnummer, data.payload));
    });
  }

  openTilbakesak = (oppgave: Oppgave) => {
    const { fptilbakeUrl, goToUrl } = this.props;
    goToUrl(getFptilbakeHref(fptilbakeUrl, oppgave.href));
  }

  reserverOppgaveOgApne = (oppgave: Oppgave) => {
    if (oppgave.status.erReservert) {
      this.openSak(oppgave);
    } else {
      const { reserverOppgave: reserver } = this.props;

      reserver(oppgave.id).then((data: {payload: OppgaveStatus }) => {
        const nyOppgaveStatus = data.payload;
        if (nyOppgaveStatus.erReservert && nyOppgaveStatus.erReservertAvInnloggetBruker) {
          this.openSak(oppgave);
        } else if (nyOppgaveStatus.erReservert && !nyOppgaveStatus.erReservertAvInnloggetBruker) {
          this.setState((prevState) => ({
            ...prevState,
            reservertAvAnnenSaksbehandler: true,
            reservertOppgave: oppgave,
            reservertOppgaveStatus: nyOppgaveStatus,
          }));
        }
      });
    }
  }

  opphevReservasjon = (oppgaveId: number, begrunnelse: string): Promise<any> => {
    const { opphevOppgaveReservasjon: opphevReservasjon, fetchReserverteOppgaver: fetchReserverte } = this.props;
    const { sakslisteId } = this.state;
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return opphevReservasjon(oppgaveId, begrunnelse)
      .then(() => fetchReserverte(sakslisteId));
  }

  forlengOppgaveReservasjon = (oppgaveId: number): Promise<any> => {
    const { forlengOppgaveReservasjon: forlengReservasjon, fetchReserverteOppgaver: fetchReserverte } = this.props;
    const { sakslisteId } = this.state;
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return forlengReservasjon(oppgaveId)
      .then(() => fetchReserverte(sakslisteId));
  }

  endreOppgaveReservasjon = (oppgaveId: number, reserverTil: string): Promise<any> => {
    const { endreOppgaveReservasjon: endreReservasjon, fetchReserverteOppgaver: fetchReserverte } = this.props;
    const { sakslisteId } = this.state;
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return endreReservasjon(oppgaveId, reserverTil)
      .then(() => fetchReserverte(sakslisteId));
  }

  flyttReservasjon = (oppgaveId: number, brukerident: string, begrunnelse: string): Promise<any> => {
    const { flyttReservasjon: flytt, fetchReserverteOppgaver: fetchReserverte } = this.props;
    const { sakslisteId } = this.state;
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return flytt(oppgaveId, brukerident, begrunnelse)
      .then(() => fetchReserverte(sakslisteId));
  }

  lukkErReservertModalOgOpneOppgave = (oppgave: Oppgave) => {
    this.setState((prevState) => ({
      ...prevState, reservertAvAnnenSaksbehandler: false, reservertOppgave: undefined, reservertOppgaveStatus: undefined,
    }));
    this.openSak(oppgave);
  }

  render = () => {
    const {
      sakslister, harTimeout,
    } = this.props;
    const {
      reservertAvAnnenSaksbehandler, reservertOppgave, reservertOppgaveStatus,
    } = this.state;
    if (sakslister.length === 0) {
      return null;
    }
    return (
      <>
        <SakslistePanel
          reserverOppgave={this.reserverOppgaveOgApne}
          sakslister={sakslister}
          fetchSakslisteOppgaver={this.fetchSakslisteOppgaver}
          opphevOppgaveReservasjon={this.opphevReservasjon}
          forlengOppgaveReservasjon={this.forlengOppgaveReservasjon}
          endreOppgaveReservasjon={this.endreOppgaveReservasjon}
          flyttReservasjon={this.flyttReservasjon}
        />
        {harTimeout
          && <BehandlingPollingTimoutModal />}
        {reservertAvAnnenSaksbehandler && reservertOppgave && reservertOppgaveStatus && (
          <OppgaveErReservertAvAnnenModal
            lukkErReservertModalOgOpneOppgave={this.lukkErReservertModalOgOpneOppgave}
            oppgave={reservertOppgave}
            oppgaveStatus={reservertOppgaveStatus}
          />
        )}
      </>
    );
  }
}

const mapStateToProps = (state) => ({
  fpsakUrl: getFpsakUrl(state),
  fptilbakeUrl: getFptilbakeUrl(state),
  harTimeout: harOppgaverTilBehandlingTimeout(state),
  sakslister: getSakslisteResult(state),
  goToUrl: (url) => window.location.assign(url),
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    fetchAlleSakslister,
    fetchOppgaverTilBehandling,
    fetchOppgaverTilBehandlingOppgaver,
    fetchReserverteOppgaver,
    reserverOppgave,
    opphevOppgaveReservasjon,
    forlengOppgaveReservasjon,
    endreOppgaveReservasjon,
    flyttReservasjon,
    setValgtSakslisteId,
    hentFpsakInternBehandlingId: hentFpsakInternBehandlingIdActionCreator,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(BehandlingskoerIndex);
