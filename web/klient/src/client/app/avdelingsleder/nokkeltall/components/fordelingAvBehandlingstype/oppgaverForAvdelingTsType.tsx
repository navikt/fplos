import { Kodeverk } from 'kodeverk/kodeverkTsType';

// TODO (TOR) default export feilar for yarn:coverage
// eslint-disable-next-line import/prefer-default-export
export type OppgaverForAvdeling = Readonly<{
  fagsakYtelseType: Kodeverk;
  behandlingType: Kodeverk;
  tilBehandling: boolean;
  antall: number;
}>;
