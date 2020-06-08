import React, {
  useState, FunctionComponent, useEffect,
} from 'react';

import { errorOfType, ErrorTypes, getErrorResponseData } from 'app/ErrorTypes';
import { RestApiPathsKeys } from 'data/restApiPaths';
import useRestApiRunner from 'data/rest-api-hooks/useRestApiRunner';
import OppgaveErReservertAvAnnenModal from 'saksbehandler/components/OppgaveErReservertAvAnnenModal';
import Fagsak from 'saksbehandler/fagsakSearch/fagsakTsType';
import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import OppgaveStatus from 'saksbehandler/oppgaveStatusTsType';
import Oppgave from 'saksbehandler/oppgaveTsType';
import FagsakSearch from './components/FagsakSearch';

const getGoToFpsakFn = (fpsakUrl: string) => (saksnummer: number, behandlingId?: number) => {
  window.location.assign(getFpsakHref(fpsakUrl, saksnummer, behandlingId));
};

const getGoToTilbakesakFn = (fptilbakeUrl: string) => (path: string) => {
  window.location.assign(getFptilbakeHref(fptilbakeUrl, path));
};

interface OwnProps {
  fpsakUrl: string;
  fptilbakeUrl: string;
}

/**
 * FagsakSearchIndex
 *
 * Container komponent. Har ansvar for å vise søkeskjermbildet og å håndtere fagsaksøket
 * mot server og lagringen av resultatet i klientens state.
 */
