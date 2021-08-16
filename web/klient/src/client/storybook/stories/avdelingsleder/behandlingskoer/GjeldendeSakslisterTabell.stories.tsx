import React, { useState } from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { GjeldendeSakslisterTabell } from 'avdelingsleder/behandlingskoer/components/GjeldendeSakslisterTabell';
import Saksliste from 'types/avdelingsleder/sakslisteAvdelingTsType';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';
import RestApiMock from '../../../utils/RestApiMock';

export default {
  title: 'avdelingsleder/behandlingskoer/GjeldendeSakslisterTabell',
  component: GjeldendeSakslisterTabell,
  decorators: [withIntl, withRestApiProvider],
};

interface Props {
  saksliste: Saksliste[];
  nyeSakslister: Saksliste[];
  valgtSakslisteId?: number;
  oppgaverForAvdelingAntall?: number;
}

const Template: Story<Props> = ({
  saksliste,
  nyeSakslister,
  valgtSakslisteId,
  oppgaverForAvdelingAntall,
}) => {
  const [sakslister, setSaksliste] = useState<Saksliste[]>(saksliste || []);

  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
  ];

  return (
    <RestApiMock data={data}>
      <GjeldendeSakslisterTabell
        sakslister={sakslister}
        valgtAvdelingEnhet=""
        setValgtSakslisteId={action('button-click')}
        lagNySaksliste={() => setSaksliste((oldState) => oldState.concat(nyeSakslister))}
        resetValgtSakslisteId={action('button-click')}
        hentAvdelingensSakslister={action('button-click') as () => Saksliste[]}
        valgtSakslisteId={valgtSakslisteId}
        oppgaverForAvdelingAntall={oppgaverForAvdelingAntall}
      />
    </RestApiMock>
  );
};

export const TabellNårDetIkkeFinnesBehandlingskøer = Template.bind({});
TabellNårDetIkkeFinnesBehandlingskøer.args = {
  nyeSakslister: [{
    sakslisteId: 1,
    navn: 'Ny liste',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: [],
    antallBehandlinger: 1,
  }],
};

export const TabellNårDetFinnesEnBehandlingskø = Template.bind({});
TabellNårDetFinnesEnBehandlingskø.args = {
  saksliste: [{
    sakslisteId: 1,
    navn: 'Saksliste 1',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: ['R23233'],
    antallBehandlinger: 1,
  }],
  valgtSakslisteId: 1,
  nyeSakslister: [{
    sakslisteId: 2,
    navn: 'Ny liste',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: [],
    antallBehandlinger: 1,
  }],
  oppgaverForAvdelingAntall: 1,
};
