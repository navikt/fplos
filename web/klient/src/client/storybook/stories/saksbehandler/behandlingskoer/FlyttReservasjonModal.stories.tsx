import React from 'react';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import FlyttReservasjonModal from 'saksbehandler/behandlingskoer/components/menu/FlyttReservasjonModal';

import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'saksbehandler/behandlingskoer/FlyttReservasjonModal',
  component: FlyttReservasjonModal,
  decorators: [withIntl, withRestApiProvider],
};

export const skalViseModalForFlyttingAvReservasjon = () => {
  const saksbehandler = {
    brukerIdent: 'R232323',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  };

  requestApi.mock(RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK, saksbehandler);

  return (
    <FlyttReservasjonModal
      showModal
      oppgaveId={1}
      closeModal={action('button-click')}
      toggleMenu={action('button-click')}
      hentReserverteOppgaver={action('button-click')}
    />
  );
};
