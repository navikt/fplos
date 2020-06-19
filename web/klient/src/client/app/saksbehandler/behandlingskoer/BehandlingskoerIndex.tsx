import React, {
  FunctionComponent, useState, useCallback, useEffect,
} from 'react';

import { RestApiPathsKeys } from 'data/restApiPaths';
import TimeoutError from 'data/rest-api/src/requestApi/error/TimeoutError';
import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import Saksliste from 'saksbehandler/behandlingskoer/sakslisteTsType';
import OppgaveStatus from 'saksbehandler/oppgaveStatusTsType';
import Oppgave from 'saksbehandler/oppgaveTsType';
import OppgaveErReservertAvAnnenModal from 'saksbehandler/components/OppgaveErReservertAvAnnenModal';
import { useRestApi, useRestApiRunner } from 'data/rest-api-hooks';
import SakslistePanel from './components/SakslistePanel';
import BehandlingPollingTimoutModal from './components/BehandlingPollingTimoutModal';

const EMPTY_ARRAY = [];

const openFagsak = (oppgave: Oppgave, hentFpsakInternBehandlingId: (param: { uuid: string}) => Promise<number>, fpsakUrl: string) => {
  hentFpsakInternBehandlingId({ uuid: oppgave.behandlingId }).then((behandlingId) => {
    window.location.assign(getFpsakHref(fpsakUrl, oppgave.saksnummer, behandlingId));
  });
};

const openTilbakesak = (oppgave: Oppgave, fptilbakeUrl: string) => {
  window.location.assign(getFptilbakeHref(fptilbakeUrl, oppgave.href));
};

const openSak = (oppgave: Oppgave, hentFpsakInternBehandlingId: (param: { uuid: string}) => Promise<number>, fpsakUrl: string, fptilbakeUrl: string) => {
  if (oppgave.system === 'FPSAK') openFagsak(oppgave, hentFpsakInternBehandlingId, fpsakUrl);
  else if (oppgave.system === 'FPTILBAKE') openTilbakesak(oppgave, fptilbakeUrl);
  else throw new Error('Fagsystemet for oppgaven er ukjent');
};

interface OwnProps {
  fpsakUrl: string;
  fptilbakeUrl: string;
  valgtSakslisteId?: number;
  setValgtSakslisteId: (sakslisteId: number) => void;
}

/**
 * BehandlingskoerIndex
 */
const BehandlingskoerIndex: FunctionComponent<OwnProps> = ({
  valgtSakslisteId,
  setValgtSakslisteId,
  fpsakUrl,
  fptilbakeUrl,
}) => {
  const [reservertAvAnnenSaksbehandler, setReservertAvAnnenSaksbehandler] = useState<boolean>(false);
  const [reservertOppgave, setReservertOppgave] = useState<Oppgave>();
  const [reservertOppgaveStatus, setReservertOppgaveStatus] = useState<OppgaveStatus>();

  const { data: sakslister = EMPTY_ARRAY } = useRestApi<Saksliste[]>(RestApiPathsKeys.SAKSLISTE);

  const { startRequest: hentReserverteOppgaver, data: reserverteOppgaver = EMPTY_ARRAY } = useRestApiRunner<Oppgave[]>(RestApiPathsKeys.RESERVERTE_OPPGAVER);
  const {
    startRequest: hentOppgaverTilBehandling, cancelRequest, data: oppgaverTilBehandling = EMPTY_ARRAY, error: hentOppgaverTilBehandlingError,
  } = useRestApiRunner<Oppgave[] | string>(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING);
  const { startRequest: reserverOppgave } = useRestApiRunner<OppgaveStatus>(RestApiPathsKeys.RESERVER_OPPGAVE);
  const { startRequest: hentFpsakInternBehandlingId } = useRestApiRunner<number>(RestApiPathsKeys.FPSAK_BEHANDLING_ID);

  useEffect(() => () => {
    if (valgtSakslisteId) {
      cancelRequest();
    }
  }, []);

  const fetchSakslisteOppgaverPolling = (nySakslisteId: number, oppgaveIder?: string) => {
    hentReserverteOppgaver({}, true);
    hentOppgaverTilBehandling(oppgaveIder ? { sakslisteId: nySakslisteId, oppgaveIder } : { sakslisteId: nySakslisteId }, true)
      .then((response) => (response !== 'INTERNAL_CANCELLATION' ? fetchSakslisteOppgaverPolling(nySakslisteId, response.map((o) => o.id).join(',')) : Promise.resolve()))
      .catch(() => undefined);
  };

  const fetchSakslisteOppgaver = useCallback((nySakslisteId: number) => {
    setValgtSakslisteId(nySakslisteId);
    hentReserverteOppgaver({}, true);
    hentOppgaverTilBehandling({ sakslisteId: nySakslisteId }, true)
      .then((response) => (response !== 'INTERNAL_CANCELLATION' ? fetchSakslisteOppgaverPolling(nySakslisteId, response.map((o) => o.id).join(',')) : Promise.resolve()));
  }, []);

  const reserverOppgaveOgApne = useCallback((oppgave: Oppgave) => {
    if (oppgave.status.erReservert) {
      openSak(oppgave, hentFpsakInternBehandlingId, fpsakUrl, fptilbakeUrl);
    } else {
      reserverOppgave({ oppgaveId: oppgave.id })
        .then((nyOppgaveStatus) => {
          if (nyOppgaveStatus.erReservert && nyOppgaveStatus.erReservertAvInnloggetBruker) {
            openSak(oppgave, hentFpsakInternBehandlingId, fpsakUrl, fptilbakeUrl);
          } else if (nyOppgaveStatus.erReservert && !nyOppgaveStatus.erReservertAvInnloggetBruker) {
            setReservertAvAnnenSaksbehandler(true);
            setReservertOppgave(oppgave);
            setReservertOppgaveStatus(nyOppgaveStatus);
          }
        });
    }
  }, [fpsakUrl, fptilbakeUrl]);

  const lukkErReservertModalOgOpneOppgave = useCallback((oppgave: Oppgave) => {
    setReservertAvAnnenSaksbehandler(false);
    setReservertOppgave(undefined);
    setReservertOppgaveStatus(undefined);

    openSak(oppgave, hentFpsakInternBehandlingId, fpsakUrl, fptilbakeUrl);
  }, [fpsakUrl, fptilbakeUrl]);

  if (sakslister.length === 0) {
    return null;
  }
  return (
    <>
      <SakslistePanel
        reserverOppgave={reserverOppgaveOgApne}
        sakslister={sakslister}
        fetchSakslisteOppgaver={fetchSakslisteOppgaver}
        reserverteOppgaver={reserverteOppgaver}
        oppgaverTilBehandling={oppgaverTilBehandling}
        hentReserverteOppgaver={hentReserverteOppgaver}
      />
      {hentOppgaverTilBehandlingError instanceof TimeoutError
        && <BehandlingPollingTimoutModal />}
      {reservertAvAnnenSaksbehandler && reservertOppgave && reservertOppgaveStatus && (
        <OppgaveErReservertAvAnnenModal
          lukkErReservertModalOgOpneOppgave={lukkErReservertModalOgOpneOppgave}
          oppgave={reservertOppgave}
          oppgaveStatus={reservertOppgaveStatus}
        />
      )}
    </>
  );
};

export default BehandlingskoerIndex;
