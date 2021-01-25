import React, { FunctionComponent } from 'react';

import { RestApiPathsKeys } from 'data/restApiPaths';
import { useRestApi } from 'data/rest-api-hooks';

import NokkeltallPanel from './components/NokkeltallPanel';
import OppgaverForAvdeling from './components/fordelingAvBehandlingstype/oppgaverForAvdelingTsType';
import OppgaveForDato from './components/tilBehandling/oppgaverForDatoTsType';
import OppgaverManueltPaVent from './components/manueltSattPaVent/oppgaverManueltPaVentTsType';
import OppgaverForForsteStonadsdag from './components/antallBehandlingerPerForsteStonadsdag/oppgaverForForsteStonadsdagTsType';

const EMPTY_ARRAY = [];

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
    data: oppgaverForAvdeling = EMPTY_ARRAY,
  } = useRestApi<OppgaverForAvdeling[]>(RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverPerDato = EMPTY_ARRAY,
  } = useRestApi<OppgaveForDato[]>(RestApiPathsKeys.HENT_OPPGAVER_PER_DATO, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverManueltPaVent = EMPTY_ARRAY,
  } = useRestApi<OppgaverManueltPaVent[]>(RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverPerForsteStonadsdag = EMPTY_ARRAY,
  } = useRestApi<OppgaverForForsteStonadsdag[]>(RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG, { avdelingEnhet: valgtAvdelingEnhet });

  return (
    <NokkeltallPanel
      oppgaverForAvdeling={oppgaverForAvdeling}
      oppgaverPerDato={oppgaverPerDato}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      oppgaverPerForsteStonadsdag={oppgaverPerForsteStonadsdag}
    />
  );
};

export default NokkeltallIndex;
