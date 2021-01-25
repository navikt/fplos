import React from 'react';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import LeggTilSaksbehandlerForm from 'avdelingsleder/saksbehandlere/components/LeggTilSaksbehandlerForm';

import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'avdelingsleder/saksbehandlere/LeggTilSaksbehandlerForm',
  component: LeggTilSaksbehandlerForm,
  decorators: [withIntl, withRestApiProvider],
};

export const skalVisePanelForÅLeggeTilSaksbehandlere = () => {
  const saksbehandler = {
    brukerIdent: 'R232323',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  };

  requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);

  return (
    <LeggTilSaksbehandlerForm
      avdelingensSaksbehandlere={[]}
      hentAvdelingensSaksbehandlere={action('button-click')}
      valgtAvdelingEnhet="NAV Viken"
    />
  );
};

export const skalVisePanelForNårSaksbehandlerErLagtTilAllerede = () => {
  const saksbehandler = {
    brukerIdent: 'R232323',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  };

  requestApi.mock(RestApiPathsKeys.SAKSBEHANDLER_SOK, saksbehandler);

  return (
    <LeggTilSaksbehandlerForm
      avdelingensSaksbehandlere={[saksbehandler]}
      hentAvdelingensSaksbehandlere={action('button-click')}
      valgtAvdelingEnhet="NAV Viken"
    />
  );
};
