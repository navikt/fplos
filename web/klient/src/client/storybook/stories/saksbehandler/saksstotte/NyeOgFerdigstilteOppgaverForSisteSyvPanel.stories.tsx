import React from 'react';
import dayjs from 'dayjs';

import { ISO_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import
NyeOgFerdigstilteOppgaverForSisteSyvPanel
  from 'saksbehandler/saksstotte/nokkeltall/components/nyeOgFerdigstilteOppgaverForSisteSyv/NyeOgFerdigstilteOppgaverForSisteSyvPanel';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/saksstotte/NyeOgFerdigstilteOppgaverForSisteSyvPanel',
  component: NyeOgFerdigstilteOppgaverForSisteSyvPanel,
  decorators: [withIntl],
};

export const GrafForNyeOgFerdigstilteOppgaver = () => (
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
      dato: dayjs().subtract(1, 'd').format(ISO_DATE_FORMAT),
    }, {
      behandlingType: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      antallNye: 30,
      antallFerdigstilte: 15,
      dato: dayjs().subtract(3, 'd').format(ISO_DATE_FORMAT),
    }, {
      behandlingType: {
        kode: behandlingType.KLAGE,
        navn: 'Klage',
      },
      antallNye: 23,
      antallFerdigstilte: 2,
      dato: dayjs().subtract(4, 'd').format(ISO_DATE_FORMAT),
    }, {
      behandlingType: {
        kode: behandlingType.KLAGE,
        navn: 'Klage',
      },
      antallNye: 23,
      antallFerdigstilte: 2,
      dato: dayjs().subtract(5, 'd').format(ISO_DATE_FORMAT),
    }]}
  />
);
