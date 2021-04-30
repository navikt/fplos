import React from 'react';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import UtvalgskriterierForSakslisteForm from 'avdelingsleder/behandlingskoer/components/sakslisteForm/UtvalgskriterierForSakslisteForm';
import koSortering from 'kodeverk/KoSortering';
import andreKriterierType from 'kodeverk/andreKriterierType';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'avdelingsleder/behandlingskoer/UtvalgskriterierForSakslisteForm',
  component: UtvalgskriterierForSakslisteForm,
  decorators: [withIntl, withRestApiProvider],
};

export const skalViseSakslisteOppsettPanel = () => {
  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);
  requestApi.mock(RestApiPathsKeys.OPPGAVE_ANTALL.name);
  return (
    <UtvalgskriterierForSakslisteForm
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
      valgtAvdelingEnhet=""
      hentAvdelingensSakslister={action('button-click')}
      hentOppgaverForAvdelingAntall={action('button-click')}
    />
  );
};
