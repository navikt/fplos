import React from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { RestApiGlobalDataProvider } from 'data/rest-api-hooks';
import UtvalgskriterierForSakslisteForm from 'avdelingsleder/behandlingskoer/components/sakslisteForm/UtvalgskriterierForSakslisteForm';
import koSortering from 'kodeverk/KoSortering';
import andreKriterierType from 'kodeverk/andreKriterierType';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/UtvalgskriterierForSakslisteForm',
  component: UtvalgskriterierForSakslisteForm,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiGlobalDataProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}}>
        {getStory()}
      </RestApiGlobalDataProvider>
    ),
  ],
};

export const skalViseSakslisteOppsettPanel = (intl) => (
  <UtvalgskriterierForSakslisteForm
    intl={intl}
    valgtSaksliste={{
      sakslisteId: 1,
      navn: 'Saksliste 1',
      sistEndret: '2020-10-10',
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
      sortering: {
        sorteringType: {
          kode: koSortering.BEHANDLINGSFRIST,
          navn: 'Behandlingsfrist',
          felttype: '',
          feltkategori: '',
        },
        fra: 1,
        til: 4,
        erDynamiskPeriode: true,
      },
      behandlingTyper: [{
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      }],
      fagsakYtelseTyper: [{
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'Foreldrepenger',
      }],
      andreKriterier: [{
        andreKriterierType: {
          kode: andreKriterierType.TIL_BESLUTTER,
          navn: 'Til beslutter',
        },
        inkluder: true,
      }, {
        andreKriterierType: {
          kode: andreKriterierType.REGISTRER_PAPIRSOKNAD,
          navn: 'Registrer papirsøknad',
        },
        inkluder: false,
      }],
    }}
    antallOppgaver={2}
    lagreSakslisteNavn={action('button-click')}
    lagreSakslisteBehandlingstype={action('button-click')}
    lagreSakslisteFagsakYtelseType={action('button-click')}
    lagreSakslisteAndreKriterier={action('button-click')}
    valgtAvdelingEnhet=""
    hentAntallOppgaverForSaksliste={action('button-click') as () => Promise<string>}
    lagreSakslisteSortering={action('button-click')}
    lagreSakslisteSorteringErDynamiskPeriode={action('button-click')}
    lagreSakslisteSorteringTidsintervallDato={action('button-click')}
    lagreSakslisteSorteringNumeriskIntervall={action('button-click')}
  />
);
