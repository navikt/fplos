import Kodeverk from 'kodeverk/kodeverkTsType';
import KoSorteringType from './KoSorteringTsType';

type AnnetKriterie = Readonly<{
  andreKriterierType: Kodeverk;
  inkluder: boolean;
}>

type Saksliste = Readonly<{
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

export default Saksliste;
