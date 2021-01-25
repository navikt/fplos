import React, { useState } from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { RestApiProvider } from 'data/rest-api-hooks';
import { GjeldendeSakslisterTabell } from 'avdelingsleder/behandlingskoer/components/GjeldendeSakslisterTabell';
import Saksliste from 'avdelingsleder/behandlingskoer/sakslisteTsType';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withIntl from '../../../decorators/withIntl';
import RequestMock from '../../../mocks/RequestMock';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/GjeldendeSakslisterTabell',
  component: GjeldendeSakslisterTabell,
  decorators: [withIntl],
};

export const skalVisePanelNårDetIkkeFinnesBehandlingskøer = () => {
  const [sakslister, setSaksliste] = useState([]);
  return (
    <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
      <GjeldendeSakslisterTabell
        sakslister={sakslister}
        valgtAvdelingEnhet=""
        setValgtSakslisteId={action('button-click')}
        lagNySaksliste={() => setSaksliste([{
          sakslisteId: 1,
          navn: 'Ny liste',
          sistEndret: '2020-01-01',
          saksbehandlerIdenter: [],
          antallBehandlinger: 1,
        }])}
        resetValgtSakslisteId={action('button-click')}
        hentAvdelingensSakslister={action('button-click') as () => Saksliste[]}
      />
    </RestApiProvider>
  );
};

export const skalVisePanelNårDetFinnesEnBehandlingskø = () => {
  const [sakslister, setSaksliste] = useState([{
    sakslisteId: 1,
    navn: 'Saksliste 1',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: ['R23233'],
    antallBehandlinger: 1,
  }]);
  return (
    <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
      <GjeldendeSakslisterTabell
        sakslister={sakslister}
        valgtAvdelingEnhet=""
        valgtSakslisteId={1}
        setValgtSakslisteId={action('button-click')}
        lagNySaksliste={() => setSaksliste([{
          sakslisteId: 2,
          navn: 'Ny liste',
          sistEndret: '2020-01-01',
          saksbehandlerIdenter: [],
          antallBehandlinger: 1,
        }])}
        resetValgtSakslisteId={action('button-click')}
        hentAvdelingensSakslister={action('button-click') as () => Saksliste[]}
        oppgaverForAvdelingAntall={1}
      />
    </RestApiProvider>
  );
};
