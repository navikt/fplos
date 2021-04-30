import React, {
  FunctionComponent, useState, useCallback,
} from 'react';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';

import Saksliste from 'types/saksbehandler/sakslisteTsType';
import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import OppgaveStatus from 'types/saksbehandler/oppgaveStatusTsType';
import Oppgave from 'types/saksbehandler/oppgaveTsType';
import OppgaveErReservertAvAnnenModal from 'saksbehandler/components/OppgaveErReservertAvAnnenModal';
import SakslistePanel from './components/SakslistePanel';

const EMPTY_ARRAY: Saksliste[] = [];

const openFagsak = (
  oppgave: Oppgave,
  hentFpsakInternBehandlingId: (param: { uuid: string}) => Promise<number | undefined>,
  fpsakUrl: string,
) => {
  hentFpsakInternBehandlingId({ uuid: oppgave.behandlingId }).then((behandlingId) => {
    window.location.assign(getFpsakHref(fpsakUrl, oppgave.saksnummer, behandlingId));
  });
};

const openTilbakesak = (oppgave: Oppgave, fptilbakeUrl: string) => {
  window.location.assign(getFptilbakeHref(fptilbakeUrl, oppgave.href));
};

const openSak = (
  oppgave: Oppgave,
  hentFpsakInternBehandlingId: (param: { uuid: string}) => Promise<number | undefined>,
  fpsakUrl: string,
  fptilbakeUrl: string,
) => {
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

  const { data: sakslister = EMPTY_ARRAY } = restApiHooks.useRestApi(RestApiPathsKeys.SAKSLISTE);

  const { startRequest: reserverOppgave } = restApiHooks.useRestApiRunner(RestApiPathsKeys.RESERVER_OPPGAVE);
  const { startRequest: hentFpsakInternBehandlingId } = restApiHooks.useRestApiRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID);

  const reserverOppgaveOgApne = useCallback((oppgave: Oppgave) => {
    if (oppgave.status.erReservert) {
      openSak(oppgave, hentFpsakInternBehandlingId, fpsakUrl, fptilbakeUrl);
    } else {
      reserverOppgave({ oppgaveId: oppgave.id })
        .then((nyOppgaveStatus) => {
          if (nyOppgaveStatus && nyOppgaveStatus.erReservert && nyOppgaveStatus.erReservertAvInnloggetBruker) {
            openSak(oppgave, hentFpsakInternBehandlingId, fpsakUrl, fptilbakeUrl);
          } else if (nyOppgaveStatus && nyOppgaveStatus.erReservert && !nyOppgaveStatus.erReservertAvInnloggetBruker) {
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
        valgtSakslisteId={valgtSakslisteId}
        setValgtSakslisteId={setValgtSakslisteId}
        reserverOppgave={reserverOppgaveOgApne}
        sakslister={sakslister}
      />
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
