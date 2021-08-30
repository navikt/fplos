import React, { FunctionComponent, useState, useCallback } from 'react';

import Saksbehandler from 'types/avdelingsleder/saksbehandlerAvdelingTsType';
import EndreSakslisterPanel from './components/EndreSakslisterPanel';

interface OwnProps {
  valgtAvdelingEnhet: string;
  avdelingensSaksbehandlere: Saksbehandler[];
}

/**
 * EndreBehandlingskoerIndex
 */
const EndreBehandlingskoerIndex: FunctionComponent<OwnProps> = ({
  valgtAvdelingEnhet,
  avdelingensSaksbehandlere,
}) => {
  const [valgtSakslisteId, setValgtSakslisteId] = useState<number>();
  const resetValgtSakslisteId = useCallback(() => setValgtSakslisteId(undefined), []);
  return (
    <EndreSakslisterPanel
      setValgtSakslisteId={setValgtSakslisteId}
      valgtSakslisteId={valgtSakslisteId}
      valgtAvdelingEnhet={valgtAvdelingEnhet}
      avdelingensSaksbehandlere={avdelingensSaksbehandlere}
      resetValgtSakslisteId={resetValgtSakslisteId}
    />
  );
};

export default EndreBehandlingskoerIndex;
