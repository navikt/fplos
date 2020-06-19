import React from 'react';

import SaksbehandlereTabell from 'avdelingsleder/saksbehandlere/components/SaksbehandlereTabell';
import { RestApiProvider } from 'data/rest-api-hooks';

import withIntl from '../../../decorators/withIntl';
import RequestMock from '../../../mocks/RequestMock';

export default {
  title: 'avdelingsleder/saksbehandlere/SaksbehandlereTabell',
  component: SaksbehandlereTabell,
  decorators: [withIntl],
};

export const skalViseTomTabell = () => (
  <RestApiProvider requestApi={new RequestMock().build()}>
    <SaksbehandlereTabell
      saksbehandlere={[]}
      hentAvdelingensSaksbehandlere={() => undefined}
      valgtAvdelingEnhet="NAV Viken"
    />
  </RestApiProvider>
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
    <RestApiProvider requestApi={new RequestMock().build()}>
      <SaksbehandlereTabell
        saksbehandlere={saksbehandlere}
        hentAvdelingensSaksbehandlere={() => undefined}
        valgtAvdelingEnhet="NAV Viken"
      />
    </RestApiProvider>
  );
};
