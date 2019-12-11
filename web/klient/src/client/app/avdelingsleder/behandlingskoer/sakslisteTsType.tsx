import { Kodeverk } from 'kodeverk/kodeverkTsType';
import {KoSorteringType} from "kodeverk/KoSorteringTsType";

type AnnetKriterie = Readonly<{
  andreKriterierType: Kodeverk;
  inkluder: boolean;
}>

// TODO (TOR) default export feilar for yarn:coverage
// eslint-disable-next-line import/prefer-default-export
export type Saksliste = Readonly<{
  sakslisteId: number;
  navn?: string;
  behandlingTyper?: Kodeverk[];
  fagsakYtelseTyper?: Kodeverk[];
  sistEndret: string;
  sortering?: {
    sorteringType: KoSorteringType;
    fomDager?: number;
    tomDager?: number;
    fomDato?: string;
    tomDato?: string;
    belop?: number;
    erDynamiskPeriode: boolean;
  };
  andreKriterier?: AnnetKriterie[];
  saksbehandlerIdenter: string[];
}>;
