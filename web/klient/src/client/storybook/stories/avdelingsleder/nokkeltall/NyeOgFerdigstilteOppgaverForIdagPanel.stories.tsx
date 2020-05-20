import React from 'react';
import moment from 'moment';

import { ISO_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { TilBehandlingPanel, ALLE_YTELSETYPER_VALGT, UKE_2 } from 'avdelingsleder/nokkeltall/components/tilBehandling/TilBehandlingPanel';

import withIntl from '../../../decorators/withIntl';
import withRedux from '../../../decorators/withRedux';

export default {
  title: 'avdelingsleder/nokkeltall/TilBehandlingPanel',
  component: TilBehandlingPanel,
  decorators: [withIntl, withRedux],
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
    initialValues={{
      ytelseType: ALLE_YTELSETYPER_VALGT,
      ukevalg: UKE_2,
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
