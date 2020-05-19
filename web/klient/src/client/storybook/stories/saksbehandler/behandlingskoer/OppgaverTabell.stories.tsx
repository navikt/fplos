import React from 'react';
import { action } from '@storybook/addon-actions';

import { OppgaverTabell } from 'saksbehandler/behandlingskoer/components/OppgaverTabell';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaverTabell',
  component: OppgaverTabell,
  decorators: [withIntl],
};

export const skalViseTomOppgaveTabell = (intl) => (
  <OppgaverTabell
    intl={intl}
    antall={0}
    oppgaverTilBehandling={[]}
    reserverteOppgaver={[]}
    finnSaksbehandler={() => { action('button-click'); return Promise.resolve('test'); }}
    resetSaksbehandler={() => { action('button-click'); return Promise.resolve('test'); }}
    reserverOppgave={action('button-click')}
    opphevOppgaveReservasjon={() => { action('button-click'); return Promise.resolve('test'); }}
    forlengOppgaveReservasjon={() => { action('button-click'); return Promise.resolve('test'); }}
    endreOppgaveReservasjon={() => { action('button-click'); return Promise.resolve('test'); }}
    flyttReservasjon={() => { action('button-click'); return Promise.resolve('test'); }}
  />
);

export const skalViseTabellMedBådeLedigOgReservertOppgave = (intl) => (
  <div style={{ width: '80%' }}>
    <OppgaverTabell
      intl={intl}
      antall={2}
      oppgaverTilBehandling={[{
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
      }]}
      reserverteOppgaver={[{
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
      }]}
      finnSaksbehandler={() => { action('button-click'); return Promise.resolve('test'); }}
      resetSaksbehandler={() => { action('button-click'); return Promise.resolve('test'); }}
      reserverOppgave={action('button-click')}
      opphevOppgaveReservasjon={() => { action('button-click'); return Promise.resolve('test'); }}
      forlengOppgaveReservasjon={() => { action('button-click'); return Promise.resolve('test'); }}
      endreOppgaveReservasjon={() => { action('button-click'); return Promise.resolve('test'); }}
      flyttReservasjon={() => { action('button-click'); return Promise.resolve('test'); }}
    />
  </div>
);
