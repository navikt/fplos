import React from 'react';
import dayjs from 'dayjs';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { ISO_DATE_FORMAT } from 'utils/formats';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import ManueltPaVentPanel from 'avdelingsleder/nokkeltall/components/manueltSattPaVent/ManueltPaVentPanel';
import OppgaverManueltPaVent from 'types/avdelingsleder/oppgaverManueltPaVentTsType';

import RestApiMock from '../../../utils/RestApiMock';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/nokkeltall/ManueltPaVentPanel',
  component: ManueltPaVentPanel,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

// https://github.com/storybookjs/storybook/issues/12208
const FIVE = 5;

const Template: Story<{ oppgaverManueltPaVent: OppgaverManueltPaVent[] }> = ({
  oppgaverManueltPaVent,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
  ];

  return (
    <RestApiMock data={data}>
      <ManueltPaVentPanel
        width={700}
        height={300}
        oppgaverManueltPaVent={oppgaverManueltPaVent}
        getValueFromLocalStorage={() => ''}
      />
    </RestApiMock>
  );
};

export const GrafForAntallBehandlingerSomErSattManueltPaVent = Template.bind({});
GrafForAntallBehandlingerSomErSattManueltPaVent.args = {
  oppgaverManueltPaVent: [{
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    },
    behandlingFrist: dayjs().format(ISO_DATE_FORMAT),
    antall: 10,
  }, {
    fagsakYtelseType: {
      kode: fagsakYtelseType.ENGANGSSTONAD,
      navn: 'Engangsstønad',
    },
    behandlingFrist: dayjs().add(FIVE, 'd').format(ISO_DATE_FORMAT),
    antall: 4,
  }, {
    fagsakYtelseType: {
      kode: fagsakYtelseType.ENGANGSSTONAD,
      navn: 'Engangsstønad',
    },
    behandlingFrist: dayjs().add(FIVE, 'w').format(ISO_DATE_FORMAT),
    antall: 14,
  }],
};
