import React from 'react';
import moment from 'moment';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { RestApiProvider } from 'data/rest-api-hooks';
import { ISO_DATE_FORMAT } from 'utils/formats';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { ManueltPaVentPanel } from 'avdelingsleder/nokkeltall/components/manueltSattPaVent/ManueltPaVentPanel';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import RequestMock from '../../../mocks/RequestMock';
import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/nokkeltall/ManueltPaVentPanel',
  component: ManueltPaVentPanel,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
        {getStory()}
      </RestApiProvider>
    ),
  ],
};

export const skalViseGrafForAntallBehandlingerSomErSattManueltPåVent = (intl) => (
  <ManueltPaVentPanel
    intl={intl}
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
      behandlingFrist: moment().add(5, 'd').format(ISO_DATE_FORMAT),
      antall: 4,
    }, {
      fagsakYtelseType: {
        kode: fagsakYtelseType.ENGANGSSTONAD,
        navn: 'Engangsstønad',
      },
      behandlingFrist: moment().add(5, 'w').format(ISO_DATE_FORMAT),
      antall: 14,
    }]}
    getValueFromLocalStorage={() => ''}
  />
);
