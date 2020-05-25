import React from 'react';
import moment from 'moment';

import { ISO_DATE_FORMAT } from 'utils/formats';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { ManueltPaVentPanel, ALLE_YTELSETYPER_VALGT, UKE_4 } from 'avdelingsleder/nokkeltall/components/manueltSattPaVent/ManueltPaVentPanel';

import withIntl from '../../../decorators/withIntl';
import withRedux from '../../../decorators/withRedux';

export default {
  title: 'avdelingsleder/nokkeltall/ManueltPaVentPanel',
  component: ManueltPaVentPanel,
  decorators: [withIntl, withRedux],
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
    initialValues={{
      valgtYtelsetype: ALLE_YTELSETYPER_VALGT,
      ukevalg: UKE_4,
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
  />
);
