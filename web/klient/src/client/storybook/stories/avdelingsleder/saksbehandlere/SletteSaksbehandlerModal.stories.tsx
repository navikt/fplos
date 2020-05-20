import React from 'react';
import { action } from '@storybook/addon-actions';

import SletteSaksbehandlerModal from 'avdelingsleder/saksbehandlere/components/SletteSaksbehandlerModal';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/saksbehandlere/SletteSaksbehandlerModal',
  component: SletteSaksbehandlerModal,
  decorators: [withIntl],
};

export const skalViseModalForSlettingAvSaksbehandler = () => (
  <SletteSaksbehandlerModal
    valgtSaksbehandler={{
      brukerIdent: 'R12122',
      navn: 'Espen Utvikler',
      avdelingsnavn: [],
    }}
    closeSletteModal={action('button-click')}
    fjernSaksbehandler={action('button-click')}
  />
);
