import React from 'react';
import dayjs from 'dayjs';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import behandlingType from 'kodeverk/behandlingType';
import { ISO_DATE_FORMAT } from 'utils/formats';

import NyeOgFerdigstilteOppgaver from 'types/saksbehandler/nyeOgFerdigstilteOppgaverTsType';
import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

import NyeOgFerdigstilteOppgaverForIdagPanel from './NyeOgFerdigstilteOppgaverForIdagPanel';

export default {
  title: 'saksbehandler/saksstotte/NyeOgFerdigstilteOppgaverForIdagPanel',
  component: NyeOgFerdigstilteOppgaverForIdagPanel,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

const Template: Story<{ nyeOgFerdigstilteOppgaver?: NyeOgFerdigstilteOppgaver[] }> = ({
  nyeOgFerdigstilteOppgaver,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
  ];

  return (
    <RestApiMock data={data}>
      <NyeOgFerdigstilteOppgaverForIdagPanel
        height={300}
        nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  nyeOgFerdigstilteOppgaver: [{
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    antallNye: 10,
    antallFerdigstilte: 20,
    dato: dayjs().format(ISO_DATE_FORMAT),
  }, {
    behandlingType: {
      kode: behandlingType.KLAGE,
      navn: 'Klage',
    },
    antallNye: 23,
    antallFerdigstilte: 2,
    dato: dayjs().format(ISO_DATE_FORMAT),
  }, {
    behandlingType: {
      kode: behandlingType.REVURDERING,
      navn: 'Revurdering',
    },
    antallNye: 3,
    antallFerdigstilte: 24,
    dato: dayjs().format(ISO_DATE_FORMAT),
  }, {
    behandlingType: {
      kode: behandlingType.DOKUMENTINNSYN,
      navn: 'Dokumentinnsyn',
    },
    antallNye: 23,
    antallFerdigstilte: 12,
    dato: dayjs().format(ISO_DATE_FORMAT),
  }],
};

export const IngenBehandlinger = Template.bind({});