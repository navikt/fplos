import React from 'react';
import moment from 'moment';
import { expect } from 'chai';
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
    expect(xYPlot).to.have.length(1);
    expect(xYPlot.prop('yDomain')).to.eql([0, 50]);
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
    expect(areaSeries).to.have.length(1);

    const koordinat = {
      x: moment().add(1, 'd').toDate(),
      y: 2,
    };

    const func = areaSeries.first().prop('onNearestX') as ({ x: Date, y: number }) => void;
    func(koordinat);

    const crosshair = wrapper.find(Crosshair);
    expect(crosshair).to.have.length(2);

    expect(crosshair.last().find(Normaltekst).childAt(0).text()).to.eql(moment(koordinat.x).format(DDMMYYYY_DATE_FORMAT));
    const tekst = crosshair.find(Undertekst);
    expect(tekst).to.have.length(1);
    expect(tekst.first().find(FormattedMessage).prop('values')).to.eql({ antall: 2 });
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

    expect(koordinater).to.eql([{
      x: moment('2018-12-31').toDate(),
      y: 1,
    }, {
      x: moment('2018-12-30').toDate(),
      y: 3,
    }, {
      x: moment('2018-12-29').toDate(),
      y: 2,
    },
    ]);
  });

  it('skal sortere og fylle inn manglende datoer i koordinatstruktur', () => {
    const koordinater = [{
      x: moment().subtract(10, 'd').startOf('day').toDate(),
      y: 3,
    }, {
      x: moment().add(10, 'd').startOf('day').toDate(),
      y: 2,
    }, {
      x: moment().add(1, 'd').startOf('day').toDate(),
      y: 1,
    },
    ];

    const sorterteKoordinater = lagDatastruktur(koordinater);

    expect(sorterteKoordinater).has.length(21);
    expect(sorterteKoordinater[0]).is.eql({
      x: moment().subtract(10, 'd').startOf('day').toDate(),
      y: 3,
    });
    expect(sorterteKoordinater[1]).is.eql({
      x: moment().subtract(9, 'd').startOf('day').toDate(),
      y: 0,
    });
    expect(sorterteKoordinater[11]).is.eql({
      x: moment().add(1, 'd').startOf('day').toDate(),
      y: 1,
    });
    expect(sorterteKoordinater[19]).is.eql({
      x: moment().add(9, 'd').startOf('day').toDate(),
      y: 0,
    });
    expect(sorterteKoordinater[20]).is.eql({
      x: moment().add(10, 'd').startOf('day').toDate(),
      y: 2,
    });
  });

  it('skal finne ut at alle koordinater har antall 0', () => {
    const koordinater = [{
      x: moment('2018-12-31').toDate(),
      y: 0,
    }, {
      x: moment('2018-12-30').toDate(),
      y: 0,
    }, {
      x: moment('2018-12-29').toDate(),
      y: 0,
    },
    ];

    const harKun0Verdier = harDatastrukturKun0Verdier(koordinater);

    expect(harKun0Verdier).is.true;
  });

  it('skal finne ut at ikke alle koordinater har antall 0', () => {
    const koordinater = [{
      x: moment('2018-12-31').toDate(),
      y: 3,
    }, {
      x: moment('2018-12-30').toDate(),
      y: 0,
    }, {
      x: moment('2018-12-29').toDate(),
      y: 0,
    },
    ];

    const harKun0Verdier = harDatastrukturKun0Verdier(koordinater);

    expect(harKun0Verdier).is.false;
  });
});
