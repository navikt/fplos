import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import SletteSaksbehandlerModal from 'avdelingsleder/saksbehandlere/components/SletteSaksbehandlerModal';
import Saksbehandler from 'types/avdelingsleder/saksbehandlerAvdelingTsType';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/saksbehandlere/SletteSaksbehandlerModal',
  component: SletteSaksbehandlerModal,
  decorators: [withIntl],
};

const Template: Story<{
  fjernSaksbehandler: (saksbehandler: Saksbehandler) => void;
}> = ({
  fjernSaksbehandler,
}) => (
  <SletteSaksbehandlerModal
    valgtSaksbehandler={{
      brukerIdent: 'R12122',
      navn: 'Espen Utvikler',
      avdelingsnavn: [],
    }}
    closeSletteModal={action('button-click')}
    fjernSaksbehandler={fjernSaksbehandler}
  />
);

export const Default = Template.bind({});
Default.args = {
  fjernSaksbehandler: action('button-click'),
};
