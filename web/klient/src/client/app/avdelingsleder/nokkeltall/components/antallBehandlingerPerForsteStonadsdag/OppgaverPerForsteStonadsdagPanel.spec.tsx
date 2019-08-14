
import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';

import { OppgaverPerForsteStonadsdagPanel } from './OppgaverPerForsteStonadsdagPanel';
import OppgaverPerForsteStonadsdagGraf from './OppgaverPerForsteStonadsdagGraf';

describe('<OppgaverPerForsteStonadsdagPanel>', () => {
  it('skal rendre panel', () => {
    const oppgaverPerForsteStonadsdag = [{
      forsteStonadsdag: '2019-01-01',
      antall: 1,
    }];

    const wrapper = shallow(<OppgaverPerForsteStonadsdagPanel
      width={300}
      height={200}
      oppgaverPerForsteStonadsdag={oppgaverPerForsteStonadsdag}
    />);

    expect(wrapper.find(OppgaverPerForsteStonadsdagGraf)).to.have.length(1);
  });
});
