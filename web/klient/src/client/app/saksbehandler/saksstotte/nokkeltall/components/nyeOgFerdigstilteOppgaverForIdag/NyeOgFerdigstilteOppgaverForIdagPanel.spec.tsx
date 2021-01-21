import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import moment from 'moment';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import behandlingType from 'kodeverk/behandlingType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import NyeOgFerdigstilteOppgaverForIdagPanel, { getNyeOgFerdigstilteForIDag } from './NyeOgFerdigstilteOppgaverForIdagPanel';
import NyeOgFerdigstilteOppgaverForIdagGraf from './NyeOgFerdigstilteOppgaverForIdagGraf';

describe('<NyeOgFerdigstilteOppgaverForIdagPanel>', () => {
  it('skal vise rendre komponent', () => {
    const alleKodeverk = {
      [kodeverkTyper.BEHANDLING_TYPE]: [{
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'FORSTEGANGSSOKNAD',
      }],
    };

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const nyeOgFerdigstilteOppgaver = [{
      behandlingType: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
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

    expect(wrapper.find(NyeOgFerdigstilteOppgaverForIdagGraf)).to.have.length(1);
  });

  it('skal filtrere bort alle andre enn dagens oppgaver', () => {
    const iDag = moment().format();
    const nyeOgFerdigstilteOppgaver = [{
      behandlingType: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'FORSTEGANGSSOKNAD',
      },
      antallNye: 12,
      antallFerdigstilte: 2,
      dato: iDag,
    }, {
      behandlingType: {
        kode: behandlingType.KLAGE,
        navn: 'KLAGE',
      },
      antallNye: 1,
      antallFerdigstilte: 6,
      dato: moment().add(1, 'days').format(),
    }, {
      behandlingType: {
        kode: behandlingType.DOKUMENTINNSYN,
        navn: 'INNSYN',
      },
      antallNye: 8,
      antallFerdigstilte: 9,
      dato: moment().subtract(1, 'days').format(),
    }];

    const filtrerteOppgaver = getNyeOgFerdigstilteForIDag(nyeOgFerdigstilteOppgaver);

    expect(filtrerteOppgaver).to.have.length(1);
    expect(filtrerteOppgaver[0].dato).is.eql(iDag);
  });
});
