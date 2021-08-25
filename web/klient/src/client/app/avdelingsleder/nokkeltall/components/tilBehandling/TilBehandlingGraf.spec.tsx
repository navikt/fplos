import React from 'react';
import dayjs from 'dayjs';
import { shallow } from 'enzyme';
import { AreaSeries, Crosshair } from 'react-vis';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { ISO_DATE_FORMAT, DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import BehandlingType from 'kodeverk/behandlingType';
import TilBehandlingGraf, { OppgaveForDatoGraf } from './TilBehandlingGraf';

describe('<TilBehandlingGraf>', () => {
  const behandlingTyper = [{
    kode: BehandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegangssøknad',
  }, {
    kode: BehandlingType.KLAGE,
    navn: 'Klage',
  }, {
    kode: BehandlingType.DOKUMENTINNSYN,
    navn: 'Dokumentinnsyn',
  }, {
    kode: BehandlingType.REVURDERING,
    navn: 'Revurdering',
  }, {
    kode: BehandlingType.TILBAKEBETALING,
    navn: 'Tilbakebetaling',
  }, {
    kode: BehandlingType.TILBAKEBETALING_REVURDERING,
    navn: 'Tilbakebet-rev',
  }];

  it('skal vise tom graf når en ikke har data', () => {
    const oppgaverPerDato: OppgaveForDatoGraf[] = [];

    const wrapper = shallow(<TilBehandlingGraf
      width={300}
      height={200}
      oppgaverPerDato={oppgaverPerDato}
      behandlingTyper={behandlingTyper}
      isToUkerValgt
    />);

    const areaSeries = wrapper.find(AreaSeries);
    expect(areaSeries).toHaveLength(0);
  });

  const sjekkAtEnHarVerdierForPeriode = (data: any, isToUkerValgt: boolean) => {
    const dager = isToUkerValgt ? 14 : 28;
    expect(data).toHaveLength(dager);
    for (let i = 0; i < dager; i += 1) {
      const dato = dayjs().startOf('day').subtract(dager - 1 - i, 'd');
      expect(dato.toDate()).toEqual(data[i].x);
    }
  };

  it(
    'skal vise graf med en kurve for hver behandlingstype for de siste fjorten dagene',
    () => {
      const oppgaverPerDato = [{
        behandlingType: behandlingTyper[0],
        opprettetDato: dayjs().format(ISO_DATE_FORMAT),
        antall: 1,
      }, {
        behandlingType: behandlingTyper[0],
        opprettetDato: dayjs().subtract(1, 'd').format(ISO_DATE_FORMAT),
        antall: 2,
      }, {
        behandlingType: behandlingTyper[1],
        opprettetDato: dayjs().format(ISO_DATE_FORMAT),
        antall: 3,
      }];

      const wrapper = shallow(<TilBehandlingGraf
        width={300}
        height={200}
        oppgaverPerDato={oppgaverPerDato}
        behandlingTyper={behandlingTyper}
        isToUkerValgt
      />);

      const areaSeries = wrapper.find(AreaSeries);
      expect(areaSeries).toHaveLength(2);

      const dataArea1 = areaSeries.first().prop('data');
      sjekkAtEnHarVerdierForPeriode(dataArea1, true);
      // @ts-ignore Fiks denne
      expect(dataArea1[0].y).toEqual(0);
      // @ts-ignore Fiks denne
      expect(dataArea1[11].y).toEqual(0);
      // @ts-ignore Fiks denne
      expect(dataArea1[12].y).toEqual(2);
      // @ts-ignore Fiks denne
      expect(dataArea1[13].y).toEqual(1);

      const dataArea2 = areaSeries.last().prop('data');
      sjekkAtEnHarVerdierForPeriode(dataArea2, true);
      // @ts-ignore Fiks denne
      expect(dataArea2[12].y).toEqual(0);
      // @ts-ignore Fiks denne
      expect(dataArea2[13].y).toEqual(3);
    },
  );

  it(
    'skal vise graf med en kurve for hver behandlingstype for den siste måneden',
    () => {
      const oppgaverPerDato = [{
        behandlingType: behandlingTyper[0],
        opprettetDato: dayjs().format(ISO_DATE_FORMAT),
        antall: 1,
      }, {
        behandlingType: behandlingTyper[0],
        opprettetDato: dayjs().subtract(1, 'd').format(ISO_DATE_FORMAT),
        antall: 2,
      }, {
        behandlingType: behandlingTyper[1],
        opprettetDato: dayjs().format(ISO_DATE_FORMAT),
        antall: 3,
      }];

      const wrapper = shallow(<TilBehandlingGraf
        width={300}
        height={200}
        oppgaverPerDato={oppgaverPerDato}
        behandlingTyper={behandlingTyper}
        isToUkerValgt={false}
      />);

      const areaSeries = wrapper.find(AreaSeries);
      expect(areaSeries).toHaveLength(2);

      const dataArea1 = areaSeries.first().prop('data');
      sjekkAtEnHarVerdierForPeriode(dataArea1, false);
      // @ts-ignore Fiks denne
      expect(dataArea1[0].y).toEqual(0);
      // @ts-ignore Fiks denne
      expect(dataArea1[25].y).toEqual(0);
      // @ts-ignore Fiks denne
      expect(dataArea1[26].y).toEqual(2);
      // @ts-ignore Fiks denne
      expect(dataArea1[27].y).toEqual(1);

      const dataArea2 = areaSeries.last().prop('data');
      sjekkAtEnHarVerdierForPeriode(dataArea2, false);
      // @ts-ignore Fiks denne
      expect(dataArea2[26].y).toEqual(0);
      // @ts-ignore Fiks denne
      expect(dataArea2[27].y).toEqual(3);
    },
  );

  it(
    'skal vise crosshair med antall for de ulike behandlingspunktene for dato',
    () => {
      const oppgaverPerDato = [{
        behandlingType: behandlingTyper[0],
        opprettetDato: dayjs().format(ISO_DATE_FORMAT),
        antall: 1,
      }, {
        behandlingType: behandlingTyper[0],
        opprettetDato: dayjs().subtract(1, 'd').format(ISO_DATE_FORMAT),
        antall: 2,
      }, {
        behandlingType: behandlingTyper[1],
        opprettetDato: dayjs().format(ISO_DATE_FORMAT),
        antall: 3,
      }];

      const wrapper = shallow(<TilBehandlingGraf
        width={300}
        height={200}
        oppgaverPerDato={oppgaverPerDato}
        behandlingTyper={behandlingTyper}
        isToUkerValgt={false}
      />);

      const areaSeries = wrapper.find(AreaSeries);
      expect(areaSeries).toHaveLength(2);

      const valgtPunkt = { x: dayjs().startOf('day').toDate(), y: 1 };
      const func = areaSeries.first().prop('onNearestX') as any;
      func(valgtPunkt);

      const crosshair = wrapper.find(Crosshair);
      expect(crosshair).toHaveLength(1);

      expect(crosshair.find(Normaltekst).childAt(0).text()).toEqual(dayjs().format(DDMMYYYY_DATE_FORMAT));
      const tekst = crosshair.find(Undertekst);
      expect(tekst).toHaveLength(2);
      expect(tekst.first().childAt(0).text()).toEqual(`Klage: ${3}`);
      expect(tekst.last().childAt(0).text()).toEqual(`Førstegangssøknad: ${1}`);
    },
  );
});
