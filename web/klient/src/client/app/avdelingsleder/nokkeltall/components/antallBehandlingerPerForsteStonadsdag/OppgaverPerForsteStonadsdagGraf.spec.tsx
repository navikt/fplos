import React from 'react';
import moment from 'moment';
import { shallow } from 'enzyme';
import { AreaSeries, Crosshair, XYPlot } from 'react-vis';
import { FormattedMessage } from 'react-intl';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import OppgaverPerForsteStonadsdagGraf, {
  lagKoordinater, harDatastrukturKun0Verdier, lagDatastruktur,
} from './OppgaverPerForsteStonadsdagGraf';

describe('<OppgaverPerForsteStonadsdagGraf>', () => {
  it('skal vise graf med 50 satt på y-linja når graf er tom', () => {
    const wrapper = shallow(<OppgaverPerForsteStonadsdagGraf
      width={300}
      height={200}
      oppgaverPerForsteStonadsdag={[]}
    />);

    const xYPlot = wrapper.find(XYPlot);
    expect(xYPlot).toHaveLength(1);
    expect(xYPlot.prop('yDomain')).toEqual([0, 50]);
  });

  it('skal vise crosshair med antall behandlinger per stønadsdag', () => {
    const oppgaverPerForsteStonadsdag = [{
      forsteStonadsdag: moment().format(),
      antall: 1,
    }, {
      forsteStonadsdag: moment().add(1, 'd').format(),
      antall: 2,
    }, {
      forsteStonadsdag: moment().add(2, 'd').format(),
      antall: 3,
    }];

    const wrapper = shallow(<OppgaverPerForsteStonadsdagGraf
      width={300}
      height={200}
      oppgaverPerForsteStonadsdag={oppgaverPerForsteStonadsdag}
    />);

    const areaSeries = wrapper.find(AreaSeries);
    expect(areaSeries).toHaveLength(1);

    const koordinat = {
      x: moment().add(1, 'd').toDate(),
      y: 2,
    };

    const func = areaSeries.first().prop('onNearestX') as ({ x: Date, y: number }) => void;
    func(koordinat);

    const crosshair = wrapper.find(Crosshair);
    expect(crosshair).toHaveLength(2);

    expect(crosshair.last().find(Normaltekst).childAt(0).text()).toEqual(moment(koordinat.x).format(DDMMYYYY_DATE_FORMAT));
    const tekst = crosshair.find(Undertekst);
    expect(tekst).toHaveLength(1);
    expect(tekst.first().find(FormattedMessage).prop('values')).toEqual({ antall: 2 });
  });

  it('skal lage koordinater til graf gitt oppgavestruktur', () => {
    const oppgaverPerForsteStonadsdag = [{
      forsteStonadsdag: '2018-12-31',
      antall: 1,
    }, {
      forsteStonadsdag: '2018-12-30',
      antall: 3,
    }, {
      forsteStonadsdag: '2018-12-29',
      antall: 2,
    }];

    const koordinater = lagKoordinater(oppgaverPerForsteStonadsdag);

    expect(koordinater).toEqual([{
      x: moment('2018-12-31').toDate()
        .getTime(),
      y: 1,
    }, {
      x: moment('2018-12-30').toDate()
        .getTime(),
      y: 3,
    }, {
      x: moment('2018-12-29').toDate()
        .getTime(),
      y: 2,
    },
    ]);
  });

  it('skal sortere og fylle inn manglende datoer i koordinatstruktur', () => {
    const koordinater = [{
      x: moment().subtract(10, 'd').startOf('day').toDate()
        .getTime(),
      y: 3,
    }, {
      x: moment().add(10, 'd').startOf('day').toDate()
        .getTime(),
      y: 2,
    }, {
      x: moment().add(1, 'd').startOf('day').toDate()
        .getTime(),
      y: 1,
    },
    ];

    const sorterteKoordinater = lagDatastruktur(koordinater);

    expect(sorterteKoordinater).toHaveLength(21);
    expect(sorterteKoordinater[0]).toEqual({
      x: moment().subtract(10, 'd').startOf('day').toDate()
        .getTime(),
      y: 3,
    });
    expect(sorterteKoordinater[1]).toEqual({
      x: moment().subtract(9, 'd').startOf('day').toDate()
        .getTime(),
      y: 0,
    });
    expect(sorterteKoordinater[11]).toEqual({
      x: moment().add(1, 'd').startOf('day').toDate()
        .getTime(),
      y: 1,
    });
    expect(sorterteKoordinater[19]).toEqual({
      x: moment().add(9, 'd').startOf('day').toDate()
        .getTime(),
      y: 0,
    });
    expect(sorterteKoordinater[20]).toEqual({
      x: moment().add(10, 'd').startOf('day').toDate()
        .getTime(),
      y: 2,
    });
  });

  it('skal finne ut at alle koordinater har antall 0', () => {
    const koordinater = [{
      x: moment('2018-12-31').toDate().getTime(),
      y: 0,
    }, {
      x: moment('2018-12-30').toDate().getTime(),
      y: 0,
    }, {
      x: moment('2018-12-29').toDate().getTime(),
      y: 0,
    },
    ];

    const harKun0Verdier = harDatastrukturKun0Verdier(koordinater);

    expect(harKun0Verdier).toBe(true);
  });

  it('skal finne ut at ikke alle koordinater har antall 0', () => {
    const koordinater = [{
      x: moment('2018-12-31').toDate().getTime(),
      y: 3,
    }, {
      x: moment('2018-12-30').toDate().getTime(),
      y: 0,
    }, {
      x: moment('2018-12-29').toDate().getTime(),
      y: 0,
    },
    ];

    const harKun0Verdier = harDatastrukturKun0Verdier(koordinater);

    expect(harKun0Verdier).toBe(false);
  });
});
