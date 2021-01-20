import React from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { RestApiProvider } from 'data/rest-api-hooks';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import behandlingType from 'kodeverk/behandlingType';
import {
  FordelingAvBehandlingstypePanel,
} from 'avdelingsleder/nokkeltall/components/fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import RequestMock from '../../../mocks/RequestMock';
import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/nokkeltall/FordelingAvBehandlingstypePanel',
  component: FordelingAvBehandlingstypePanel,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
        {getStory()}
      </RestApiProvider>
    ),
  ],
};

export const skalViseGrafForFordelingAvBehandlingstyper = () => (
  <FordelingAvBehandlingstypePanel
    width={700}
    height={300}
    oppgaverForAvdeling={[{
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'Foreldreprenger',
      },
      behandlingType: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      tilBehandling: true,
      antall: 10,
    }, {
      fagsakYtelseType: {
        kode: fagsakYtelseType.ENGANGSSTONAD,
        navn: 'Engangsstønad',
      },
      behandlingType: {
        kode: behandlingType.KLAGE,
        navn: 'Klage',
      },
      tilBehandling: true,
      antall: 4,
    }, {
      fagsakYtelseType: {
        kode: fagsakYtelseType.ENGANGSSTONAD,
        navn: 'Engangsstønad',
      },
      behandlingType: {
        kode: behandlingType.REVURDERING,
        navn: 'Revurdering',
      },
      tilBehandling: true,
      antall: 14,
    }]}
    getValueFromLocalStorage={() => ''}
  />
);
