import React from 'react';
import { action } from '@storybook/addon-actions';

import { SaksbehandlereForSakslisteForm } from 'avdelingsleder/behandlingskoer/components/saksbehandlerForm/SaksbehandlereForSakslisteForm';
import { RestApiGlobalDataProvider } from 'data/rest-api-hooks';

import withIntl from '../../../decorators/withIntl';
import RequestMock from '../../../mocks/RequestMock';

export default {
  title: 'avdelingsleder/behandlingskoer/SaksbehandlereForSakslisteForm',
  component: SaksbehandlereForSakslisteForm,
  decorators: [withIntl],
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
    <RestApiGlobalDataProvider requestApi={new RequestMock().build()}>
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
    </RestApiGlobalDataProvider>
  );
};
