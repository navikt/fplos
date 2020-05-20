import React from 'react';
import moment from 'moment';

import { ISO_DATE_FORMAT } from 'utils/formats';
import {
  OppgaverPerForsteStonadsdagPanel,
} from 'avdelingsleder/nokkeltall/components/antallBehandlingerPerForsteStonadsdag/OppgaverPerForsteStonadsdagPanel';

import withIntl from '../../../decorators/withIntl';
import withRedux from '../../../decorators/withRedux';

export default {
  title: 'avdelingsleder/nokkeltall/OppgaverPerForsteStonadsdagPanel',
  component: OppgaverPerForsteStonadsdagPanel,
  decorators: [withIntl, withRedux],
};

export const skalViseGrafForOppgaverPerFørsteStønadsdag = () => (
  <OppgaverPerForsteStonadsdagPanel
    width={700}
    height={300}
    oppgaverPerForsteStonadsdag={[{
      forsteStonadsdag: moment().subtract(14, 'd').format(ISO_DATE_FORMAT),
      antall: 10,
    }, {
      forsteStonadsdag: moment().subtract(13, 'd').format(ISO_DATE_FORMAT),
      antall: 9,
    }, {
      forsteStonadsdag: moment().subtract(12, 'd').format(ISO_DATE_FORMAT),
      antall: 6,
    }, {
      forsteStonadsdag: moment().subtract(11, 'd').format(ISO_DATE_FORMAT),
      antall: 11,
    }, {
      forsteStonadsdag: moment().subtract(10, 'd').format(ISO_DATE_FORMAT),
      antall: 15,
    }, {
      forsteStonadsdag: moment().subtract(9, 'd').format(ISO_DATE_FORMAT),
      antall: 20,
    }, {
      forsteStonadsdag: moment().subtract(8, 'd').format(ISO_DATE_FORMAT),
      antall: 13,
    }]}
  />
);
