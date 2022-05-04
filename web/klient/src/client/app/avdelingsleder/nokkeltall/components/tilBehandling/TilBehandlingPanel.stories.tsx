import React from 'react';
import dayjs from 'dayjs';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { ISO_DATE_FORMAT } from '@navikt/ft-utils';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import TilBehandlingPanel from 'avdelingsleder/nokkeltall/components/tilBehandling/TilBehandlingPanel';
import OppgaveForDato from 'types/avdelingsleder/oppgaverForDatoTsType';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

export default {
  title: 'avdelingsleder/nokkeltall/TilBehandlingPanel',
  component: TilBehandlingPanel,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

const Template: Story<{ oppgaverPerDato: OppgaveForDato[] }> = ({
  oppgaverPerDato,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
  ];

  return (
    <RestApiMock data={data}>
      <TilBehandlingPanel
        height={300}
        oppgaverPerDato={oppgaverPerDato}
        getValueFromLocalStorage={() => ''}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  oppgaverPerDato: [{
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
    behandlingType: behandlingType.FORSTEGANGSSOKNAD,
    opprettetDato: dayjs().format(ISO_DATE_FORMAT),
    antall: 1,
  }, {
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
    behandlingType: behandlingType.FORSTEGANGSSOKNAD,
    opprettetDato: dayjs().subtract(3, 'd').format(ISO_DATE_FORMAT),
    antall: 2,
  }, {
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
    behandlingType: behandlingType.KLAGE,
    opprettetDato: dayjs().subtract(4, 'd').format(ISO_DATE_FORMAT),
    antall: 2,
  }, {
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
    behandlingType: behandlingType.FORSTEGANGSSOKNAD,
    opprettetDato: dayjs().subtract(4, 'd').format(ISO_DATE_FORMAT),
    antall: 6,
  }, {
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
    behandlingType: behandlingType.DOKUMENTINNSYN,
    opprettetDato: dayjs().subtract(10, 'd').format(ISO_DATE_FORMAT),
    antall: 3,
  }, {
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
    behandlingType: behandlingType.DOKUMENTINNSYN,
    opprettetDato: dayjs().subtract(16, 'd').format(ISO_DATE_FORMAT),
    antall: 3,
  }],
};
