import React, {
  FunctionComponent, useState, useCallback, useEffect,
} from 'react';

import { RestApiPathsKeys } from 'data/restApiPaths';
import useRestApiRunner from 'data/rest-api-hooks/useRestApiRunner';
import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import Saksliste from 'saksbehandler/behandlingskoer/sakslisteTsType';
import OppgaveStatus from 'saksbehandler/oppgaveStatusTsType';
import Oppgave from 'saksbehandler/oppgaveTsType';
import OppgaveErReservertAvAnnenModal from 'saksbehandler/components/OppgaveErReservertAvAnnenModal';
import useRestApi from 'data/rest-api-hooks/useRestApi';
import SakslistePanel from './components/SakslistePanel';
import BehandlingPollingTimoutModal from './components/BehandlingPollingTimoutModal';

const EMPTY_ARRAY = [];

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
    startRequest: hentOppgaverTilBehandling, requestApi, data: oppgaverTilBehandling = EMPTY_ARRAY,
  } = useRestApiRunner<Oppgave[]>(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING);
  const { startRequest: reserverOppgave } = useRestApiRunner<OppgaveStatus>(RestApiPathsKeys.RESERVER_OPPGAVE);
  const { startRequest: opphevOppgavereservasjon } = useRestApiRunner<Oppgave[]>(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON);
  const { startRequest: forlengOppgavereservasjon } = useRestApiRunner<Oppgave[]>(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON);
  const { startRequest: endreOppgavereservasjon } = useRestApiRunner<Oppgave[]>(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON);
  const { startRequest: flyttOppgavereservasjon } = useRestApiRunner<Oppgave[]>(RestApiPathsKeys.FLYTT_RESERVASJON);
  const { startRequest: hentFpsakInternBehandlingId } = useRestApiRunner<number>(RestApiPathsKeys.FPSAK_BEHANDLING_ID);

  const goToUrl = useCallback((url) => window.location.assign(url), []);

  // FIXME finn timeout og vis denne fram
  // const { state: harTimeout } = useRestApiData<Oppgave[]>(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING);
  const harTimeout = false;

  useEffect(() => () => {
    if (valgtSakslisteId) {
      requestApi.cancelRequest();
    }
  }, []);

  const fetchSakslisteOppgaverPolling = (nySakslisteId: number, oppgaveIder?: string) => {
    hentReserverteOppgaver();
    hentOppgaverTilBehandling(oppgaveIder ? { sakslisteId: nySakslisteId, oppgaveIder } : { sakslisteId: nySakslisteId })
      .then((response) => fetchSakslisteOppgaverPolling(nySakslisteId, response.map((o) => o.id).join(','))).catch(() => undefined);
  };

  const fetchSakslisteOppgaver = (nySakslisteId: number) => {
    setValgtSakslisteId(nySakslisteId);
    hentReserverteOppgaver();
    hentOppgaverTilBehandling({ sakslisteId: nySakslisteId })
      .then((response) => fetchSakslisteOppgaverPolling(nySakslisteId, response.map((o) => o.id).join(',')));
  };

  const openFagsak = (oppgave: Oppgave) => {
    hentFpsakInternBehandlingId({ uuid: oppgave.behandlingId }).then((behandlingId) => {
      goToUrl(getFpsakHref(fpsakUrl, oppgave.saksnummer, behandlingId));
    });
  };

  const openTilbakesak = (oppgave: Oppgave) => {
    goToUrl(getFptilbakeHref(fptilbakeUrl, oppgave.href));
  };

  const openSak = (oppgave: Oppgave) => {
    if (oppgave.system === 'FPSAK') openFagsak(oppgave);
    else if (oppgave.system === 'FPTILBAKE') openTilbakesak(oppgave);
    else throw new Error('Fagsystemet for oppgaven er ukjent');
  };

  const reserverOppgaveOgApne = (oppgave: Oppgave) => {
    if (oppgave.status.erReservert) {
      openSak(oppgave);
    } else {
      reserverOppgave({ oppgaveId: oppgave.id })
        .then((nyOppgaveStatus) => {
          if (nyOppgaveStatus.erReservert && nyOppgaveStatus.erReservertAvInnloggetBruker) {
            openSak(oppgave);
          } else if (nyOppgaveStatus.erReservert && !nyOppgaveStatus.erReservertAvInnloggetBruker) {
            setReservertAvAnnenSaksbehandler(true);
            setReservertOppgave(oppgave);
            setReservertOppgaveStatus(nyOppgaveStatus);
          }
        });
    }
  };

  const opphevReservasjonFn = (oppgaveId: number, begrunnelse: string): Promise<any> => {
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return opphevOppgavereservasjon({ oppgaveId, begrunnelse })
      .then(() => hentReserverteOppgaver());
  };

  const forlengOppgaveReservasjonFn = (oppgaveId: number): Promise<any> => {
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return forlengOppgavereservasjon({ oppgaveId })
      .then(() => hentReserverteOppgaver());
  };

  const endreOppgaveReservasjonFn = (oppgaveId: number, reserverTil: string): Promise<any> => {
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return endreOppgavereservasjon({ oppgaveId, reserverTil })
      .then(() => hentReserverteOppgaver());
  };

  const flyttReservasjonFn = (oppgaveId: number, brukerident: string, begrunnelse: string): Promise<any> => {
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return flyttOppgavereservasjon({ oppgaveId, brukerIdent: brukerident, begrunnelse })
      .then(() => hentReserverteOppgaver());
  };

  const lukkErReservertModalOgOpneOppgave = (oppgave: Oppgave) => {
    setReservertAvAnnenSaksbehandler(false);
    setReservertOppgave(undefined);
    setReservertOppgaveStatus(undefined);

    openSak(oppgave);
  };

  if (sakslister.length === 0) {
    return null;
  }
  return (
    <>
      <SakslistePanel
        reserverOppgave={reserverOppgaveOgApne}
        sakslister={sakslister}
        fetchSakslisteOppgaver={fetchSakslisteOppgaver}
        opphevOppgaveReservasjon={opphevReservasjonFn}
        forlengOppgaveReservasjon={forlengOppgaveReservasjonFn}
        endreOppgaveReservasjon={endreOppgaveReservasjonFn}
        flyttReservasjon={flyttReservasjonFn}
        reserverteOppgaver={reserverteOppgaver}
        oppgaverTilBehandling={oppgaverTilBehandling}
      />
      {harTimeout
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
