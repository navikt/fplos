import Kodeverk from '../kodeverkTsType';
import Person from './personTsType';

type Fagsak = Readonly<{
  saksnummer: number;
  saksnummerString: string;
  system: string;
  fagsakYtelseType: Kodeverk;
  status: Kodeverk;
  person: Person;
  barnFodt?: string;
  behandlingStatus?: Kodeverk;
  opprettet: string;
  endret?: string;
  href?: string;
}>;

export default Fagsak;
