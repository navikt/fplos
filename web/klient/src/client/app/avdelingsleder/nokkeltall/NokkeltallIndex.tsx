import React, { FunctionComponent } from 'react';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';

import OppgaverForAvdeling from 'types/avdelingsleder/oppgaverForAvdelingTsType';
import OppgaveForDato from 'types/avdelingsleder/oppgaverForDatoTsType';
import OppgaverManueltPaVent from 'types/avdelingsleder/oppgaverManueltPaVentTsType';
import OppgaverForForsteStonadsdag from 'types/avdelingsleder/oppgaverForForsteStonadsdagTsType';
import OppgaverSomErApneEllerPaVent from 'types/avdelingsleder/oppgaverSomErApneEllerPaVentTsType';
import NokkeltallPanel from './components/NokkeltallPanel';

const EMPTY_ARRAY_AVDELING: OppgaverForAvdeling[] = [];
const EMPTY_ARRAY_DATO: OppgaveForDato[] = [];
const EMPTY_ARRAY_PA_VENT: OppgaverManueltPaVent[] = [];
const EMPTY_ARRAY_STONADSDAG: OppgaverForForsteStonadsdag[] = [];
const EMPTY_ARRAY_APNE_ELLER_PA_VENT: OppgaverSomErApneEllerPaVent[] = [];

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
  } = restApiHooks.useRestApi(RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverPerDato = EMPTY_ARRAY_DATO,
  } = restApiHooks.useRestApi(RestApiPathsKeys.HENT_OPPGAVER_PER_DATO, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverManueltPaVent = EMPTY_ARRAY_PA_VENT,
  } = restApiHooks.useRestApi(RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverPerForsteStonadsdag = EMPTY_ARRAY_STONADSDAG,
  } = restApiHooks.useRestApi(RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG, { avdelingEnhet: valgtAvdelingEnhet });
  const {
    data: oppgaverApneEllerPaVent = EMPTY_ARRAY_APNE_ELLER_PA_VENT,
  } = restApiHooks.useRestApi(RestApiPathsKeys.HENT_OPPGAVER_APNE_ELLER_PA_VENT, { avdelingEnhet: valgtAvdelingEnhet });

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
