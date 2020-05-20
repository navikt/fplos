import React from 'react';
import { action } from '@storybook/addon-actions';

import SletteSakslisteModal from 'avdelingsleder/behandlingskoer/components/SletteSakslisteModal';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/behandlingskoer/SletteSakslisteModal',
  component: SletteSakslisteModal,
  decorators: [withIntl],
};

export const skalViseModalForSlettingAvSaksliste = () => (
  <SletteSakslisteModal
    valgtSaksliste={{
      sakslisteId: 1,
      navn: 'Saksliste 1',
      sistEndret: '2020-01-01',
      saksbehandlerIdenter: [],
      antallBehandlinger: 2,
    }}
    cancel={action('button-click')}
    submit={action('button-click')}
  />
);
