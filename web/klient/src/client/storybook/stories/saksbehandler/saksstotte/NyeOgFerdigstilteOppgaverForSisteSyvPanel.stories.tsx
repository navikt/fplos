import React from 'react';
import moment from 'moment';

import { ISO_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import {
  NyeOgFerdigstilteOppgaverForSisteSyvPanel,
} from 'saksbehandler/saksstotte/nokkeltall/components/nyeOgFerdigstilteOppgaverForSisteSyv/NyeOgFerdigstilteOppgaverForSisteSyvPanel';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/saksstotte/NyeOgFerdigstilteOppgaverForSisteSyvPanel',
  component: NyeOgFerdigstilteOppgaverForSisteSyvPanel,
  decorators: [withIntl],
};

export const skalViseGrafForNyeOgFerdigstilteOppgaver = () => (
  <NyeOgFerdigstilteOppgaverForSisteSyvPanel
    width={700}
    height={200}
    nyeOgFerdigstilteOppgaver={[{
      behandlingType: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      antallNye: 10,
      antallFerdigstilte: 20,
      dato: moment().subtract(1, 'd').format(ISO_DATE_FORMAT),
    }, {
      behandlingType: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      antallNye: 30,
      antallFerdigstilte: 15,
      dato: moment().subtract(3, 'd').format(ISO_DATE_FORMAT),
    }, {
      behandlingType: {
        kode: behandlingType.KLAGE,
        navn: 'Klage',
      },
      antallNye: 23,
      antallFerdigstilte: 2,
      dato: moment().subtract(4, 'd').format(ISO_DATE_FORMAT),
    }, {
      behandlingType: {
        kode: behandlingType.KLAGE,
        navn: 'Klage',
      },
      antallNye: 23,
      antallFerdigstilte: 2,
      dato: moment().subtract(5, 'd').format(ISO_DATE_FORMAT),
    }]}
  />
);
