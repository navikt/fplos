import React from 'react';
import { action } from '@storybook/addon-actions';

import OppgaveReservasjonEndringDatoModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaveReservasjonEndringDatoModal',
  component: OppgaveReservasjonEndringDatoModal,
  decorators: [withIntl],
};

export const skalViseModalForEndringAvReservasjon = () => (
  <OppgaveReservasjonEndringDatoModal
    showModal
    endreOppgaveReservasjon={action('button-click')}
    closeModal={action('button-click')}
    reserverTilDefault=""
  />
);
