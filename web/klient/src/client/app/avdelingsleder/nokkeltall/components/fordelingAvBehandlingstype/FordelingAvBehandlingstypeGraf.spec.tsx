import React from 'react';
import { expect } from 'chai';
import {
  XYPlot, YAxis, HorizontalRectSeries, Hint,
} from 'react-vis';

import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { FordelingAvBehandlingstypeGraf } from './FordelingAvBehandlingstypeGraf';

describe('<FordelingAvBehandlingstypeGraf>', () => {
  const fagsakYtelseTyper = [{
    kode: fagsakYtelseType.ENGANGSSTONAD,
    navn: 'Engangsstønad',
  }, {
    kode: fagsakYtelseType.FORELDREPRENGER,
    navn: 'Foreldrepenger',
  }, {
    kode: fagsakYtelseType.SVANGERSKAPPENGER,
    navn: 'Svangerskapspenger',
  }];
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

  it('skal vise graf', () => {
    const oppgaverForAvdeling = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: behandlingTyper[0],
      tilBehandling: true,
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypeGraf
      intl={intlMock}
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      behandlingTyper={behandlingTyper}
    />);

    const plot = wrapper.find(XYPlot);
    expect(plot).to.have.length(1);
  });

  it('skal vise behandlingstyper på y-aksen', () => {
    const oppgaverForAvdeling = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: behandlingTyper[0],
      tilBehandling: true,
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypeGraf
      intl={intlMock}
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      behandlingTyper={behandlingTyper}
    />);

    const yAksen = wrapper.find(YAxis);
    expect(yAksen).to.have.length(1);

    const verdiIndekser = yAksen.prop('tickValues');
    expect(verdiIndekser).to.have.length(4);

    const verdiFn = yAksen.prop('tickFormat');

    const behandlingstyper = verdiIndekser.map(i => verdiFn(undefined, i - 1));
    expect(behandlingstyper).is.eql(['Dokumentinnsyn', 'Klage', 'Revurdering', 'Førstegangssøknad']);
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

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypeGraf
      intl={intlMock}
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      behandlingTyper={behandlingTyper}
    />);

    const hRectSeries = wrapper.find(HorizontalRectSeries);
    expect(hRectSeries).to.have.length(2);

    const grafPosisjon = {
      x: 2,
      x0: 1,
      y: 4,
    };
    hRectSeries.first().prop('onValueMouseOver')(grafPosisjon);

    const hint = wrapper.find(Hint);
    expect(hint).to.have.length(1);
    expect(hint.childAt(0).html()).to.include('Antall: 1');
    expect(hint.childAt(0).html()).to.include('Totalt antall: 2');
  });
});
