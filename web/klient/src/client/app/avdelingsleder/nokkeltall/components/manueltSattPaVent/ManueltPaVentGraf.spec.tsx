import React from 'react';
import moment from 'moment';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import { AreaSeries, Crosshair, XYPlot } from 'react-vis';
import { FormattedMessage } from 'react-intl';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { DDMMYYYY_DATE_FORMAT, ISO_DATE_FORMAT } from 'utils/formats';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import ManueltPaVentGraf from './ManueltPaVentGraf';

describe('<ManueltPaVentGraf>', () => {
  it('skal vise graf med 50 satt på y-linja når graf er tom', () => {
    const wrapper = shallow(<ManueltPaVentGraf
      width={300}
      height={200}
      oppgaverManueltPaVent={[]}
      isFireUkerValgt
    />);

    const xYPlot = wrapper.find(XYPlot);
    expect(xYPlot).to.have.length(1);
    expect(xYPlot.prop('yDomain')).to.eql([0, 50]);
  });

  it('skal vise crosshair med antall behandlinger for i morgen', () => {
    const oppgaverManeultPaVent = [{
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'Foreldrepenger',
      },
      behandlingFrist: moment().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'Foreldrepenger',
      },
      behandlingFrist: moment().add(1, 'd').format(ISO_DATE_FORMAT),
      antall: 2,
    }, {
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'Foreldrepenger',
      },
      behandlingFrist: moment().add(2, 'd').format(ISO_DATE_FORMAT),
      antall: 3,
    }];

    const wrapper = shallow(<ManueltPaVentGraf
      width={300}
      height={200}
      oppgaverManueltPaVent={oppgaverManeultPaVent}
      isFireUkerValgt
    />);

    const areaSeries = wrapper.find(AreaSeries);
    expect(areaSeries).to.have.length(1);

    const func = areaSeries.first().prop('onNearestX') as ({ x: Date, y: number }) => void;
    func({
      x: moment().add(1, 'd').toDate(),
      y: 2,
    });

    const crosshair = wrapper.find(Crosshair);
    expect(crosshair).to.have.length(1);

    expect(crosshair.find(Normaltekst).childAt(0).text()).to.eql(moment().add(1, 'd').format(DDMMYYYY_DATE_FORMAT));
    const tekst = crosshair.find(Undertekst);
    expect(tekst).to.have.length(1);
    expect(tekst.first().find(FormattedMessage).prop('values')).to.eql({ antall: 2 });
  });
});
