import Kodeverk from 'kodeverk/kodeverkTsType';

type OppgaverSomErApneEllerPaVent = Readonly<{
  antall: number;
  behandlingType: Kodeverk;
  behandlingVenteStatus: Kodeverk;
  førsteUttakMåned?: string;
}>

export default OppgaverSomErApneEllerPaVent;
