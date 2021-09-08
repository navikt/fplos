import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import SletteSakslisteModal from 'avdelingsleder/behandlingskoer/components/SletteSakslisteModal';
import Saksliste from 'types/avdelingsleder/sakslisteAvdelingTsType';

import withIntl from 'storybookUtils/decorators/withIntl';

export default {
  title: 'avdelingsleder/behandlingskoer/SletteSakslisteModal',
  component: SletteSakslisteModal,
  decorators: [withIntl],
};

const Template: Story<{
  cancel: () => void;
  submit: (saksliste: Saksliste) => void;
}> = ({
  cancel,
  submit,
}) => (
  <SletteSakslisteModal
    valgtSaksliste={{
      sakslisteId: 1,
      navn: 'Saksliste 1',
      sistEndret: '2020-01-01',
      saksbehandlerIdenter: [],
      antallBehandlinger: 2,
    }}
    cancel={cancel}
    submit={submit}
  />
);

export const Default = Template.bind({});
Default.args = {
  cancel: action('button-click'),
  submit: action('button-click'),
};
