import Kodeverk from '../kodeverkTsType';

type OppgaveForDato = Readonly<{
  fagsakYtelseType: Kodeverk;
  behandlingType: Kodeverk;
  opprettetDato: string;
  antall: number;
}>

export default OppgaveForDato;
