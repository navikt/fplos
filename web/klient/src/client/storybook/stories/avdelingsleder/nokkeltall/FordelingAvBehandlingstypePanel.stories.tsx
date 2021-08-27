import React from 'react';
import { Story } from '@storybook/react';

import OppgaverForAvdeling from 'types/avdelingsleder/oppgaverForAvdelingTsType';
import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import behandlingType from 'kodeverk/behandlingType';
import {
  FordelingAvBehandlingstypePanel,
} from 'avdelingsleder/nokkeltall/components/fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';

import RestApiMock from '../../../utils/RestApiMock';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/nokkeltall/FordelingAvBehandlingstypePanel',
  component: FordelingAvBehandlingstypePanel,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

const Template: Story<{ oppgaverForAvdeling: OppgaverForAvdeling[] }> = ({
  oppgaverForAvdeling,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
  ];

  return (
    <RestApiMock data={data}>
      <FordelingAvBehandlingstypePanel
        width={700}
        height={300}
        oppgaverForAvdeling={oppgaverForAvdeling}
        getValueFromLocalStorage={() => ''}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  oppgaverForAvdeling: [{
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    },
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    tilBehandling: true,
    antall: 10,
  }, {
    fagsakYtelseType: {
      kode: fagsakYtelseType.ENGANGSSTONAD,
      navn: 'Engangsstønad',
    },
    behandlingType: {
      kode: behandlingType.KLAGE,
      navn: 'Klage',
    },
    tilBehandling: true,
    antall: 4,
  }, {
    fagsakYtelseType: {
      kode: fagsakYtelseType.ENGANGSSTONAD,
      navn: 'Engangsstønad',
    },
    behandlingType: {
      kode: behandlingType.REVURDERING,
      navn: 'Revurdering',
    },
    tilBehandling: true,
    antall: 14,
  }],
};
