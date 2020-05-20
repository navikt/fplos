import React, { useCallback, useState } from 'react';

import { SaksbehandlereForSakslisteForm } from 'avdelingsleder/behandlingskoer/components/saksbehandlerForm/SaksbehandlereForSakslisteForm';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/behandlingskoer/SaksbehandlereForSakslisteForm',
  component: SaksbehandlereForSakslisteForm,
  decorators: [withIntl],
};

export const skalVisePanelForÅLeggeSaksbehandlereTilEnSaksliste = () => {
  const [saksliste, setSaksliste] = useState({
    sakslisteId: 1,
    navn: 'Saksliste 1',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: ['S34354'],
    antallBehandlinger: 1,
  });

  const leggTilSaksbehandler = useCallback((_sakslisteId, brukerIdent, isChecked) => {
    setSaksliste((oldState) => ({
      ...oldState,
      saksbehandlerIdenter: isChecked
        ? oldState.saksbehandlerIdenter.concat(brukerIdent)
        : oldState.saksbehandlerIdenter.filter((i) => i !== brukerIdent),
    }));
  }, []);

  return (
    <SaksbehandlereForSakslisteForm
      valgtSaksliste={saksliste}
      avdelingensSaksbehandlere={[{
        brukerIdent: 'E23232',
        navn: 'Espen Utvikler',
        avdelingsnavn: ['NAV Viken'],
      }, {
        brukerIdent: 'S34354',
        navn: 'Steffen',
        avdelingsnavn: ['NAV Viken'],
      }, {
        brukerIdent: 'E24353',
        navn: 'Eirik',
        avdelingsnavn: ['NAV Viken'],
      }]}
      knyttSaksbehandlerTilSaksliste={leggTilSaksbehandler}
      valgtAvdelingEnhet="NAV Viken"
    />
  );
};
