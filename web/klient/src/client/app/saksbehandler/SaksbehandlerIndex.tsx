import React, { useState } from 'react';

import SaksbehandlerDashboard from './components/SaksbehandlerDashboard';

/**
 * SaksbehandlerIndex
 */
const SaksbehandlerIndex = () => {
  const [valgtSakslisteId, setValgtSakslisteId] = useState<number>();
  return (
    <SaksbehandlerDashboard valgtSakslisteId={valgtSakslisteId} setValgtSakslisteId={setValgtSakslisteId} />
  );
};

export default SaksbehandlerIndex;
