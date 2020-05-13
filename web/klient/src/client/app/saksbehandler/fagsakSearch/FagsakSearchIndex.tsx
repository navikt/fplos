import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import OppgaveErReservertAvAnnenModal from 'saksbehandler/components/OppgaveErReservertAvAnnenModal';
import Fagsak from 'saksbehandler/fagsakSearch/fagsakTsType';
import { hentFpsakInternBehandlingId as hentFpsakInternBehandlingIdActionCreator, getFpsakUrl, getFptilbakeUrl } from 'app/duck';
import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import {
  reserverOppgave as reserverOppgaveActionCreator, hentReservasjonsstatus as hentReservasjonActionCreator,
} from 'saksbehandler/behandlingskoer/duck';
import OppgaveStatus from 'saksbehandler/oppgaveStatusTsType';
import Oppgave from 'saksbehandler/oppgaveTsType';
import {
  searchFagsaker, resetFagsakSearch, hentOppgaverForFagsaker as hentOppgaverForFagsakerActionCreator,
} from './duck';
import {
  getFagsaker,
  getFagsakOppgaver,
  getSearchFagsakerAccessDenied,
} from './fagsakSearchSelectors';
import FagsakSearch from './components/FagsakSearch';

interface SearchResultAccessDenied {
  feilmelding?: string;
}

type Props = Readonly<{
  fagsaker: Fagsak[];
  fagsakOppgaver: Oppgave[];
  searchFagsaker: ({ searchString: string, skalReservere: boolean }) => void;
  searchResultAccessDenied?: SearchResultAccessDenied;
  resetFagsakSearch: () => void;
  goToFpsak: (saknummer: number, behandlingId?: number) => void;
  goToTilbakesak: (path: string) => void;
  reserverOppgave: (oppgaveId: number) => Promise<{payload: OppgaveStatus }>;
  hentReservasjonsstatus: (oppgaveId: number) => Promise<{payload: OppgaveStatus }>;
  hentOppgaverForFagsaker: (fagsaker: Fagsak[]) => Promise<{payload: Oppgave[] }>;
  hentFpsakInternBehandlingId: (behandlingId: string) => Promise<{payload: number }>;
}>;

interface StateProps {
  skalReservere: boolean;
  reservertAvAnnenSaksbehandler: boolean;
  reservertOppgave?: Oppgave;
  sokStartet: boolean;
  sokFerdig: boolean;
}

/** s
 * FagsakSearchIndex
 *
 * Container komponent. Har ansvar for å vise søkeskjermbildet og å håndtere fagsaksøket
 * mot server og lagringen av resultatet i klientens state.
 */
export class FagsakSearchIndex extends Component<Props, StateProps> {
   state = {
     skalReservere: false,
     reservertAvAnnenSaksbehandler: false,
     reservertOppgave: undefined,
     sokStartet: false,
     sokFerdig: false,
   };

  static defaultProps = {
    fagsaker: [],
    searchResultAccessDenied: undefined,
  };

  componentDidUpdate = (prevProps: Props, prevState: StateProps) => {
    const {
      fagsaker, fagsakOppgaver, goToFpsak,
    } = this.props;
    const { sokFerdig } = this.state;
    if (sokFerdig && !prevState.sokFerdig && fagsaker.length === 1) {
      if (fagsakOppgaver.length === 1) {
        this.velgFagsakOperasjoner(fagsakOppgaver[0], false);
      } else if (fagsakOppgaver.length === 0) {
        goToFpsak(fagsaker[0].saksnummer);
      }
    }
  }

  componentWillUnmount = () => {
    const { resetFagsakSearch: resetSearch } = this.props;
    resetSearch();
  }

  goToFagsakEllerApneModal = (oppgave: Oppgave, oppgaveStatus: OppgaveStatus) => {
    const { goToFpsak, goToTilbakesak, hentFpsakInternBehandlingId } = this.props;
    if (!oppgaveStatus.erReservert || (oppgaveStatus.erReservert && oppgaveStatus.erReservertAvInnloggetBruker)) {
      if (oppgave.system === 'FPSAK') {
        hentFpsakInternBehandlingId(oppgave.behandlingId).then((data: {payload: number }) => {
          goToFpsak(oppgave.saksnummer, data.payload);
        });
      } else if (oppgave.system === 'FPTILBAKE') {
        goToTilbakesak(oppgave.href);
      } else throw new Error('Fagsystemet for oppgaven er ukjent');
    } else if (oppgaveStatus.erReservert && !oppgaveStatus.erReservertAvInnloggetBruker) {
      this.setState((prevState) => ({ ...prevState, reservertAvAnnenSaksbehandler: true, reservertOppgave: oppgave }));
    }
  }

