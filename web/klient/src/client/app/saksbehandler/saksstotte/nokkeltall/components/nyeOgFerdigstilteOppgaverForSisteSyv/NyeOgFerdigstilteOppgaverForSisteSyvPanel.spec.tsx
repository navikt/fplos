import React from 'react';
import { shallow } from 'enzyme';
import moment from 'moment';

import BehandlingType from 'kodeverk/behandlingType';
import NyeOgFerdigstilteOppgaverForSisteSyvPanel from './NyeOgFerdigstilteOppgaverForSisteSyvPanel';
import NyeOgFerdigstilteOppgaverForSisteSyvGraf from './NyeOgFerdigstilteOppgaverForSisteSyvGraf';

describe('<NyeOgFerdigstilteOppgaverForSisteSyvPanel>', () => {
  it('skal vise rendre komponent', () => {
    const nyeOgFerdigstilteOppgaver = [{
      behandlingType: {
        kode: BehandlingType.FORSTEGANGSSOKNAD,
        navn: 'FORSTEGANGSSOKNAD',
      },
      antallNye: 12,
      antallFerdigstilte: 2,
      dato: '2019-01-01',
    }];

    const wrapper = shallow(<NyeOgFerdigstilteOppgaverForSisteSyvPanel
      width={300}
      height={200}
      nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
    />);

    expect(wrapper.find(NyeOgFerdigstilteOppgaverForSisteSyvGraf)).toHaveLength(1);
  });

  it('skal filtrere bort dagens oppgaver', () => {
    const iDag = moment().format();
    const iGar = moment().subtract(1, 'days').format();
    const atteDagerSiden = moment().subtract(8, 'days').format();
    const nyeOgFerdigstilteOppgaver = [{
      behandlingType: {
        kode: BehandlingType.FORSTEGANGSSOKNAD,
        navn: 'FORSTEGANGSSOKNAD',
      },
      antallNye: 12,
      antallFerdigstilte: 2,
      dato: iDag,
    }, {
      behandlingType: {
        kode: BehandlingType.KLAGE,
        navn: 'KLAGE',
      },
      antallNye: 1,
      antallFerdigstilte: 6,
      dato: iGar,
    }, {
      behandlingType: {
        kode: BehandlingType.DOKUMENTINNSYN,
        navn: 'INNSYN',
      },
      antallNye: 8,
      antallFerdigstilte: 9,
      dato: atteDagerSiden,
    }];

    const wrapper = shallow(<NyeOgFerdigstilteOppgaverForSisteSyvPanel
      width={300}
      height={200}
      nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
    />);

    const graf = wrapper.find(NyeOgFerdigstilteOppgaverForSisteSyvGraf);
    expect(graf).toHaveLength(1);
    const oppgaver = graf.props().nyeOgFerdigstilteOppgaver;

    expect(oppgaver).toHaveLength(2);
    expect(oppgaver[0].dato).toEqual(iGar);
    expect(oppgaver[1].dato).toEqual(atteDagerSiden);
  });
});
