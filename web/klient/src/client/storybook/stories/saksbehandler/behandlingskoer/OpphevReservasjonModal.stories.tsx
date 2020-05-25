import React from 'react';
import { action } from '@storybook/addon-actions';

import OpphevReservasjonModal from 'saksbehandler/behandlingskoer/components/menu/OpphevReservasjonModal';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';


import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OpphevReservasjonModal',
  component: OpphevReservasjonModal,
  decorators: [withIntl],
};

export const skalViseModalForEndringAvReservasjon = () => (
  <OpphevReservasjonModal
    showModal
    cancel={action('button-click')}
    submit={action('button-click')}
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
  />
);
