import React from 'react';
import moment from 'moment';

import { ISO_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import {
  NyeOgFerdigstilteOppgaverForIdagPanel,
} from 'saksbehandler/saksstotte/nokkeltall/components/nyeOgFerdigstilteOppgaverForIdag/NyeOgFerdigstilteOppgaverForIdagPanel';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/saksstotte/NyeOgFerdigstilteOppgaverForIdagPanel',
  component: NyeOgFerdigstilteOppgaverForIdagPanel,
  decorators: [withIntl],
};

export const skalViseGrafForNyeOgFerdigstilteOppgaverForIdag = () => (
  <NyeOgFerdigstilteOppgaverForIdagPanel
    width={700}
    height={300}
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
