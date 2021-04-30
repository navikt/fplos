import React from 'react';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import OppgaveReservasjonEndringDatoModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';

import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaveReservasjonEndringDatoModal',
  component: OppgaveReservasjonEndringDatoModal,
  decorators: [withIntl, withRestApiProvider],
};

export const skalViseModalForEndringAvReservasjon = () => {
  requestApi.mock(RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON.name, {});

  return (
    <OppgaveReservasjonEndringDatoModal
      showModal
      closeModal={action('button-click')}
      reserverTilDefault=""
      oppgaveId={1}
      endreReserverasjonState={action('button-click')}
      hentReserverteOppgaver={action('button-click')}
    />
  );
};
