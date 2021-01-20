import React from 'react';
import moment from 'moment';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { RestApiProvider } from 'data/rest-api-hooks';
import { ISO_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { TilBehandlingPanel } from 'avdelingsleder/nokkeltall/components/tilBehandling/TilBehandlingPanel';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import RequestMock from '../../../mocks/RequestMock';
import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/nokkeltall/TilBehandlingPanel',
  component: TilBehandlingPanel,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
        {getStory()}
      </RestApiProvider>
    ),
  ],
};

export const skalViseGrafForAntallOppgaverTilBehandlingPerDag = (intl) => (
  <TilBehandlingPanel
    intl={intl}
    width={700}
    height={300}
    oppgaverPerDato={[{
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
    }]}
    getValueFromLocalStorage={() => ''}
  />
);
