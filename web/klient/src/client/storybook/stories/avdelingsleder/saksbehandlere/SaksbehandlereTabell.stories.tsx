import React, { useState, useCallback } from 'react';

import { SaksbehandlereTabell } from 'avdelingsleder/saksbehandlere/components/SaksbehandlereTabell';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/saksbehandlere/SaksbehandlereTabell',
  component: SaksbehandlereTabell,
  decorators: [withIntl],
};

export const skalViseTomTabell = () => (
  <SaksbehandlereTabell
    saksbehandlere={[]}
    fjernSaksbehandler={() => undefined}
    valgtAvdelingEnhet="NAV Viken"
  />
);

export const skalViseSaksbehandlereITabell = () => {
  const [saksbehandlere, fjernSaksbehandler] = useState([{
    brukerIdent: 'R12122',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  }, {
    brukerIdent: 'S53343',
    navn: 'Steffen',
    avdelingsnavn: ['NAV Oslo'],
  }]);

  const fjern = useCallback((brukerIdent) => {
    fjernSaksbehandler((oldState) => oldState.filter((s) => s.brukerIdent !== brukerIdent));
  }, [saksbehandlere]);

  return (
    <SaksbehandlereTabell
      saksbehandlere={saksbehandlere}
      fjernSaksbehandler={fjern as () => Promise<string>}
      valgtAvdelingEnhet="NAV Viken"
    />
  );
};
