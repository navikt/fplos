import React, { FunctionComponent, useState, useCallback } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import { RestApiPathsKeys } from 'data/restApiPaths';
import useRestApiRunner from 'data/useRestApiRunner';
import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import Saksliste from 'saksbehandler/behandlingskoer/sakslisteTsType';
import hentFpsakInternBehandlingIdActionCreator from 'app/duck';
import OppgaveStatus from 'saksbehandler/oppgaveStatusTsType';
import Oppgave from 'saksbehandler/oppgaveTsType';
import OppgaveErReservertAvAnnenModal from 'saksbehandler/components/OppgaveErReservertAvAnnenModal';
import useRestApi from 'data/useRestApi';
import {
  setValgtSakslisteId,
} from './duck';
import SakslistePanel from './components/SakslistePanel';
import BehandlingPollingTimoutModal from './components/BehandlingPollingTimoutModal';

interface OwnProps {
  sakslister: Saksliste[];
  fpsakUrl: string;
  fptilbakeUrl: string;
}

interface DispatchProps {
  setValgtSakslisteId: (sakslisteId: number) => void;
  hentFpsakInternBehandlingId: (uuid: string) => Promise<{payload: number }>;
}

/**
 * BehandlingskoerIndex
 */
const BehandlingskoerIndex: FunctionComponent<OwnProps & DispatchProps> = ({
  fpsakUrl,
  fptilbakeUrl,
  hentFpsakInternBehandlingId,
}) => {
  const [sakslisteId, setSakslisteId] = useState<number>();
  const [reservertAvAnnenSaksbehandler, setReservertAvAnnenSaksbehandler] = useState<boolean>(false);
  const [reservertOppgave, setReservertOppgave] = useState<Oppgave>();
  const [reservertOppgaveStatus, setReservertOppgaveStatus] = useState<OppgaveStatus>();

  const { data: sakslister = [] } = useRestApi<Saksliste[]>(RestApiPathsKeys.SAKSLISTE);
  const runRequest = useRestApiRunner();

  const goToUrl = useCallback((url) => window.location.assign(url), []);

  // FIXME finn timeout og vis denne fram
  // const { state: harTimeout } = useRestApiData<Oppgave[]>(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING);
  const harTimeout = false;

  // FIXME Avbryt polling
  /* componentWillUnmount = () => {
    const { sakslisteId: id } = this.state;
    if (id) {
      fpLosApi.OPPGAVER_TIL_BEHANDLING.cancelRestApiRequest();
    }
  }; */

  const fetchReserverteOppgaver = () => runRequest(RestApiPathsKeys.RESERVERTE_OPPGAVER, undefined, { keepData: true });

  const fetchSakslisteOppgaverPolling = (nySakslisteId: number, oppgaveIder?: string) => {
    fetchReserverteOppgaver();
    runRequest<Oppgave[]>(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING,
      oppgaveIder ? { sakslisteId: nySakslisteId, oppgaveIder } : { sakslisteId: nySakslisteId }, { keepData: true })
      .then((response) => (nySakslisteId === sakslisteId
        ? fetchSakslisteOppgaverPolling(nySakslisteId, response.map((o) => o.id).join(',')) : Promise.resolve())).catch(() => undefined);
  };

  const fetchSakslisteOppgaver = (nySakslisteId: number) => {
    setSakslisteId(nySakslisteId);
    fetchReserverteOppgaver();
    runRequest<Oppgave[]>(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { sakslisteId }, { keepData: false })
      .then((response) => (nySakslisteId === sakslisteId ? fetchSakslisteOppgaverPolling(nySakslisteId, response.map((o) => o.id)
        .join(',')) : Promise.resolve()));
  };

  const openFagsak = (oppgave: Oppgave) => {
    hentFpsakInternBehandlingId(oppgave.behandlingId).then((data: {payload: number }) => {
      goToUrl(getFpsakHref(fpsakUrl, oppgave.saksnummer, data.payload));
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
      runRequest<OppgaveStatus>(RestApiPathsKeys.RESERVER_OPPGAVE, { oppgaveId: oppgave.id })
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
    return runRequest<Oppgave[]>(RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON, { oppgaveId, begrunnelse })
      .then(() => fetchReserverteOppgaver());
  };

  const forlengOppgaveReservasjonFn = (oppgaveId: number): Promise<any> => {
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return runRequest<Oppgave[]>(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, { oppgaveId })
      .then(() => fetchReserverteOppgaver());
  };

  const endreOppgaveReservasjonFn = (oppgaveId: number, reserverTil: string): Promise<any> => {
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return runRequest<Oppgave[]>(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, { oppgaveId, reserverTil })
      .then(() => fetchReserverteOppgaver());
  };

  const flyttReservasjonFn = (oppgaveId: number, brukerident: string, begrunnelse: string): Promise<any> => {
    if (!sakslisteId) {
      return Promise.resolve();
    }
    return runRequest<Oppgave[]>(RestApiPathsKeys.FLYTT_RESERVASJON, { oppgaveId, brukerIdent: brukerident, begrunnelse })
      .then(() => fetchReserverteOppgaver());
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

const mapStateToProps = () => ({
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    setValgtSakslisteId,
    hentFpsakInternBehandlingId: hentFpsakInternBehandlingIdActionCreator,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(BehandlingskoerIndex);
