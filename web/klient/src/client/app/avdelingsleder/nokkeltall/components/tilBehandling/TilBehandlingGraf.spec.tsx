
import React from 'react';
import moment from 'moment';
import { expect } from 'chai';
import { AreaSeries, Crosshair } from 'react-vis';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { ISO_DATE_FORMAT, DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { TilBehandlingGraf } from './TilBehandlingGraf';

describe('<TilBehandlingGraf>', () => {
  const behandlingTyper = [{
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
  }];

  it('skal vise tom graf når en ikke har data', () => {
    const oppgaverPerDato = [];

    const wrapper = shallowWithIntl(<TilBehandlingGraf
      intl={intlMock}
      width={300}
      height={200}
      oppgaverPerDato={oppgaverPerDato}
      behandlingTyper={behandlingTyper}
      isToUkerValgt
    />);

    const areaSeries = wrapper.find(AreaSeries);
    expect(areaSeries).to.have.length(0);
  });

  const sjekkAtEnHarVerdierForPeriode = (data, isToUkerValgt) => {
    const dager = isToUkerValgt ? 14 : 28;
    expect(data).to.have.length(dager);
    for (let i = 0; i < dager; i += 1) {
      const dato = moment().startOf('day').subtract(dager - 1 - i, 'd');
      expect(dato.toDate()).is.eql(data[i].x);
    }
  };

  it('skal vise graf med en kurve for hver behandlingstype for de siste fjorten dagene', () => {
    const oppgaverPerDato = [{
      behandlingType: behandlingTyper[0],
      opprettetDato: moment().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      behandlingType: behandlingTyper[0],
      opprettetDato: moment().subtract(1, 'd').format(ISO_DATE_FORMAT),
      antall: 2,
    }, {
      behandlingType: behandlingTyper[1],
      opprettetDato: moment().format(ISO_DATE_FORMAT),
      antall: 3,
    }];

    const wrapper = shallowWithIntl(<TilBehandlingGraf
      width={300}
      height={200}
      oppgaverPerDato={oppgaverPerDato}
      behandlingTyper={behandlingTyper}
      isToUkerValgt
    />);

    const areaSeries = wrapper.find(AreaSeries);
    expect(areaSeries).to.have.length(2);

    const dataArea1 = areaSeries.first().prop('data');
    sjekkAtEnHarVerdierForPeriode(dataArea1, true);
    expect(dataArea1[0].y).to.eql(0);
    expect(dataArea1[11].y).to.eql(0);
    expect(dataArea1[12].y).to.eql(2);
    expect(dataArea1[13].y).to.eql(1);

    const dataArea2 = areaSeries.last().prop('data');
    sjekkAtEnHarVerdierForPeriode(dataArea2, true);
    expect(dataArea2[12].y).to.eql(0);
    expect(dataArea2[13].y).to.eql(3);
  });

  it('skal vise graf med en kurve for hver behandlingstype for den siste måneden', () => {
    const oppgaverPerDato = [{
      behandlingType: behandlingTyper[0],
      opprettetDato: moment().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      behandlingType: behandlingTyper[0],
      opprettetDato: moment().subtract(1, 'd').format(ISO_DATE_FORMAT),
      antall: 2,
    }, {
      behandlingType: behandlingTyper[1],
      opprettetDato: moment().format(ISO_DATE_FORMAT),
      antall: 3,
    }];

    const wrapper = shallowWithIntl(<TilBehandlingGraf
      width={300}
      height={200}
      oppgaverPerDato={oppgaverPerDato}
      behandlingTyper={behandlingTyper}
      isToUkerValgt={false}
    />);

    const areaSeries = wrapper.find(AreaSeries);
    expect(areaSeries).to.have.length(2);

    const dataArea1 = areaSeries.first().prop('data');
    sjekkAtEnHarVerdierForPeriode(dataArea1, false);
    expect(dataArea1[0].y).to.eql(0);
    expect(dataArea1[25].y).to.eql(0);
    expect(dataArea1[26].y).to.eql(2);
    expect(dataArea1[27].y).to.eql(1);

    const dataArea2 = areaSeries.last().prop('data');
    sjekkAtEnHarVerdierForPeriode(dataArea2, false);
    expect(dataArea2[26].y).to.eql(0);
    expect(dataArea2[27].y).to.eql(3);
  });

  it('skal vise crosshair med antall for de ulike behandlingspunktene for dato', () => {
    const oppgaverPerDato = [{
      behandlingType: behandlingTyper[0],
      opprettetDato: moment().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      behandlingType: behandlingTyper[0],
      opprettetDato: moment().subtract(1, 'd').format(ISO_DATE_FORMAT),
      antall: 2,
    }, {
      behandlingType: behandlingTyper[1],
      opprettetDato: moment().format(ISO_DATE_FORMAT),
      antall: 3,
    }];

    const wrapper = shallowWithIntl(<TilBehandlingGraf
      width={300}
      height={200}
      oppgaverPerDato={oppgaverPerDato}
      behandlingTyper={behandlingTyper}
      isToUkerValgt={false}
    />);

    const areaSeries = wrapper.find(AreaSeries);
    expect(areaSeries).to.have.length(2);

    const valgtPunkt = { x: moment().startOf('day').toDate(), y: 1 };
    areaSeries.first().prop('onNearestX')(valgtPunkt);

    const crosshair = wrapper.find(Crosshair);
    expect(crosshair).to.have.length(1);

    expect(crosshair.find(Normaltekst).childAt(0).text()).to.eql(moment().format(DDMMYYYY_DATE_FORMAT));
    const tekst = crosshair.find(Undertekst);
    expect(tekst).to.have.length(2);
    expect(tekst.first().childAt(0).text()).to.eql(`Klage: ${3}`);
    expect(tekst.last().childAt(0).text()).to.eql(`Førstegangssøknad: ${1}`);
  });
});
