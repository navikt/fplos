import Kodeverk from '../kodeverkTsType';

type AnnetKriterie = Readonly<{
  andreKriterierType: Kodeverk;
  inkluder: boolean;
}>;

type Saksliste = Readonly<{
  sakslisteId: number;
  navn: string;
  behandlingTyper: Kodeverk[];
  fagsakYtelseTyper: Kodeverk[];
  andreKriterier: AnnetKriterie[];
  sortering?: {
    sorteringType: Kodeverk;
    fra?: number;
    til?: number;
    fomDato?: string;
    tomDato?: string;
    erDynamiskPeriode: boolean;
  };
}>

export default Saksliste;
