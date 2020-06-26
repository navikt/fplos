import Kodeverk from 'kodeverk/kodeverkTsType';

type OppgaverManueltPaVent = Readonly<{
  fagsakYtelseType: Kodeverk;
  behandlingFrist: string;
  antall: number;
}>

export default OppgaverManueltPaVent;
