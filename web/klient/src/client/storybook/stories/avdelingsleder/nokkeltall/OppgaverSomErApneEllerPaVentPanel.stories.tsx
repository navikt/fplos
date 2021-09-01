import React from 'react';
import dayjs from 'dayjs';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { ISO_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import OppgaverSomErApneEllerPaVentPanel from 'avdelingsleder/nokkeltall/components/apneOgPaVentBehandlinger/OppgaverSomErApneEllerPaVentPanel';
import behandlingVenteStatus from 'kodeverk/behandlingVenteStatus';
import OppgaverSomErApneEllerPaVent from 'types/avdelingsleder/oppgaverSomErApneEllerPaVentTsType';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';
import RestApiMock from '../../../utils/RestApiMock';

export default {
  title: 'avdelingsleder/nokkeltall/OppgaverSomErApneEllerPaVentPanel',
  component: OppgaverSomErApneEllerPaVentPanel,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

const Template: Story<{ oppgaverApneEllerPaVent: OppgaverSomErApneEllerPaVent[] }> = ({
  oppgaverApneEllerPaVent,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
  ];

  return (
    <RestApiMock data={data}>
      <OppgaverSomErApneEllerPaVentPanel
        height={300}
        oppgaverApneEllerPaVent={oppgaverApneEllerPaVent}
        getValueFromLocalStorage={() => ''}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  oppgaverApneEllerPaVent: [{
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.PA_VENT,
      navn: 'På vent',
    },
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    førsteUttakMåned: dayjs().startOf('month').format(ISO_DATE_FORMAT),
    antall: 2,
  }, {
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.IKKE_PA_VENT,
      navn: 'Ikke på vent',
    },
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    førsteUttakMåned: dayjs().startOf('month').format(ISO_DATE_FORMAT),
    antall: 5,
  }, {
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.IKKE_PA_VENT,
      navn: 'Ikke på vent',
    },
    behandlingType: {
      kode: behandlingType.REVURDERING,
      navn: 'Revurdering',
    },
    førsteUttakMåned: dayjs().startOf('month').subtract(4, 'M').format(ISO_DATE_FORMAT),
    antall: 2,
  }, {
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.IKKE_PA_VENT,
      navn: 'Ikke på vent',
    },
    behandlingType: {
      kode: behandlingType.KLAGE,
      navn: 'Klage',
    },
    antall: 2,
  }, {
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.PA_VENT,
      navn: 'På vent',
    },
    behandlingType: {
      kode: behandlingType.KLAGE,
      navn: 'Klage',
    },
    antall: 6,
  }, {
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.PA_VENT,
      navn: 'På vent',
    },
    behandlingType: {
      kode: behandlingType.REVURDERING,
      navn: 'Revurdering',
    },
    førsteUttakMåned: dayjs().startOf('month').subtract(4, 'M').format(ISO_DATE_FORMAT),
    antall: 6,
  }, {
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.PA_VENT,
      navn: 'På vent',
    },
    behandlingType: {
      kode: behandlingType.DOKUMENTINNSYN,
      navn: 'Dokumentinnsyn',
    },
    førsteUttakMåned: dayjs().startOf('month').subtract(10, 'M').format(ISO_DATE_FORMAT),
    antall: 3,
  }, {
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.IKKE_PA_VENT,
      navn: 'Ikke på vent',
    },
    behandlingType: {
      kode: behandlingType.DOKUMENTINNSYN,
      navn: 'Dokumentinnsyn',
    },
    førsteUttakMåned: dayjs().startOf('month').subtract(10, 'M').format(ISO_DATE_FORMAT),
    antall: 5,
  }],
};
