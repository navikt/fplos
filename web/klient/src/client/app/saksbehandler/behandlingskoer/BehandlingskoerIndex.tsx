import React, {
  FunctionComponent, useState, useCallback,
} from 'react';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';

import Saksliste from 'types/saksbehandler/sakslisteTsType';
import { åpneFagsak } from 'app/paths';
import OppgaveStatus from 'types/saksbehandler/oppgaveStatusTsType';
import Oppgave from 'types/saksbehandler/oppgaveTsType';
import OppgaveErReservertAvAnnenModal from 'saksbehandler/components/OppgaveErReservertAvAnnenModal';
import SakslistePanel from './components/SakslistePanel';

const EMPTY_ARRAY: Saksliste[] = [];

const openSak = (
  oppgave: Oppgave,
  fpsakUrl: string,
) => {
  åpneFagsak(fpsakUrl, oppgave.system, oppgave.saksnummer, oppgave.behandlingId);
};

interface OwnProps {
  fpsakUrl: string;
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
}) => {
  const [reservertAvAnnenSaksbehandler, setReservertAvAnnenSaksbehandler] = useState<boolean>(false);
  const [reservertOppgave, setReservertOppgave] = useState<Oppgave>();
  const [reservertOppgaveStatus, setReservertOppgaveStatus] = useState<OppgaveStatus>();

  const { data: sakslister = EMPTY_ARRAY } = restApiHooks.useRestApi(RestApiPathsKeys.SAKSLISTE);

  const { startRequest: reserverOppgave } = restApiHooks.useRestApiRunner(RestApiPathsKeys.RESERVER_OPPGAVE);

  const reserverOppgaveOgApne = useCallback((oppgave: Oppgave) => {
    if (oppgave.status.erReservert) {
      openSak(oppgave, fpsakUrl);
    } else {
      reserverOppgave({ oppgaveId: oppgave.id })
        .then((nyOppgaveStatus) => {
          if (nyOppgaveStatus && nyOppgaveStatus.erReservert && nyOppgaveStatus.erReservertAvInnloggetBruker) {
            openSak(oppgave, fpsakUrl);
          } else if (nyOppgaveStatus && nyOppgaveStatus.erReservert && !nyOppgaveStatus.erReservertAvInnloggetBruker) {
            setReservertAvAnnenSaksbehandler(true);
            setReservertOppgave(oppgave);
            setReservertOppgaveStatus(nyOppgaveStatus);
          }
        });
    }
  }, [fpsakUrl]);

  const lukkErReservertModalOgOpneOppgave = useCallback((oppgave: Oppgave) => {
    setReservertAvAnnenSaksbehandler(false);
    setReservertOppgave(undefined);
    setReservertOppgaveStatus(undefined);

    openSak(oppgave, fpsakUrl);
  }, [fpsakUrl]);

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
