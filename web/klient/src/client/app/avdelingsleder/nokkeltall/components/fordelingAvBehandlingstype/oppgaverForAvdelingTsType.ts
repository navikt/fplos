import Kodeverk from 'kodeverk/kodeverkTsType';

type OppgaverForAvdeling = Readonly<{
  fagsakYtelseType: Kodeverk;
  behandlingType: Kodeverk;
  tilBehandling: boolean;
  antall: number;
}>;

export default OppgaverForAvdeling;
