import React from 'react';

import { RestApiPathsKeys } from 'data/restApiPaths';
import { RestDataProvider } from 'data/RestDataContext';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import behandlingType from 'kodeverk/behandlingType';
import {
  FordelingAvBehandlingstypePanel, ALLE_YTELSETYPER_VALGT,
} from 'avdelingsleder/nokkeltall/components/fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withIntl from '../../../decorators/withIntl';
import withRedux from '../../../decorators/withRedux';

const initialState = {
  [RestApiPathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/nokkeltall/FordelingAvBehandlingstypePanel',
  component: FordelingAvBehandlingstypePanel,
  decorators: [
    withIntl,
    withRedux,
    (getStory) => <RestDataProvider initialState={initialState as {[key in RestApiPathsKeys]: any}}>{getStory()}</RestDataProvider>,
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
    initialValues={{
      valgtYtelseType: ALLE_YTELSETYPER_VALGT,
    }}
  />
);
