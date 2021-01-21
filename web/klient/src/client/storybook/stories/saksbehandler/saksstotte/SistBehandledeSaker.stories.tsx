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
  requestApi.mock(RestApiGlobalStatePathsKeys.FPSAK_URL, { value: 'fpsak-url' });
  requestApi.mock(RestApiGlobalStatePathsKeys.FPTILBAKE_URL, { value: 'fptilbake-url' });
  requestApi.mock(RestApiPathsKeys.BEHANDLEDE_OPPGAVER);
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

  requestApi.mock(RestApiGlobalStatePathsKeys.FPSAK_URL, { value: 'fpsak-url' });
  requestApi.mock(RestApiGlobalStatePathsKeys.FPTILBAKE_URL, { value: 'fptilbake-url' });
  requestApi.mock(RestApiPathsKeys.BEHANDLEDE_OPPGAVER, behandledeOppgaver);

  return (
    <SistBehandledeSaker />
  );
};
