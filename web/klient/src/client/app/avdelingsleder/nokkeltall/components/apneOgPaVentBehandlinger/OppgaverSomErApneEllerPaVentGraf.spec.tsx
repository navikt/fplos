import React from 'react';
import moment from 'moment';
import { Hint, VerticalRectSeries } from 'react-vis';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { ISO_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import behandlingVenteStatus from 'kodeverk/behandlingVenteStatus';
import OppgaverSomErApneEllerPaVentGraf from './OppgaverSomErApneEllerPaVentGraf';

const oppgaverApneEllerPaVent = [{
  behandlingVenteStatus: {
    kode: behandlingVenteStatus.PA_VENT,
    navn: 'På vent',
  },
  behandlingType: {
    kode: behandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegangssøknad',
  },
  førsteUttakMåned: moment().startOf('month').format(ISO_DATE_FORMAT),
  antall: 2,
}, {
  behandlingVenteStatus: {
    kode: behandlingVenteStatus.IKKE_PA_VENT,
    navn: 'Ikke på vent',
  },
  behandlingType: {
    kode: behandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegangssøknad',
  },
  førsteUttakMåned: moment().startOf('month').format(ISO_DATE_FORMAT),
  antall: 5,
}, {
  behandlingVenteStatus: {
    kode: behandlingVenteStatus.PA_VENT,
    navn: 'På vent',
  },
  behandlingType: {
    kode: behandlingType.KLAGE,
    navn: 'Klage',
  },
  antall: 6,
}];

describe('<OppgaverSomErApneEllerPaVentGraf>', () => {
  it('skal vise gref', () => {
    const wrapper = shallowWithIntl(<OppgaverSomErApneEllerPaVentGraf.WrappedComponent
      intl={intlMock}
      width={300}
      height={200}
      oppgaverApneEllerPaVent={oppgaverApneEllerPaVent}
    />);

    const rectSeries = wrapper.find(VerticalRectSeries);
    expect(rectSeries.first().prop('data')).toHaveLength(13);
    // @ts-ignore
    expect(rectSeries.first().prop('data')[10].y).toEqual(5);
    expect(rectSeries.last().prop('data')).toHaveLength(13);
    // @ts-ignore
    expect(rectSeries.last().prop('data')[10].y).toEqual(2);
    // @ts-ignore
    expect(rectSeries.last().prop('data')[12].y).toEqual(6);
  });

  it('skal vise hint med antall ved mouseover', () => {
    const wrapper = shallowWithIntl(<OppgaverSomErApneEllerPaVentGraf.WrappedComponent
      intl={intlMock}
      width={300}
      height={200}
      oppgaverApneEllerPaVent={oppgaverApneEllerPaVent}
    />);

    const vRectSeries = wrapper.find(VerticalRectSeries);
    expect(vRectSeries).toHaveLength(2);

    const grafPosisjon = {
      x: 2,
      x0: 1,
      y: 6,
    };
    const func = vRectSeries.first().prop('onValueMouseOver') as ({ x: number, x0, y }) => void;
    func(grafPosisjon);

    const hint = wrapper.find(Hint);
    expect(hint).toHaveLength(1);
    expect(hint.childAt(0).html()).toEqual('<div class="hint">Antall: 6</div>');
  });
});
