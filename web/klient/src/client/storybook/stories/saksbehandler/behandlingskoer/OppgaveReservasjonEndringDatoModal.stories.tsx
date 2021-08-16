import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import OppgaveReservasjonEndringDatoModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';

import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';
import RestApiMock from '../../../utils/RestApiMock';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaveReservasjonEndringDatoModal',
  component: OppgaveReservasjonEndringDatoModal,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story = () => {
  const data = [
    { key: RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON.name, data: {} },
  ];

  return (
    <RestApiMock data={data}>
      <OppgaveReservasjonEndringDatoModal
        showModal
        closeModal={action('button-click')}
        reserverTilDefault=""
        oppgaveId={1}
        endreReserverasjonState={action('button-click')}
        hentReserverteOppgaver={action('button-click')}
      />
    </RestApiMock>
  );
};

export const ModalForEndringAvReservasjon = Template.bind({});
