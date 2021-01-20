import React from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiProvider } from 'data/rest-api-hooks';
import { RestApiPathsKeys } from 'data/fplosRestApi';
import { OppgaverTabell } from 'saksbehandler/behandlingskoer/components/OppgaverTabell';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import RequestMock from '../../../mocks/RequestMock';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaverTabell',
  component: OppgaverTabell,
  decorators: [withIntl],
};

export const skalViseTomOppgaveTabell = (intl) => {
  const requestApiMock = new RequestMock()
    .withKeyAndResult(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, {})
    .withKeyAndResult(RestApiPathsKeys.RESERVERTE_OPPGAVER, [])
    .withKeyAndResult(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, [])
    .build();

  return (
    <RestApiProvider requestApi={requestApiMock}>
      <OppgaverTabell
        intl={intl}
        reserverOppgave={action('button-click')}
        valgtSakslisteId={1}
        doPolling={false}
      />
    </RestApiProvider>
  );
};

export const skalViseTabellMedBådeLedigOgReservertOppgave = (intl) => {
  const oppgaverTilBehandling = [{
    id: 1,
    status: {
      erReservert: false,
      flyttetReservasjon: {
        tidspunkt: '2019-02-02',
        uid: '23423',
        navn: 'Espen Utvikler',
        begrunnelse: 'Flyttet',
      },
    },
    saksnummer: 1234,
    personnummer: '1212',
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
    opprettetTidspunkt: '2019-01-01',
    behandlingsfrist: '2019-01-01',
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldrepenger',
    },
    erTilSaksbehandling: true,
    behandlingId: '1',
    href: '',
  }];
  const reserverteOppgaver = [{
    id: 2,
    status: {
      erReservert: true,
    },
    saksnummer: 1234,
    personnummer: '1212',
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
    opprettetTidspunkt: '2019-01-01',
    behandlingsfrist: '2019-01-01',
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldrepenger',
    },
    erTilSaksbehandling: true,
    behandlingId: '2',
    href: '',
  }];
  const requestApiMock = new RequestMock()
    .withKeyAndResult(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, {})
    .withKeyAndResult(RestApiPathsKeys.RESERVERTE_OPPGAVER, reserverteOppgaver)
    .withKeyAndResult(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, oppgaverTilBehandling)
    .build();

  return (
    <RestApiProvider requestApi={requestApiMock}>
      <div style={{ width: '80%' }}>
        <OppgaverTabell
          intl={intl}
          reserverOppgave={action('button-click')}
          valgtSakslisteId={1}
          doPolling={false}
        />
      </div>
    </RestApiProvider>
  );
};
