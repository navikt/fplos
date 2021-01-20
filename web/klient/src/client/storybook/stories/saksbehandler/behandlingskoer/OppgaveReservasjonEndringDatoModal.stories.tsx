import React from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiProvider } from 'data/rest-api-hooks';
import { RestApiPathsKeys } from 'data/fplosRestApi';
import OppgaveReservasjonEndringDatoModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';

import RequestMock from '../../../mocks/RequestMock';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaveReservasjonEndringDatoModal',
  component: OppgaveReservasjonEndringDatoModal,
  decorators: [withIntl],
};

export const skalViseModalForEndringAvReservasjon = () => {
  const requestApi = new RequestMock()
    .withKeyAndResult(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON, {})
    .build();

  return (
    <RestApiProvider requestApi={requestApi}>
      <OppgaveReservasjonEndringDatoModal
        showModal
        closeModal={action('button-click')}
        reserverTilDefault=""
        oppgaveId={1}
        endreReserverasjonState={action('button-click')}
        hentReserverteOppgaver={action('button-click')}
      />
    </RestApiProvider>
  );
};
