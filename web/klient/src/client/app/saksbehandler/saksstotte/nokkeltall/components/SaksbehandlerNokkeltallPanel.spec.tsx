import React from 'react';
import { shallow } from 'enzyme';

import SaksbehandlerNokkeltallPanel from './SaksbehandlerNokkeltallPanel';
import NyeOgFerdigstilteOppgaverForIdagPanel from './nyeOgFerdigstilteOppgaverForIdag/NyeOgFerdigstilteOppgaverForIdagPanel';
import NyeOgFerdigstilteOppgaverForSisteSyvPanel from './nyeOgFerdigstilteOppgaverForSisteSyv/NyeOgFerdigstilteOppgaverForSisteSyvPanel';

describe('<SaksbehandlerNokkeltallPanel>', () => {
  it('skal vise grafpaneler', () => {
    const wrapper = shallow(<SaksbehandlerNokkeltallPanel
      nyeOgFerdigstilteOppgaver={[]}
    />);

    expect(wrapper.find(NyeOgFerdigstilteOppgaverForIdagPanel)).toHaveLength(1);
    expect(wrapper.find(NyeOgFerdigstilteOppgaverForSisteSyvPanel)).toHaveLength(1);
  });
});
