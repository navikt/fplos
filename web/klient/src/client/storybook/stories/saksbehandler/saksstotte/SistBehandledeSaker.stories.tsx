import React from 'react';

import { RestApiProvider } from 'data/rest-api-hooks';
import SistBehandledeSaker from 'saksbehandler/saksstotte/components/SistBehandledeSaker';
import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/restApiPaths';

import RequestMock from '../../../mocks/RequestMock';
import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiGlobalStatePathsKeys.FPSAK_URL]: {
    value: 'fpsak-url',
  },
  [RestApiGlobalStatePathsKeys.FPTILBAKE_URL]: {
    value: 'fptilbake-url',
  },
};

export default {
  title: 'saksbehandler/saksstotte/SistBehandledeSaker',
  component: SistBehandledeSaker,
  decorators: [
    withIntl,
  ],
};

export const skalViseIngenBehandlinger = () => (
  <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
    <SistBehandledeSaker />
  </RestApiProvider>
);

export const skalViseSistBehandlendeSaker = () => {
  const behandledeOppgaver = [{
    behandlingId: 1,
    personnummer: '334342323',
    navn: 'Espen Utvikler',
  }];

  const requestApi = new RequestMock()
    .withKeyAndResult(RestApiPathsKeys.BEHANDLEDE_OPPGAVER, behandledeOppgaver)
    .build();

  return (
    <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={requestApi}>
      <SistBehandledeSaker />
    </RestApiProvider>
  );
};
