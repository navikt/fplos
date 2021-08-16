import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import FlyttReservasjonModal from 'saksbehandler/behandlingskoer/components/menu/FlyttReservasjonModal';
import SaksbehandlerAvdeling from 'types/avdelingsleder/saksbehandlerAvdelingTsType';

import RestApiMock from '../../../utils/RestApiMock';
import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'saksbehandler/behandlingskoer/FlyttReservasjonModal',
  component: FlyttReservasjonModal,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ saksbehandler: SaksbehandlerAvdeling }> = ({
  saksbehandler,
}) => {
  const data = [
    { key: RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK.name, data: saksbehandler },
  ];

  return (
    <RestApiMock data={data}>
      <FlyttReservasjonModal
        showModal
        oppgaveId={1}
        closeModal={action('button-click')}
        toggleMenu={action('button-click')}
        hentReserverteOppgaver={action('button-click')}
      />
    </RestApiMock>
  );
};

export const ModalForFlyttingAvReservasjon = Template.bind({});
ModalForFlyttingAvReservasjon.args = {
  saksbehandler: {
    brukerIdent: 'R232323',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  },
};
