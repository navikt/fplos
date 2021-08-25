import React from 'react';
import { shallow } from 'enzyme';
import dayjs from 'dayjs';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import BehandlingType from 'kodeverk/behandlingType';
import KodeverkType from 'kodeverk/kodeverkTyper';
import NyeOgFerdigstilteOppgaverForIdagPanel, { getNyeOgFerdigstilteForIDag } from './NyeOgFerdigstilteOppgaverForIdagPanel';
import NyeOgFerdigstilteOppgaverForIdagGraf from './NyeOgFerdigstilteOppgaverForIdagGraf';

describe('<NyeOgFerdigstilteOppgaverForIdagPanel>', () => {
  it('skal vise rendre komponent', () => {
    const alleKodeverk = {
      [KodeverkType.BEHANDLING_TYPE]: [{
        kode: BehandlingType.FORSTEGANGSSOKNAD,
        navn: 'FORSTEGANGSSOKNAD',
      }],
    };

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const nyeOgFerdigstilteOppgaver = [{
      behandlingType: {
        kode: BehandlingType.FORSTEGANGSSOKNAD,
        navn: 'FORSTEGANGSSOKNAD',
      },
      antallNye: 12,
      antallFerdigstilte: 2,
      dato: '2019-01-01',
    }];

    const wrapper = shallow(<NyeOgFerdigstilteOppgaverForIdagPanel
      width={300}
      height={200}
      nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
    />);

    expect(wrapper.find(NyeOgFerdigstilteOppgaverForIdagGraf)).toHaveLength(1);
  });

  it('skal filtrere bort alle andre enn dagens oppgaver', () => {
    const iDag = dayjs().format();
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
      dato: dayjs().add(1, 'days').format(),
    }, {
      behandlingType: {
        kode: BehandlingType.DOKUMENTINNSYN,
        navn: 'INNSYN',
      },
      antallNye: 8,
      antallFerdigstilte: 9,
      dato: dayjs().subtract(1, 'days').format(),
    }];

    const filtrerteOppgaver = getNyeOgFerdigstilteForIDag(nyeOgFerdigstilteOppgaver);

    expect(filtrerteOppgaver).toHaveLength(1);
    expect(filtrerteOppgaver[0].dato).toEqual(iDag);
  });
});
