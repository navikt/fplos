import { Kodeverk } from 'kodeverk/kodeverkTsType';

// TODO (TOR) default export feilar for yarn:coverage
// eslint-disable-next-line import/prefer-default-export
export type OppgaveForDato = Readonly<{
  fagsakYtelseType: Kodeverk;
  behandlingType: Kodeverk;
  opprettetDato: string;
  antall: number;
}>
