import React from 'react';
import { action } from '@storybook/addon-actions';

import OppgaveReservasjonForlengetModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonForlengetModal';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from 'storybookUtils/decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaveReservasjonForlengetModal',
  component: OppgaveReservasjonForlengetModal,
  decorators: [withIntl],
};

export const Default = () => (
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
        reservertTilTidspunkt: '2017-08-02T00:54:25.455',
      },
      saksnummer: 1234,
      personnummer: '1212',
      navn: 'Espen Utvikler',
      system: 'SAK',
      behandlingstype: behandlingType.FORSTEGANGSSOKNAD,
      behandlingStatus: behandlingStatus.BEHANDLING_UTREDES,
      opprettetTidspunkt: '2019-01-01',
      behandlingsfrist: '2019-01-01',
      fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
      erTilSaksbehandling: true,
      behandlingId: '1',
      href: '',
    }}
    closeModal={action('button-click')}
  />
);
