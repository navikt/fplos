import React from 'react';
import dayjs from 'dayjs';

import { ISO_DATE_FORMAT } from '@navikt/ft-utils';
import behandlingType from 'kodeverk/behandlingType';

import withIntl from 'storybookUtils/decorators/withIntl';
import NyeOgFerdigstilteOppgaverForSisteSyvPanel from './NyeOgFerdigstilteOppgaverForSisteSyvPanel';

export default {
  title: 'saksbehandler/saksstotte/NyeOgFerdigstilteOppgaverForSisteSyvPanel',
  component: NyeOgFerdigstilteOppgaverForSisteSyvPanel,
  decorators: [withIntl],
};

export const Default = () => (
  <NyeOgFerdigstilteOppgaverForSisteSyvPanel
    height={200}
    nyeOgFerdigstilteOppgaver={[{
      behandlingType: behandlingType.FORSTEGANGSSOKNAD,
      antallNye: 10,
      antallFerdigstilte: 20,
      dato: dayjs().subtract(1, 'd').format(ISO_DATE_FORMAT),
    }, {
      behandlingType: behandlingType.FORSTEGANGSSOKNAD,
      antallNye: 30,
      antallFerdigstilte: 15,
      dato: dayjs().subtract(3, 'd').format(ISO_DATE_FORMAT),
    }, {
      behandlingType: behandlingType.KLAGE,
      antallNye: 23,
      antallFerdigstilte: 2,
      dato: dayjs().subtract(4, 'd').format(ISO_DATE_FORMAT),
    }, {
      behandlingType: behandlingType.KLAGE,
      antallNye: 23,
      antallFerdigstilte: 2,
      dato: dayjs().subtract(5, 'd').format(ISO_DATE_FORMAT),
    }]}
  />
);