  velgFagsakOperasjoner = (oppgave: Oppgave, skalSjekkeOmReservert: boolean) => {
    const {
      goToFpsak, goToTilbakesak, reserverOppgave, hentReservasjonsstatus, hentFpsakInternBehandlingId,
    } = this.props;
    const { skalReservere } = this.state;

    if (oppgave.status.erReservert && !oppgave.status.erReservertAvInnloggetBruker) {
      this.setState((prevState) => ({ ...prevState, reservertAvAnnenSaksbehandler: true, reservertOppgave: oppgave }));
    } else if (!skalReservere) {
      if (skalSjekkeOmReservert) {
        hentReservasjonsstatus(oppgave.id).then((data: {payload: OppgaveStatus }) => {
          this.goToFagsakEllerApneModal(oppgave, data.payload);
        });
      } else if (oppgave.system === 'FPSAK') {
        hentFpsakInternBehandlingId(oppgave.behandlingId).then((data: {payload: number }) => {
          goToFpsak(oppgave.saksnummer, data.payload);
        });
      } else if (oppgave.system === 'FPTILBAKE') {
        goToTilbakesak(oppgave.href);
      } else throw new Error('Fagsystemet for oppgaven er ukjent');
    } else {
      reserverOppgave(oppgave.id).then((data: {payload: OppgaveStatus }) => {
        this.goToFagsakEllerApneModal(oppgave, data.payload);
      });
    }
  }

  reserverOppgaveOgApne = (oppgave: Oppgave) => {
    this.velgFagsakOperasjoner(oppgave, true);
  }

  sokFagsak = (values: {searchString: string; skalReservere: boolean}) => {
    const {
      searchFagsaker: search, hentOppgaverForFagsaker,
    } = this.props;

    this.setState((prevState) => ({
      ...prevState, skalReservere: values.skalReservere, sokStartet: true, sokFerdig: false,
    }));

    return search(values).then((data: {payload: Fagsak[] }) => {
      const fagsaker = data.payload;
      if (fagsaker.length > 0) {
        hentOppgaverForFagsaker(fagsaker).then(() => {
          this.setState((prevState) => ({ ...prevState, sokStartet: false, sokFerdig: true }));
        });
      } else {
        this.setState((prevState) => ({ ...prevState, sokStartet: false, sokFerdig: true }));
      }
    });
  }

  lukkErReservertModalOgOpneOppgave = (oppgave: Oppgave) => {
    const { goToFpsak, hentFpsakInternBehandlingId } = this.props;
    this.setState((prevState) => ({
      ...prevState, reservertAvAnnenSaksbehandler: false, reservertOppgave: undefined,
    }));
    if (oppgave.system === 'FPSAK') {
      hentFpsakInternBehandlingId(oppgave.behandlingId).then((data: {payload: number }) => {
        goToFpsak(oppgave.saksnummer, data.payload);
      });
    } else if (oppgave.system === 'FPTILBAKE') {
      window.location.assign(oppgave.href);
    } else throw new Error('Fagsystemet for oppgaven er ukjent');
  }

  resetSearch = () => {
    const { resetFagsakSearch: resetSearch } = this.props;
    resetSearch();
    this.setState((prevState) => ({ ...prevState, sokStartet: false, sokFerdig: false }));
  }

  render = () => {
    const {
      fagsaker, fagsakOppgaver, searchResultAccessDenied, goToFpsak,
    } = this.props;
    const {
      reservertAvAnnenSaksbehandler, reservertOppgave, sokStartet, sokFerdig,
    } = this.state;
    return (
      <>
        <FagsakSearch
          fagsaker={fagsaker || []}
          fagsakOppgaver={fagsakOppgaver || []}
          searchFagsakCallback={this.sokFagsak}
          searchResultReceived={sokFerdig}
          selectFagsakCallback={goToFpsak}
          selectOppgaveCallback={this.reserverOppgaveOgApne}
          searchStarted={sokStartet}
          searchResultAccessDenied={searchResultAccessDenied}
          resetSearch={this.resetSearch}
        />
        {reservertAvAnnenSaksbehandler && reservertOppgave && (
        <OppgaveErReservertAvAnnenModal
          lukkErReservertModalOgOpneOppgave={this.lukkErReservertModalOgOpneOppgave}
          oppgave={reservertOppgave}
          oppgaveStatus={reservertOppgave.status}
        />
        )}
      </>
    );
  }
}

const getGoToFpsakFn = (fpsakUrl) => (saksnummer, behandlingId) => {
  window.location.assign(getFpsakHref(fpsakUrl, saksnummer, behandlingId));
};

const getGoToTilbakesakFn = (fptilbakeUrl) => (path) => {
  window.location.assign(getFptilbakeHref(fptilbakeUrl, path));
};


const mapStateToProps = (state) => ({
  fagsaker: getFagsaker(state),
  fagsakOppgaver: getFagsakOppgaver(state),
  searchResultAccessDenied: getSearchFagsakerAccessDenied(state),
  goToFpsak: getGoToFpsakFn(getFpsakUrl(state)),
  goToTilbakesak: getGoToTilbakesakFn(getFptilbakeUrl(state)),
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  ...bindActionCreators({
    searchFagsaker,
    resetFagsakSearch,
    reserverOppgave: reserverOppgaveActionCreator,
    hentReservasjonsstatus: hentReservasjonActionCreator,
    hentOppgaverForFagsaker: hentOppgaverForFagsakerActionCreator,
    hentFpsakInternBehandlingId: hentFpsakInternBehandlingIdActionCreator,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(FagsakSearchIndex);
