import React, { FunctionComponent } from 'react';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';

import NokkeltallPanel from './components/NokkeltallPanel';
import OppgaverForAvdeling from './components/fordelingAvBehandlingstype/oppgaverForAvdelingTsType';
import OppgaveForDato from './components/tilBehandling/oppgaverForDatoTsType';
import OppgaverManueltPaVent from './components/manueltSattPaVent/oppgaverManueltPaVentTsType';
import OppgaverForForsteStonadsdag from './components/antallBehandlingerPerForsteStonadsdag/oppgaverForForsteStonadsdagTsType';
import OppgaverSomErApneEllerPaVent from './components/apneOgPaVentBehandlinger/oppgaverSomErApneEllerPaVentTsType';

const EMPTY_ARRAY_AVDELING: OppgaverForAvdeling[] = [];
const EMPTY_ARRAY_DATO: OppgaveForDato[] = [];
const EMPTY_ARRAY_PA_VENT: OppgaverManueltPaVent[] = [];
const EMPTY_ARRAY_STONADSDAG: OppgaverForForsteStonadsdag[] = [];

interface OwnProps {
  valgtAvdelingEnhet: string;
}

/**
 * NokkeltallIndex
 */
const NokkeltallIndex: FunctionComponent<OwnProps> = ({
  valgtAvdelingEnhet,
}) => {
  const {
    data: oppgaverForAvdeling = EMPTY_ARRAY_AVDELING,
  } = restApiHooks.useRestApi<OppgaverForAvdeling[]>(RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverPerDato = EMPTY_ARRAY_DATO,
  } = restApiHooks.useRestApi<OppgaveForDato[]>(RestApiPathsKeys.HENT_OPPGAVER_PER_DATO, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverManueltPaVent = EMPTY_ARRAY_PA_VENT,
  } = restApiHooks.useRestApi<OppgaverManueltPaVent[]>(RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverPerForsteStonadsdag = EMPTY_ARRAY_STONADSDAG,
  } = restApiHooks.useRestApi<OppgaverForForsteStonadsdag[]>(RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverApneEllerPaVent = EMPTY_ARRAY,
  } = restApiHooks.useRestApi<OppgaverSomErApneEllerPaVent[]>(RestApiPathsKeys.HENT_OPPGAVER_APNE_ELLER_PA_VENT, { avdelingEnhet: valgtAvdelingEnhet });

  return (
    <NokkeltallPanel
      oppgaverForAvdeling={oppgaverForAvdeling}
      oppgaverPerDato={oppgaverPerDato}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      oppgaverPerForsteStonadsdag={oppgaverPerForsteStonadsdag}
      oppgaverApneEllerPaVent={oppgaverApneEllerPaVent}
    />
  );
};

export default NokkeltallIndex;
