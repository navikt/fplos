import React from 'react';

import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import behandlingType from 'kodeverk/behandlingType';
import {
  FordelingAvBehandlingstypePanel, ALLE_YTELSETYPER_VALGT,
} from 'avdelingsleder/nokkeltall/components/fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';

import withIntl from '../../../decorators/withIntl';
import withRedux from '../../../decorators/withRedux';

export default {
  title: 'avdelingsleder/nokkeltall/FordelingAvBehandlingstypePanel',
  component: FordelingAvBehandlingstypePanel,
  decorators: [withIntl, withRedux],
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
    fagsakYtelseTyper={[{
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldreprenger',
    }, {
      kode: fagsakYtelseType.ENGANGSSTONAD,
      navn: 'Engangsstønad',
    }, {
      kode: fagsakYtelseType.SVANGERSKAPPENGER,
      navn: 'Svangerskapspenger',
    }]}
    behandlingTyper={[{
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    }, {
      kode: behandlingType.KLAGE,
      navn: 'Klage',
    }, {
      kode: behandlingType.DOKUMENTINNSYN,
      navn: 'Dokumentinnsyn',
    }, {
      kode: behandlingType.REVURDERING,
      navn: 'Revurdering',
    }]}
  />
);
