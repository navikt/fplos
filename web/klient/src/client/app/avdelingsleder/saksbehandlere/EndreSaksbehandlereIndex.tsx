import React, { FunctionComponent } from 'react';

import Saksbehandler from 'types/avdelingsleder/saksbehandlerTsType';

import SaksbehandlerePanel from './components/SaksbehandlerePanel';

interface OwnProps {
  avdelingensSaksbehandlere: Saksbehandler[];
  valgtAvdelingEnhet: string;
  hentAvdelingensSaksbehandlere: (params: {avdelingEnhet: string}) => void;
}

/**
 * EndreSaksbehandlereIndex
 */
const EndreSaksbehandlereIndex: FunctionComponent<OwnProps> = ({
  valgtAvdelingEnhet,
  avdelingensSaksbehandlere,
  hentAvdelingensSaksbehandlere,
}) => (
  <SaksbehandlerePanel
    saksbehandlere={avdelingensSaksbehandlere}
    valgtAvdelingEnhet={valgtAvdelingEnhet}
    avdelingensSaksbehandlere={avdelingensSaksbehandlere}
    hentAvdelingensSaksbehandlere={hentAvdelingensSaksbehandlere}
  />
);

export default EndreSaksbehandlereIndex;
