import React from 'react';
import { shallow } from 'enzyme';

import SistBehandledeSaker from './SistBehandledeSaker';
import SaksstottePaneler from './SaksstottePaneler';
import SaksbehandlerNokkeltallIndex from '../nokkeltall/SaksbehandlerNokkeltallIndex';

describe('<SaksstottePaneler>', () => {
  it('skal vise sist behandlede saker', () => {
    const wrapper = shallow(<SaksstottePaneler />);

    expect(wrapper.find(SistBehandledeSaker)).toHaveLength(1);
  });

  it('skal ikke vise nøkkeldata når det ikke er valgt saksliste', () => {
    const wrapper = shallow(<SaksstottePaneler />);

    expect(wrapper.find(SaksbehandlerNokkeltallIndex)).toHaveLength(0);
  });

  it('skal vise nøkkeldata når det er valgt saksliste', () => {
    const wrapper = shallow(<SaksstottePaneler
      valgtSakslisteId={1}
    />);

    expect(wrapper.find(SaksbehandlerNokkeltallIndex)).toHaveLength(1);
  });
});
