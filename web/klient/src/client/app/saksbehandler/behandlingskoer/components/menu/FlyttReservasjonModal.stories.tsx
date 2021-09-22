import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import FlyttReservasjonModal from 'saksbehandler/behandlingskoer/components/menu/FlyttReservasjonModal';
import SaksbehandlerAvdeling from 'types/avdelingsleder/saksbehandlerAvdelingTsType';

import RestApiMock from 'storybookUtils//RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';

export default {
  title: 'saksbehandler/behandlingskoer/FlyttReservasjonModal',
  component: FlyttReservasjonModal,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{
  saksbehandler: SaksbehandlerAvdeling,
  hentReserverteOppgaver: (params: any, keepData: boolean) => void
}> = ({
  saksbehandler,
  hentReserverteOppgaver,
}) => {
  const data = [
    { key: RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK.name, data: saksbehandler },
    { key: RestApiPathsKeys.FLYTT_RESERVASJON.name, data: undefined },
  ];

  return (
    <RestApiMock data={data}>
      <FlyttReservasjonModal
        showModal
        oppgaveId={1}
        closeModal={action('button-click')}
        toggleMenu={action('button-click')}
        hentReserverteOppgaver={hentReserverteOppgaver}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  hentReserverteOppgaver: action('button-click'),
  saksbehandler: undefined,
};

export const MedTreffPåSøk = Template.bind({});
MedTreffPåSøk.args = {
  hentReserverteOppgaver: action('button-click'),
  saksbehandler: {
    brukerIdent: 'R232323',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  },
};