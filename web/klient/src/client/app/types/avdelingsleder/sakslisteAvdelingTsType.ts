import Kodeverk from '../kodeverkTsType';
import KoSorteringType from './koSorteringTsType';

type AnnetKriterie = Readonly<{
  andreKriterierType: Kodeverk;
  inkluder: boolean;
}>

type SakslisteAvdeling = Readonly<{
  sakslisteId: number;
  navn?: string;
  behandlingTyper?: Kodeverk[];
  fagsakYtelseTyper?: Kodeverk[];
  sistEndret: string;
  sortering?: {
    sorteringType: KoSorteringType;
    fra?: number;
    til?: number;
    fomDato?: string;
    tomDato?: string;
    erDynamiskPeriode: boolean;
  };
  andreKriterier?: AnnetKriterie[];
  saksbehandlerIdenter: string[];
  antallBehandlinger: number;
}>;

export default SakslisteAvdeling;