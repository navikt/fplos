import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import OppgaveReservasjonEndringDatoModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';

import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import withIntl from 'storybookUtils/decorators/withIntl';
import RestApiMock from 'storybookUtils//RestApiMock';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaveReservasjonEndringDatoModal',
  component: OppgaveReservasjonEndringDatoModal,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ endreReserverasjonState: () => void }> = ({
  endreReserverasjonState,
}) => {
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
        endreReserverasjonState={endreReserverasjonState}
        hentReserverteOppgaver={action('button-click')}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  endreReserverasjonState: action('button-click'),
};
