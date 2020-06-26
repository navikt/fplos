import React from 'react';
import moment from 'moment';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import behandlingType from 'kodeverk/behandlingType';
import { ISO_DATE_FORMAT } from 'utils/formats';
import { RestApiProvider } from 'data/rest-api-hooks';
import
NyeOgFerdigstilteOppgaverForIdagPanel
  from 'saksbehandler/saksstotte/nokkeltall/components/nyeOgFerdigstilteOppgaverForIdag/NyeOgFerdigstilteOppgaverForIdagPanel';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import RequestMock from '../../../mocks/RequestMock';
import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'saksbehandler/saksstotte/NyeOgFerdigstilteOppgaverForIdagPanel',
  component: NyeOgFerdigstilteOppgaverForIdagPanel,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
        {getStory()}
      </RestApiProvider>
    ),
  ],
};

export const skalViseGrafForNyeOgFerdigstilteOppgaverForIdag = () => (
  <NyeOgFerdigstilteOppgaverForIdagPanel
    width={700}
    height={300}
    nyeOgFerdigstilteOppgaver={[{
      behandlingType: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      antallNye: 10,
      antallFerdigstilte: 20,
      dato: moment().format(ISO_DATE_FORMAT),
    }, {
      behandlingType: {
        kode: behandlingType.KLAGE,
        navn: 'Klage',
      },
      antallNye: 23,
      antallFerdigstilte: 2,
      dato: moment().format(ISO_DATE_FORMAT),
    }, {
      behandlingType: {
        kode: behandlingType.REVURDERING,
        navn: 'Revurdering',
      },
      antallNye: 3,
      antallFerdigstilte: 24,
      dato: moment().format(ISO_DATE_FORMAT),
    }, {
      behandlingType: {
        kode: behandlingType.DOKUMENTINNSYN,
        navn: 'Dokumentinnsyn',
      },
      antallNye: 23,
      antallFerdigstilte: 12,
      dato: moment().format(ISO_DATE_FORMAT),
    }]}
  />
);
