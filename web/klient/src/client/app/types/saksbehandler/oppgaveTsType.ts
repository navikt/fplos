import Kodeverk from '../kodeverkTsType';
import OppgaveStatus from './oppgaveStatusTsType';

type Oppgave = Readonly<{
  id: number;
  status: OppgaveStatus;
  saksnummer: number;
  personnummer: string;
  navn: string;
  system: string;
  behandlingstype: Kodeverk;
  behandlingStatus: Kodeverk;
  opprettetTidspunkt: string;
  behandlingsfrist: string;
  fagsakYtelseType: Kodeverk;
  erTilSaksbehandling: boolean;
  behandlingId: string;
  href: string;
}>

export default Oppgave;
