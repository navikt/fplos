import React from 'react';
import moment from 'moment';
import { XYPlot, Hint, HorizontalRectSeries } from 'react-vis';
import { IntlShape } from 'react-intl';

import { ISO_DATE_FORMAT } from 'utils/formats';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';

import behandlingType from 'kodeverk/behandlingType';
import NyeOgFerdigstilteOppgaverForIdagGraf from './NyeOgFerdigstilteOppgaverForIdagGraf';

describe('<NyeOgFerdigstilteOppgaverForIdagGraf>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
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
  }, {
    kode: behandlingType.TILBAKEBETALING,
    navn: 'Tilbakebetaling',
  }, {
    kode: behandlingType.TILBAKEBETALING_REVURDERING,
    navn: 'Tilbakebet-rev',
  }];

  it('skal vise graf med 10 satt på x-linja når graf er tom', () => {
    const wrapper = shallowWithIntl(<NyeOgFerdigstilteOppgaverForIdagGraf.WrappedComponent
      intl={intl as IntlShape}
      width={300}
      height={200}
      behandlingTyper={behandlingTyper}
      nyeOgFerdigstilteOppgaver={[]}
    />);

    const xYPlot = wrapper.find(XYPlot);
    expect(xYPlot).toHaveLength(1);
    expect(xYPlot.prop('xDomain')).toEqual([0, 10]);
  });

  it('skal vise graf med 7 satt på x-linja når data har maksverdi x=5', () => {
    const nyeOgFerdigstilteOppgaver = [{
      behandlingType: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'FORSTEGANGSSOKNAD',
      },
      antallNye: 12,
      antallFerdigstilte: 2,
      dato: moment().format(ISO_DATE_FORMAT),
    }];

    const wrapper = shallowWithIntl(<NyeOgFerdigstilteOppgaverForIdagGraf.WrappedComponent
      intl={intl as IntlShape}
      width={300}
      height={200}
      behandlingTyper={behandlingTyper}
      nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
    />);

    const xYPlot = wrapper.find(XYPlot);
    expect(xYPlot).toHaveLength(1);
    expect(xYPlot.prop('xDomain')).toEqual([0, 14]);
  });

  it(
    'skal vise hint som viser at det er fem ferdigstilte behandlinger',
    () => {
      const nyeOgFerdigstilteOppgaver = [{
        behandlingType: {
          kode: behandlingType.FORSTEGANGSSOKNAD,
          navn: 'FORSTEGANGSSOKNAD',
        },
        antallNye: 12,
        antallFerdigstilte: 2,
        dato: moment().format(ISO_DATE_FORMAT),
      }];

      const wrapper = shallowWithIntl(<NyeOgFerdigstilteOppgaverForIdagGraf.WrappedComponent
        intl={intl as IntlShape}
        width={300}
        height={200}
        behandlingTyper={behandlingTyper}
        nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
      />);

      const func = wrapper.find(HorizontalRectSeries).first().prop('onValueMouseOver') as (koordinat: {x: number; y: number }) => void;
      func({ x: 2, y: 4.5 });

      const hint = wrapper.find(Hint);
      expect(hint).toHaveLength(1);
      expect(hint.find('div').childAt(0).text()).toEqual('Antall ferdigstilte: 2');
    },
  );
});
