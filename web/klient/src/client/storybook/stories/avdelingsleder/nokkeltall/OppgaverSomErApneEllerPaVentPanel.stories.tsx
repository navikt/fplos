import React from 'react';
import moment from 'moment';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { ISO_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import OppgaverSomErApneEllerPaVentPanel from 'avdelingsleder/nokkeltall/components/apneOgPaVentBehandlinger/OppgaverSomErApneEllerPaVentPanel';
import behandlingVenteStatus from 'kodeverk/behandlingVenteStatus';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/nokkeltall/OppgaverSomErApneEllerPaVentPanel',
  component: OppgaverSomErApneEllerPaVentPanel,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

export const skalViseGrafForAntallOppgaverTilBehandlingPerDag = () => {
  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

  return (
    <OppgaverSomErApneEllerPaVentPanel
      width={700}
      height={300}
      oppgaverApneEllerPaVent={[{
        behandlingVenteStatus: {
          kode: behandlingVenteStatus.PA_VENT,
          navn: 'På vent',
        },
        behandlingType: {
          kode: behandlingType.FORSTEGANGSSOKNAD,
          navn: 'Førstegangssøknad',
        },
        førsteUttakMåned: moment().startOf('month').format(ISO_DATE_FORMAT),
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
        førsteUttakMåned: moment().startOf('month').format(ISO_DATE_FORMAT),
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
        førsteUttakMåned: moment().startOf('month').subtract(4, 'M').format(ISO_DATE_FORMAT),
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
        førsteUttakMåned: moment().startOf('month').subtract(4, 'M').format(ISO_DATE_FORMAT),
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
        førsteUttakMåned: moment().startOf('month').subtract(10, 'M').format(ISO_DATE_FORMAT),
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
        førsteUttakMåned: moment().startOf('month').subtract(10, 'M').format(ISO_DATE_FORMAT),
        antall: 5,
      }]}
      getValueFromLocalStorage={() => ''}
    />
  );
};
