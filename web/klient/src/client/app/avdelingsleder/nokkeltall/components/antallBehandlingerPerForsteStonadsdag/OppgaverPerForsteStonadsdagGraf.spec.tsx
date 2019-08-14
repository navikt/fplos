import React from 'react';
import moment from 'moment';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import { AreaSeries, Crosshair, XYPlot } from 'react-vis';
import { FormattedMessage } from 'react-intl';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import {
  OppgaverPerForsteStonadsdagGraf, lagKoordinater, harDatastrukturKun0Verdier, lagDatastruktur,
} from './OppgaverPerForsteStonadsdagGraf';

describe('<OppgaverPerForsteStonadsdagGraf>', () => {
  it('skal vise graf med 50 satt på y-linja når graf er tom', () => {
    const wrapper = shallow(<OppgaverPerForsteStonadsdagGraf
      width={300}
      height={200}
      data={[]}
      isEmpty
    />);

    const xYPlot = wrapper.find(XYPlot);
    expect(xYPlot).to.have.length(1);
    expect(xYPlot.prop('yDomain')).to.eql([0, 50]);
  });

  it('skal vise crosshair med antall behandlinger per stønadsdag', () => {
    const data = [{
      x: moment().toDate(),
      y: 1,
    }, {
      x: moment().add(1, 'd').toDate(),
      y: 2,
    }, {
      x: moment().add(2, 'd').toDate(),
      y: 3,
    }];

    const wrapper = shallow(<OppgaverPerForsteStonadsdagGraf
      width={300}
      height={200}
      data={data}
      isEmpty={false}
    />);

    const areaSeries = wrapper.find(AreaSeries);
    expect(areaSeries).to.have.length(1);

    areaSeries.first().prop('onNearestX')(data[1]);

    const crosshair = wrapper.find(Crosshair);
    expect(crosshair).to.have.length(2);

    expect(crosshair.last().find(Normaltekst).childAt(0).text()).to.eql(moment(data[1].x).format(DDMMYYYY_DATE_FORMAT));
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

    const props = {
      oppgaverPerForsteStonadsdag,
    };
    const koordinater = lagKoordinater.resultFunc(props);

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

    const sorterteKoordinater = lagDatastruktur.resultFunc(koordinater);

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

    const harKun0Verdier = harDatastrukturKun0Verdier.resultFunc(koordinater);

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

    const harKun0Verdier = harDatastrukturKun0Verdier.resultFunc(koordinater);

    expect(harKun0Verdier).is.false;
  });
});
