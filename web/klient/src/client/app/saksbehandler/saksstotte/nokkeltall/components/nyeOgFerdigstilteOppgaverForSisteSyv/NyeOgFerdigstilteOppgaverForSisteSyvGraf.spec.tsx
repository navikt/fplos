import React from 'react';
import { IntlShape } from 'react-intl';
import {
  XYPlot, AreaSeries, Crosshair,
} from 'react-vis';
import dayjs from 'dayjs';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { ISO_DATE_FORMAT, DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';

import BehandlingType from 'kodeverk/behandlingType';
import NyeOgFerdigstilteOppgaverForSisteSyvGraf from './NyeOgFerdigstilteOppgaverForSisteSyvGraf';

describe('<NyeOgFerdigstilteOppgaverForSisteSyvGraf>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
  it(
    'skal vise graf med default-verdier på x og y-aksen når datagrunnlaget er tom',
    () => {
      const wrapper = shallowWithIntl(<NyeOgFerdigstilteOppgaverForSisteSyvGraf.WrappedComponent
        intl={intl as IntlShape}
        width={300}
        height={200}
        nyeOgFerdigstilteOppgaver={[]}
      />);

      const xYPlot = wrapper.find(XYPlot);
      expect(xYPlot).toHaveLength(1);
      expect(xYPlot.prop('xDomain')).toEqual(
        [dayjs().subtract(7, 'd').startOf('day').toDate(), dayjs().subtract(1, 'd').startOf('day').toDate()],
      );
      expect(xYPlot.prop('yDomain')).toEqual([0, 50]);
    },
  );

  it(
    'skal vise graf med en kurve for ferdigstilte og en for nye oppgaver',
    () => {
      const nyeOgFerdigstilteOppgaver = [{
        behandlingType: {
          kode: BehandlingType.FORSTEGANGSSOKNAD,
          navn: 'FORSTEGANGSSOKNAD',
        },
        antallNye: 11,
        antallFerdigstilte: 1,
        dato: dayjs().subtract(1, 'd').format(ISO_DATE_FORMAT),
      }, {
        behandlingType: {
          kode: BehandlingType.FORSTEGANGSSOKNAD,
          navn: 'FORSTEGANGSSOKNAD',
        },
        antallNye: 12,
        antallFerdigstilte: 2,
        dato: dayjs().subtract(2, 'd').format(ISO_DATE_FORMAT),
      }, {
        behandlingType: {
          kode: BehandlingType.FORSTEGANGSSOKNAD,
          navn: 'FORSTEGANGSSOKNAD',
        },
        antallNye: 13,
        antallFerdigstilte: 3,
        dato: dayjs().subtract(3, 'd').format(ISO_DATE_FORMAT),
      }, {
        behandlingType: {
          kode: BehandlingType.FORSTEGANGSSOKNAD,
          navn: 'FORSTEGANGSSOKNAD',
        },
        antallNye: 14,
        antallFerdigstilte: 4,
        dato: dayjs().subtract(4, 'd').format(ISO_DATE_FORMAT),
      }, {
        behandlingType: {
          kode: BehandlingType.FORSTEGANGSSOKNAD,
          navn: 'FORSTEGANGSSOKNAD',
        },
        antallNye: 15,
        antallFerdigstilte: 5,
        dato: dayjs().subtract(5, 'd').format(ISO_DATE_FORMAT),
      }, {
        behandlingType: {
          kode: BehandlingType.FORSTEGANGSSOKNAD,
          navn: 'FORSTEGANGSSOKNAD',
        },
        antallNye: 16,
        antallFerdigstilte: 6,
        dato: dayjs().subtract(6, 'd').format(ISO_DATE_FORMAT),
      }, {
        behandlingType: {
          kode: BehandlingType.FORSTEGANGSSOKNAD,
          navn: 'FORSTEGANGSSOKNAD',
        },
        antallNye: 17,
        antallFerdigstilte: 7,
        dato: dayjs().subtract(7, 'd').format(ISO_DATE_FORMAT),
      }];

      const wrapper = shallowWithIntl(<NyeOgFerdigstilteOppgaverForSisteSyvGraf.WrappedComponent
        intl={intl as IntlShape}
        width={300}
        height={200}
        nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
      />);

      const areaSeries = wrapper.find(AreaSeries);
      expect(areaSeries).toHaveLength(2);

      const ferdigstilteOppgaver = [{
        x: dayjs().subtract(7, 'd').startOf('day').toDate()
          .getTime(),
        y: 7,
      }, {
        x: dayjs().subtract(6, 'd').startOf('day').toDate()
          .getTime(),
        y: 6,
      }, {
        x: dayjs().subtract(5, 'd').startOf('day').toDate()
          .getTime(),
        y: 5,
      }, {
        x: dayjs().subtract(4, 'd').startOf('day').toDate()
          .getTime(),
        y: 4,
      }, {
        x: dayjs().subtract(3, 'd').startOf('day').toDate()
          .getTime(),
        y: 3,
      }, {
        x: dayjs().subtract(2, 'd').startOf('day').toDate()
          .getTime(),
        y: 2,
      }, {
        x: dayjs().subtract(1, 'd').startOf('day').toDate()
          .getTime(),
        y: 1,
      }];
      const dataArea1 = areaSeries.first().prop('data');
      expect(dataArea1).toEqual(ferdigstilteOppgaver);

      const nyeOppgaver = [{
        x: dayjs().subtract(7, 'd').startOf('day').toDate()
          .getTime(),
        y: 17,
      }, {
        x: dayjs().subtract(6, 'd').startOf('day').toDate()
          .getTime(),
        y: 16,
      }, {
        x: dayjs().subtract(5, 'd').startOf('day').toDate()
          .getTime(),
        y: 15,
      }, {
        x: dayjs().subtract(4, 'd').startOf('day').toDate()
          .getTime(),
        y: 14,
      }, {
        x: dayjs().subtract(3, 'd').startOf('day').toDate()
          .getTime(),
        y: 13,
      }, {
        x: dayjs().subtract(2, 'd').startOf('day').toDate()
          .getTime(),
        y: 12,
      }, {
        x: dayjs().subtract(1, 'd').startOf('day').toDate()
          .getTime(),
        y: 11,
      }];
      const dataArea2 = areaSeries.last().prop('data');
      expect(dataArea2).toEqual(nyeOppgaver);
    },
  );

  it(
    'skal vise crosshair med antall nye og ferdigstilte for valgt dato',
    () => {
      const nyeOgFerdigstilteOppgaver = [{
        behandlingType: {
          kode: BehandlingType.FORSTEGANGSSOKNAD,
          navn: 'FORSTEGANGSSOKNAD',
        },
        antallNye: 12,
        antallFerdigstilte: 2,
        dato: dayjs().subtract(1, 'd').format(ISO_DATE_FORMAT),
      }];

      const wrapper = shallowWithIntl(<NyeOgFerdigstilteOppgaverForSisteSyvGraf.WrappedComponent
        intl={intl as IntlShape}
        width={300}
        height={200}
        nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
      />);

      const areaSeries = wrapper.find(AreaSeries);
      expect(areaSeries).toHaveLength(2);
      const valgtPunkt = {
        x: dayjs().startOf('day').subtract(1, 'd').toDate()
          .getTime(),
        y: 1,
      };
      const func = areaSeries.first().prop('onNearestX') as (value: {x: number; y: number}) => void;
      func(valgtPunkt);

      const crosshair = wrapper.find(Crosshair);
      expect(crosshair).toHaveLength(1);

      expect(crosshair.find(Normaltekst).childAt(0).text()).toEqual(dayjs().subtract(1, 'd').format(DDMMYYYY_DATE_FORMAT));
      const tekst = crosshair.find(Undertekst);
      expect(tekst).toHaveLength(2);
      expect(tekst.first().childAt(0).prop('values').antall).toEqual(2);
      expect(tekst.last().childAt(0).prop('values').antall).toEqual(12);
    },
  );
});
