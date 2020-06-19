import Kodeverk from 'kodeverk/kodeverkTsType';

type Reservasjon = Readonly<{
  reservertAvUid: string;
  reservertAvNavn: string;
  reservertTilTidspunkt: string;
  oppgaveId: number;
  oppgaveSaksNr: number;
  behandlingType: Kodeverk;
}>

export default Reservasjon;
