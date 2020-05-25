import React from 'react';
import { action } from '@storybook/addon-actions';

import OppgaveReservasjonForlengetModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonForlengetModal';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaveReservasjonForlengetModal',
  component: OppgaveReservasjonForlengetModal,
  decorators: [withIntl],
};

export const skalViseModalForEndringAvReservasjon = () => (
  <OppgaveReservasjonForlengetModal
    showModal
    oppgave={{
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
    }}
    closeModal={action('button-click')}
  />
);
