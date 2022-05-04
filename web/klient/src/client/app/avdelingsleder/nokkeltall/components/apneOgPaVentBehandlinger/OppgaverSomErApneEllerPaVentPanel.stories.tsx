import React from 'react';
import dayjs from 'dayjs';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { ISO_DATE_FORMAT } from '@navikt/ft-utils';
import behandlingType from 'kodeverk/behandlingType';
import OppgaverSomErApneEllerPaVentPanel from 'avdelingsleder/nokkeltall/components/apneOgPaVentBehandlinger/OppgaverSomErApneEllerPaVentPanel';
import behandlingVenteStatus from 'kodeverk/behandlingVenteStatus';
import OppgaverSomErApneEllerPaVent from 'types/avdelingsleder/oppgaverSomErApneEllerPaVentTsType';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

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
    behandlingVenteStatus: behandlingVenteStatus.PA_VENT,
    behandlingType: behandlingType.FORSTEGANGSSOKNAD,
    førsteUttakMåned: dayjs().startOf('month').format(ISO_DATE_FORMAT),
    antall: 2,
  }, {
    behandlingVenteStatus: behandlingVenteStatus.IKKE_PA_VENT,
    behandlingType: behandlingType.FORSTEGANGSSOKNAD,
    førsteUttakMåned: dayjs().startOf('month').format(ISO_DATE_FORMAT),
    antall: 5,
  }, {
    behandlingVenteStatus: behandlingVenteStatus.IKKE_PA_VENT,
    behandlingType: behandlingType.REVURDERING,
    førsteUttakMåned: dayjs().startOf('month').subtract(4, 'M').format(ISO_DATE_FORMAT),
    antall: 2,
  }, {
    behandlingVenteStatus: behandlingVenteStatus.IKKE_PA_VENT,
    behandlingType: behandlingType.KLAGE,
    antall: 2,
  }, {
    behandlingVenteStatus: behandlingVenteStatus.PA_VENT,
    behandlingType: behandlingType.KLAGE,
    antall: 6,
  }, {
    behandlingVenteStatus: behandlingVenteStatus.PA_VENT,
    behandlingType: behandlingType.REVURDERING,
    førsteUttakMåned: dayjs().startOf('month').subtract(4, 'M').format(ISO_DATE_FORMAT),
    antall: 6,
  }, {
    behandlingVenteStatus: behandlingVenteStatus.PA_VENT,
    behandlingType: behandlingType.DOKUMENTINNSYN,
    førsteUttakMåned: dayjs().startOf('month').subtract(10, 'M').format(ISO_DATE_FORMAT),
    antall: 3,
  }, {
    behandlingVenteStatus: behandlingVenteStatus.IKKE_PA_VENT,
    behandlingType: behandlingType.DOKUMENTINNSYN,
    førsteUttakMåned: dayjs().startOf('month').subtract(10, 'M').format(ISO_DATE_FORMAT),
    antall: 5,
  }],
};
