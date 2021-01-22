import React from 'react';
import moment from 'moment';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { ISO_DATE_FORMAT } from 'utils/formats';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import ManueltPaVentPanel from 'avdelingsleder/nokkeltall/components/manueltSattPaVent/ManueltPaVentPanel';

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

export const skalViseGrafForAntallBehandlingerSomErSattManueltPaVent = () => {
  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
  return (
    <ManueltPaVentPanel
      width={700}
      height={300}
      oppgaverManueltPaVent={[{
        fagsakYtelseType: {
          kode: fagsakYtelseType.FORELDREPRENGER,
          navn: 'Foreldreprenger',
        },
        behandlingFrist: moment().format(ISO_DATE_FORMAT),
        antall: 10,
      }, {
        fagsakYtelseType: {
          kode: fagsakYtelseType.ENGANGSSTONAD,
          navn: 'Engangsstønad',
        },
        behandlingFrist: moment().add(FIVE, 'd').format(ISO_DATE_FORMAT),
        antall: 4,
      }, {
        fagsakYtelseType: {
          kode: fagsakYtelseType.ENGANGSSTONAD,
          navn: 'Engangsstønad',
        },
        behandlingFrist: moment().add(FIVE, 'w').format(ISO_DATE_FORMAT),
        antall: 14,
      }]}
      getValueFromLocalStorage={() => ''}
    />
  );
};
