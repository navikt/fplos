import React from 'react';
import { Story } from '@storybook/react';

import OppgaverForAvdeling from 'types/avdelingsleder/oppgaverForAvdelingTsType';
import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import behandlingType from 'kodeverk/behandlingType';
import {
  FordelingAvBehandlingstypePanel,
} from 'avdelingsleder/nokkeltall/components/fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

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
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
    behandlingType: behandlingType.FORSTEGANGSSOKNAD,
    tilBehandling: true,
    antall: 10,
  }, {
    fagsakYtelseType: fagsakYtelseType.ENGANGSSTONAD,
    behandlingType: behandlingType.KLAGE,
    tilBehandling: true,
    antall: 4,
  }, {
    fagsakYtelseType: fagsakYtelseType.ENGANGSSTONAD,
    behandlingType: behandlingType.REVURDERING,
    tilBehandling: true,
    antall: 14,
  }, {
    fagsakYtelseType: fagsakYtelseType.ENGANGSSTONAD,
    behandlingType: behandlingType.REVURDERING,
    tilBehandling: false,
    antall: 4,
  }, {
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
    behandlingType: behandlingType.TILBAKEBETALING,
    tilBehandling: false,
    antall: 6,
  }],
};
