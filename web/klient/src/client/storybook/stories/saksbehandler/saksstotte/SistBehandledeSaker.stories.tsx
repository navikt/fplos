import React from 'react';

import { requestApi, RestApiPathsKeys, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import SistBehandledeSaker from 'saksbehandler/saksstotte/components/SistBehandledeSaker';

import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/saksstotte/SistBehandledeSaker',
  component: SistBehandledeSaker,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

export const skalViseIngenBehandlinger = () => {
  requestApi.mock(RestApiGlobalStatePathsKeys.FPSAK_URL.name, { value: 'fpsak-url' });
  requestApi.mock(RestApiPathsKeys.BEHANDLEDE_OPPGAVER.name);
  return (
    <SistBehandledeSaker />
  );
};

export const skalViseSistBehandlendeSaker = () => {
  const behandledeOppgaver = [{
    behandlingId: 1,
    personnummer: '334342323',
    navn: 'Espen Utvikler',
  }];

  requestApi.mock(RestApiGlobalStatePathsKeys.FPSAK_URL.name, { value: 'fpsak-url' });
  requestApi.mock(RestApiPathsKeys.BEHANDLEDE_OPPGAVER.name, behandledeOppgaver);

  return (
    <SistBehandledeSaker />
  );
};
