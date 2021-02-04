import React from 'react';
import { shallow } from 'enzyme';

import NokkeltallPanel from './NokkeltallPanel';
import FordelingAvBehandlingstypePanel from './fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';
import TilBehandlingPanel from './tilBehandling/TilBehandlingPanel';

describe('<NokkeltallPanel>', () => {
  it('skal vise grafpaneler', () => {
    const wrapper = shallow(<NokkeltallPanel
      oppgaverForAvdeling={[]}
      oppgaverPerDato={[]}
      oppgaverManueltPaVent={[]}
      oppgaverPerForsteStonadsdag={[]}
      oppgaverApneEllerPaVent={[]}
    />);

    expect(wrapper.find(FordelingAvBehandlingstypePanel)).toHaveLength(1);
    expect(wrapper.find(TilBehandlingPanel)).toHaveLength(1);
  });
});
