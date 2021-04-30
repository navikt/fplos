import React, { useState } from 'react';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { GjeldendeSakslisterTabell } from 'avdelingsleder/behandlingskoer/components/GjeldendeSakslisterTabell';
import Saksliste from 'types/avdelingsleder/sakslisteAvdelingTsType';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'avdelingsleder/behandlingskoer/GjeldendeSakslisterTabell',
  component: GjeldendeSakslisterTabell,
  decorators: [withIntl, withRestApiProvider],
};

export const skalVisePanelNårDetIkkeFinnesBehandlingskøer = () => {
  const [sakslister, setSaksliste] = useState<Saksliste[]>([]);
  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);
  return (
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
  );
};

export const skalVisePanelNårDetFinnesEnBehandlingskø = () => {
  const [sakslister, setSaksliste] = useState<Saksliste[]>([{
    sakslisteId: 1,
    navn: 'Saksliste 1',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: ['R23233'],
    antallBehandlinger: 1,
  }]);
  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);
  return (
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
  );
};
