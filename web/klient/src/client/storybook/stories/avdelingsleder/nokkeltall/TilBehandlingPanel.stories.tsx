import React from 'react';
import dayjs from 'dayjs';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { ISO_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import TilBehandlingPanel from 'avdelingsleder/nokkeltall/components/tilBehandling/TilBehandlingPanel';
import OppgaveForDato from 'types/avdelingsleder/oppgaverForDatoTsType';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';
import RestApiMock from '../../../utils/RestApiMock';

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
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    },
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    opprettetDato: dayjs().format(ISO_DATE_FORMAT),
    antall: 1,
  }, {
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    },
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    opprettetDato: dayjs().subtract(3, 'd').format(ISO_DATE_FORMAT),
    antall: 2,
  }, {
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    },
    behandlingType: {
      kode: behandlingType.KLAGE,
      navn: 'Klage',
    },
    opprettetDato: dayjs().subtract(4, 'd').format(ISO_DATE_FORMAT),
    antall: 2,
  }, {
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    },
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    opprettetDato: dayjs().subtract(4, 'd').format(ISO_DATE_FORMAT),
    antall: 6,
  }, {
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    },
    behandlingType: {
      kode: behandlingType.DOKUMENTINNSYN,
      navn: 'Dokumentinnsyn',
    },
    opprettetDato: dayjs().subtract(10, 'd').format(ISO_DATE_FORMAT),
    antall: 3,
  }, {
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    },
    behandlingType: {
      kode: behandlingType.DOKUMENTINNSYN,
      navn: 'Dokumentinnsyn',
    },
    opprettetDato: dayjs().subtract(16, 'd').format(ISO_DATE_FORMAT),
    antall: 3,
  }],
};
