import React from 'react';
import { IntlShape } from 'react-intl';
import {
  XYPlot, YAxis, HorizontalRectSeries, Hint,
} from 'react-vis';

import BehandlingType from 'kodeverk/behandlingType';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import FordelingAvBehandlingstypeGraf from './FordelingAvBehandlingstypeGraf';

describe('<FordelingAvBehandlingstypeGraf>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  const fagsakYtelseTyper = [{
    kode: FagsakYtelseType.ENGANGSSTONAD,
    navn: 'Engangsstønad',
  }, {
    kode: FagsakYtelseType.FORELDREPRENGER,
    navn: 'Foreldrepenger',
  }, {
    kode: FagsakYtelseType.SVANGERSKAPPENGER,
    navn: 'Svangerskapspenger',
  }];
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

  it('skal vise graf', () => {
    const oppgaverForAvdeling = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: behandlingTyper[0],
      tilBehandling: true,
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypeGraf.WrappedComponent
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      behandlingTyper={behandlingTyper}
    />);

    const plot = wrapper.find(XYPlot);
    expect(plot).toHaveLength(1);
  });

  it('skal vise behandlingstyper på y-aksen', () => {
    const oppgaverForAvdeling = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: behandlingTyper[0],
      tilBehandling: true,
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypeGraf.WrappedComponent
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      behandlingTyper={behandlingTyper}
    />);

    const yAksen = wrapper.find(YAxis);
    expect(yAksen).toHaveLength(1);

    const verdiIndekser = yAksen.prop('tickValues') as number[];
    expect(verdiIndekser).toHaveLength(6);

    const verdiFn = yAksen.prop('tickFormat') as any;

    const behandlingstyper = verdiIndekser.map((i) => verdiFn(undefined, i - 1));
    expect(behandlingstyper).toEqual(
      ['Tilbakebet-rev', 'Tilbakebetaling', 'Dokumentinnsyn', 'Klage', 'Revurdering', 'Førstegangssøknad'],
    );
  });

  it('skal vise hint med antall og total-antall ved mouseover', () => {
    const oppgaverForAvdeling = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: behandlingTyper[0],
      tilBehandling: true,
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: behandlingTyper[0],
      tilBehandling: false,
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypeGraf.WrappedComponent
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      behandlingTyper={behandlingTyper}
    />);

    const hRectSeries = wrapper.find(HorizontalRectSeries);
    expect(hRectSeries).toHaveLength(2);

    const grafPosisjon = {
      x: 2,
      x0: 1,
      y: 6,
    };
    const func = hRectSeries.first().prop('onValueMouseOver') as any;
    func(grafPosisjon);

    const hint = wrapper.find(Hint);
    expect(hint).toHaveLength(1);
    expect(hint.childAt(0).html()).toEqual('<div class="hint">Antall: 1<br/>Totalt antall: 2</div>');
  });
});
