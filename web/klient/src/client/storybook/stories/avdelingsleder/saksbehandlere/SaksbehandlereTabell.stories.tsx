import React from 'react';

import SaksbehandlereTabell from 'avdelingsleder/saksbehandlere/components/SaksbehandlereTabell';
import { RestApiGlobalDataProvider } from 'data/rest-api-hooks';

import withIntl from '../../../decorators/withIntl';
import RequestMock from '../../../mocks/RequestMock';

export default {
  title: 'avdelingsleder/saksbehandlere/SaksbehandlereTabell',
  component: SaksbehandlereTabell,
  decorators: [withIntl],
};

export const skalViseTomTabell = () => (
  <RestApiGlobalDataProvider requestApi={new RequestMock().build()}>
    <SaksbehandlereTabell
      saksbehandlere={[]}
      hentAvdelingensSaksbehandlere={() => undefined}
      valgtAvdelingEnhet="NAV Viken"
    />
  </RestApiGlobalDataProvider>
);

export const skalViseSaksbehandlereITabell = () => {
  const saksbehandlere = [{
    brukerIdent: 'R12122',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  }, {
    brukerIdent: 'S53343',
    navn: 'Steffen',
    avdelingsnavn: ['NAV Oslo'],
  }];

  return (
    <RestApiGlobalDataProvider requestApi={new RequestMock().build()}>
      <SaksbehandlereTabell
        saksbehandlere={saksbehandlere}
        hentAvdelingensSaksbehandlere={() => undefined}
        valgtAvdelingEnhet="NAV Viken"
      />
    </RestApiGlobalDataProvider>
  );
};
