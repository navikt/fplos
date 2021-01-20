import React from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { RestApiProvider } from 'data/rest-api-hooks';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import fagsakStatus from 'kodeverk/fagsakStatus';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import FagsakSearch from 'saksbehandler/fagsakSearch/components/FagsakSearch';

import RequestMock from '../../../mocks/RequestMock';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiGlobalStatePathsKeys.NAV_ANSATT]: {
    kanSaksbehandle: true,
  },
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'saksbehandler/fagsakSearch/FagsakSearch',
  component: FagsakSearch,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
        {getStory()}
      </RestApiProvider>
    ),
  ],
};

export const skalViseAtIngentingBleFunnet = () => (
  <FagsakSearch
    fagsaker={[]}
    fagsakOppgaver={[]}
    searchFagsakCallback={action('button-click')}
    searchResultReceived
    selectFagsakCallback={action('button-click')}
    selectOppgaveCallback={action('button-click')}
    searchStarted={false}
    resetSearch={action('button-click')}
  />
);

export const skalVisePersonkortOgTabellNårEnFinnerEnFagsakMedToOppgaver = () => (
  <FagsakSearch
    fagsaker={[{
      saksnummer: 12213234,
      system: 'SAK',
      sakstype: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'Foreldrepenger',
      },
      status: {
        kode: fagsakStatus.UNDER_BEHANDLING,
        navn: 'Under behandling',
      },
      person: {
        navn: 'Espen Utvikler',
        alder: 41,
        personnummer: '232434234',
        erKvinne: false,
      },
      barnFodt: '2019-12-12',
      opprettet: '2020-01-01',
    }]}
    fagsakOppgaver={[{
      id: 1,
      status: {
        erReservert: false,
      },
      saksnummer: 12213234,
      personnummer: '1010',
      navn: 'Espen Utvikler',
      system: 'SAK',
      behandlingstype: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      behandlingStatus: {
        kode: behandlingStatus.BEHANDLING_UTREDES,
        navn: 'Behandling utredes',
      },
      opprettetTidspunkt: '2020-01-01',
      behandlingsfrist: '2020-01-01',
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'Foreldrepenger',
      },
      erTilSaksbehandling: true,
      behandlingId: '12344',
      href: '',
    }, {
      id: 2,
      status: {
        erReservert: false,
      },
      saksnummer: 12213234,
      personnummer: '1010',
      navn: 'Espen Utvikler',
      system: 'SAK',
      behandlingstype: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      behandlingStatus: {
        kode: behandlingStatus.BEHANDLING_UTREDES,
        navn: 'Behandling utredes',
      },
      opprettetTidspunkt: '2020-01-01',
      behandlingsfrist: '2020-01-01',
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'Foreldrepenger',
      },
      erTilSaksbehandling: true,
      behandlingId: '12344',
      href: '',
    }]}
    searchFagsakCallback={action('button-click')}
    searchResultReceived
    selectFagsakCallback={action('button-click')}
    selectOppgaveCallback={action('button-click')}
    searchStarted={false}
    resetSearch={action('button-click')}
  />
);