const FagsakSearchIndex: FunctionComponent<OwnProps> = ({
  fpsakUrl,
  fptilbakeUrl,
}) => {
  const goToFpsak = getGoToFpsakFn(fpsakUrl);
  const goToTilbakesak = getGoToTilbakesakFn(fptilbakeUrl);

  const [skalReservere, setSkalReservere] = useState(false);
  const [reservertAvAnnenSaksbehandler, setReservertAvAnnenSaksbehandler] = useState(false);
  const [reservertOppgave, setReservertOppgave] = useState<Oppgave>();
  const [sokStartet, setSokStartet] = useState(false);
  const [sokFerdig, setSokFerdig] = useState(false);

  const { startRequest: reserverOppgave } = useRestApiRunner<OppgaveStatus>(RestApiPathsKeys.RESERVER_OPPGAVE);
  const {
    startRequest: sokFagsak, resetRequestData: resetFagsakSok, data: fagsaker = [], error: fagsakError,
  } = useRestApiRunner<Fagsak[]>(RestApiPathsKeys.SEARCH_FAGSAK);
  const { startRequest: hentOppgaverForFagsaker, data: fagsakOppgaver } = useRestApiRunner<Oppgave[]>(RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER);
  const { startRequest: hentReservasjonsstatus } = useRestApiRunner<OppgaveStatus>(RestApiPathsKeys.HENT_RESERVASJONSSTATUS);
  const { startRequest: hentFpsakInternBehandlingId } = useRestApiRunner<number>(RestApiPathsKeys.FPSAK_BEHANDLING_ID);

  const searchResultAccessDenied = fagsakError && errorOfType(fagsakError, ErrorTypes.MANGLER_TILGANG_FEIL) ? getErrorResponseData(fagsakError) : undefined;

  useEffect(() => {
    if (sokFerdig && fagsaker.length === 1) {
      if (fagsakOppgaver.length === 1) {
        // eslint-disable-next-line no-use-before-define
        velgFagsakOperasjoner(fagsakOppgaver[0], false);
      } else if (fagsakOppgaver.length === 0) {
        goToFpsak(fagsaker[0].saksnummer);
      }
    }

    return () => {
      resetFagsakSok();
    };
  });

  const goToFagsakEllerApneModal = (oppgave: Oppgave, oppgaveStatus: OppgaveStatus) => {
    if (!oppgaveStatus.erReservert || (oppgaveStatus.erReservert && oppgaveStatus.erReservertAvInnloggetBruker)) {
      if (oppgave.system === 'FPSAK') {
        hentFpsakInternBehandlingId(oppgave.behandlingId).then((behandlingId) => {
          goToFpsak(oppgave.saksnummer, behandlingId);
        });
      } else if (oppgave.system === 'FPTILBAKE') {
        goToTilbakesak(oppgave.href);
      } else throw new Error('Fagsystemet for oppgaven er ukjent');
    } else if (oppgaveStatus.erReservert && !oppgaveStatus.erReservertAvInnloggetBruker) {
      setReservertOppgave(oppgave);
      setReservertAvAnnenSaksbehandler(true);
    }
  };

  const velgFagsakOperasjoner = (oppgave: Oppgave, skalSjekkeOmReservert: boolean) => {
    if (oppgave.status.erReservert && !oppgave.status.erReservertAvInnloggetBruker) {
      setReservertOppgave(oppgave);
      setReservertAvAnnenSaksbehandler(true);
    } else if (!skalReservere) {
      if (skalSjekkeOmReservert) {
        hentReservasjonsstatus({ oppgaveId: oppgave.id }).then((status) => {
          goToFagsakEllerApneModal(oppgave, status);
        });
      } else if (oppgave.system === 'FPSAK') {
        hentFpsakInternBehandlingId(oppgave.behandlingId).then((behandlingId) => {
          goToFpsak(oppgave.saksnummer, behandlingId);
        });
      } else if (oppgave.system === 'FPTILBAKE') {
        goToTilbakesak(oppgave.href);
      } else throw new Error('Fagsystemet for oppgaven er ukjent');
    } else {
      reserverOppgave(oppgave.id).then((data) => {
        goToFagsakEllerApneModal(oppgave, data);
      });
    }
  };

  const reserverOppgaveOgApne = (oppgave: Oppgave) => {
    velgFagsakOperasjoner(oppgave, true);
  };

  const sokFagsakFn = (values: {searchString: string; skalReservere: boolean}) => {
    setSkalReservere(values.skalReservere);
    setSokStartet(true);
    setSokFerdig(false);

    return sokFagsak(values).then((fagsakerResultat) => {
      if (fagsakerResultat.length > 0) {
        hentOppgaverForFagsaker({ saksnummerListe: fagsakerResultat.map((fagsak) => `${fagsak.saksnummer}`).join(',') })
          .then(() => {
            setSokStartet(false);
            setSokFerdig(true);
          });
      } else {
        setSokStartet(false);
        setSokFerdig(true);
      }
    });
  };

  const lukkErReservertModalOgOpneOppgave = (oppgave: Oppgave) => {
    setReservertOppgave(undefined);
    setReservertAvAnnenSaksbehandler(false);
    if (oppgave.system === 'FPSAK') {
      hentFpsakInternBehandlingId(oppgave.behandlingId).then((behandlingId) => {
        goToFpsak(oppgave.saksnummer, behandlingId);
      });
    } else if (oppgave.system === 'FPTILBAKE') {
      window.location.assign(oppgave.href);
    } else throw new Error('Fagsystemet for oppgaven er ukjent');
  };

  const resetSearchFn = () => {
    resetFagsakSok();
    setSokStartet(false);
    setSokFerdig(false);
  };

  return (
    <>
      <FagsakSearch
        fagsaker={fagsaker || []}
        fagsakOppgaver={fagsakOppgaver || []}
        searchFagsakCallback={sokFagsakFn}
        searchResultReceived={sokFerdig}
        selectFagsakCallback={goToFpsak}
        selectOppgaveCallback={reserverOppgaveOgApne}
        searchStarted={sokStartet}
        searchResultAccessDenied={searchResultAccessDenied}
        resetSearch={resetSearchFn}
      />
      {reservertAvAnnenSaksbehandler && reservertOppgave && (
      <OppgaveErReservertAvAnnenModal
        lukkErReservertModalOgOpneOppgave={lukkErReservertModalOgOpneOppgave}
        oppgave={reservertOppgave}
        oppgaveStatus={reservertOppgave.status}
      />
      )}
    </>
  );
};

export default FagsakSearchIndex;
