import React from 'react';

import { RestApiGlobalStatePathsKeys, requestApi } from 'data/fplosRestApi';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import behandlingType from 'kodeverk/behandlingType';
import {
  FordelingAvBehandlingstypePanel,
} from 'avdelingsleder/nokkeltall/components/fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/nokkeltall/FordelingAvBehandlingstypePanel',
  component: FordelingAvBehandlingstypePanel,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

export const skalViseGrafForFordelingAvBehandlingstyper = () => {
  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);
  return (
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
};
