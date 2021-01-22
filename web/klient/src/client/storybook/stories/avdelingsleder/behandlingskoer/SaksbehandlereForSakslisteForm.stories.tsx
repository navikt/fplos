import React from 'react';
import { action } from '@storybook/addon-actions';

import SaksbehandlereForSakslisteForm from 'avdelingsleder/behandlingskoer/components/saksbehandlerForm/SaksbehandlereForSakslisteForm';

import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'avdelingsleder/behandlingskoer/SaksbehandlereForSakslisteForm',
  component: SaksbehandlereForSakslisteForm,
  decorators: [withIntl, withRestApiProvider],
};

export const skalVisePanelForÅLeggeSaksbehandlereTilEnSaksliste = () => {
  const saksliste = {
    sakslisteId: 1,
    navn: 'Saksliste 1',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: ['S34354'],
    antallBehandlinger: 1,
  };

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
      hentAvdelingensSakslister={action('button-click')}
      valgtAvdelingEnhet="NAV Viken"
    />
  );
};
