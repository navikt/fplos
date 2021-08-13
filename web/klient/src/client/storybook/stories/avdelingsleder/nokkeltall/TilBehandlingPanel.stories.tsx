import React from 'react';
import moment from 'moment';
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
        width={700}
        height={300}
        oppgaverPerDato={oppgaverPerDato}
        getValueFromLocalStorage={() => ''}
      />
    </RestApiMock>
  );
};

export const GrafForAntallOppgaverTilBehandlingPerDag = Template.bind({});
GrafForAntallOppgaverTilBehandlingPerDag.args = {
  oppgaverPerDato: [{
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    },
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    opprettetDato: moment().format(ISO_DATE_FORMAT),
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
    opprettetDato: moment().subtract(3, 'd').format(ISO_DATE_FORMAT),
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
    opprettetDato: moment().subtract(4, 'd').format(ISO_DATE_FORMAT),
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
    opprettetDato: moment().subtract(4, 'd').format(ISO_DATE_FORMAT),
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
    opprettetDato: moment().subtract(10, 'd').format(ISO_DATE_FORMAT),
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
    opprettetDato: moment().subtract(16, 'd').format(ISO_DATE_FORMAT),
    antall: 3,
  }],
};
