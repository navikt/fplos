import React from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiPathsKeys } from 'data/restApiPaths';
import { LeggTilSaksbehandlerForm } from 'avdelingsleder/saksbehandlere/components/LeggTilSaksbehandlerForm';
import { RestApiProvider } from 'data/rest-api-hooks';

import withIntl from '../../../decorators/withIntl';
import RequestMock from '../../../mocks/RequestMock';

export default {
  title: 'avdelingsleder/saksbehandlere/LeggTilSaksbehandlerForm',
  component: LeggTilSaksbehandlerForm,
  decorators: [withIntl],
};

export const skalVisePanelForÅLeggeTilSaksbehandlere = (intl) => {
  const saksbehandler = {
    brukerIdent: 'R232323',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  };

  const requestApi = new RequestMock()
    .withKeyAndResult(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler)
    .build();

  return (
    <RestApiProvider requestApi={requestApi}>
      <LeggTilSaksbehandlerForm
        intl={intl}
        avdelingensSaksbehandlere={[]}
        hentAvdelingensSaksbehandlere={action('button-click')}
        valgtAvdelingEnhet="NAV Viken"
      />
    </RestApiProvider>
  );
};

export const skalVisePanelForNårSaksbehandlerErLagtTilAllerede = (intl) => {
  const saksbehandler = {
    brukerIdent: 'R232323',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  };

  const requestApi = new RequestMock()
    .withKeyAndResult(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler)
    .build();

  return (
    <RestApiProvider requestApi={requestApi}>
      <LeggTilSaksbehandlerForm
        intl={intl}
        avdelingensSaksbehandlere={[saksbehandler]}
        hentAvdelingensSaksbehandlere={action('button-click')}
        valgtAvdelingEnhet="NAV Viken"
      />
    </RestApiProvider>
  );
};
