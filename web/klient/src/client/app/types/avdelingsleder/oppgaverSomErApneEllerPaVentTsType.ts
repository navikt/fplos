import Kodeverk from '../kodeverkTsType';

type OppgaverSomErApneEllerPaVent = Readonly<{
  antall: number;
  behandlingType: Kodeverk;
  behandlingVenteStatus: Kodeverk;
  førsteUttakMåned?: string;
}>

export default OppgaverSomErApneEllerPaVent;
