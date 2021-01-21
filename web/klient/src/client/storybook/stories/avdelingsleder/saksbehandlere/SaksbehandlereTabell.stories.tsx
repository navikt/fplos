import React from 'react';

import SaksbehandlereTabell from 'avdelingsleder/saksbehandlere/components/SaksbehandlereTabell';

import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/saksbehandlere/SaksbehandlereTabell',
  component: SaksbehandlereTabell,
  decorators: [withIntl, withRestApiProvider],
};

export const skalViseTomTabell = () => (
  <SaksbehandlereTabell
    saksbehandlere={[]}
    hentAvdelingensSaksbehandlere={() => undefined}
    valgtAvdelingEnhet="NAV Viken"
  />
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
    <SaksbehandlereTabell
      saksbehandlere={saksbehandlere}
      hentAvdelingensSaksbehandlere={() => undefined}
      valgtAvdelingEnhet="NAV Viken"
    />
  );
};
