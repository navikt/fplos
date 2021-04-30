import React from 'react';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import OppgaverTabell from 'saksbehandler/behandlingskoer/components/OppgaverTabell';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaverTabell',
  component: OppgaverTabell,
  decorators: [withIntl, withRestApiProvider],
};

export const skalViseTomOppgaveTabell = () => {
  requestApi.mock(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON.name, {});
  requestApi.mock(RestApiPathsKeys.RESERVERTE_OPPGAVER.name, []);
  requestApi.mock(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING.name, []);

  return (
    <OppgaverTabell
      reserverOppgave={action('button-click')}
      valgtSakslisteId={1}
      doPolling={false}
    />
  );
};

export const skalViseTabellMedBådeLedigOgReservertOppgave = () => {
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

  requestApi.mock(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON.name, {});
  requestApi.mock(RestApiPathsKeys.RESERVERTE_OPPGAVER.name, reserverteOppgaver);
  requestApi.mock(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING.name, oppgaverTilBehandling);

  return (
    <div style={{ width: '80%' }}>
      <OppgaverTabell
        reserverOppgave={action('button-click')}
        valgtSakslisteId={1}
        doPolling={false}
      />
    </div>
  );
};
