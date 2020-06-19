import React from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiPathsKeys } from 'data/restApiPaths';
import FlyttReservasjonModal from 'saksbehandler/behandlingskoer/components/menu/FlyttReservasjonModal';
import { RestApiProvider } from 'data/rest-api-hooks';

import withIntl from '../../../decorators/withIntl';
import RequestMock from '../../../mocks/RequestMock';

export default {
  title: 'saksbehandler/behandlingskoer/FlyttReservasjonModal',
  component: FlyttReservasjonModal,
  decorators: [withIntl],
};

export const skalViseModalForFlyttingAvReservasjon = (intl) => {
  const saksbehandler = {
    brukerIdent: 'R232323',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  };

  const requestApi = new RequestMock()
    .withKeyAndResult(RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK, saksbehandler)
    .build();

  return (
    <RestApiProvider requestApi={requestApi}>
      <FlyttReservasjonModal
        intl={intl}
        showModal
        oppgaveId={1}
        closeModal={action('button-click')}
        submit={action('button-click')}
      />
    </RestApiProvider>
  );
};
