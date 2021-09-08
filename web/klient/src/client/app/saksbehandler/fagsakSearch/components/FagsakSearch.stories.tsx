import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import fagsakStatus from 'kodeverk/fagsakStatus';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import FagsakSearch from 'saksbehandler/fagsakSearch/components/FagsakSearch';

import Fagsak from 'types/saksbehandler/fagsakTsType';
import Oppgave from 'types/saksbehandler/oppgaveTsType';
import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

export default {
  title: 'saksbehandler/fagsakSearch/FagsakSearch',
  component: FagsakSearch,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

const Template: Story<{ fagsaker: Fagsak[], fagsakOppgaver: Oppgave[] }> = ({
  fagsaker,
  fagsakOppgaver,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.NAV_ANSATT.name, data: { kanSaksbehandle: true } },
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
  ];

  return (
    <RestApiMock data={data}>
      <FagsakSearch
        fagsaker={fagsaker}
        fagsakOppgaver={fagsakOppgaver}
        searchFagsakCallback={action('button-click')}
        searchResultReceived
        selectFagsakCallback={action('button-click')}
        selectOppgaveCallback={action('button-click')}
        searchStarted={false}
        resetSearch={action('button-click')}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  fagsaker: [{
    saksnummer: 12213234,
    saksnummerString: '12213234',
    system: 'SAK',
    fagsakYtelseType: {
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
    barnFødt: '2019-12-12',
    opprettet: '2020-01-01',
  }],
  fagsakOppgaver: [{
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
  }],
};

export const IngentingBleFunnet = Template.bind({});
IngentingBleFunnet.args = {
  fagsaker: [],
  fagsakOppgaver: [],
};
