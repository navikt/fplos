import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import NokkeltallPanel from './NokkeltallPanel';
import FordelingAvBehandlingstypePanel from './fordelingAvBehandlingstype/FordelingAvBehandlingstypePanel';
import TilBehandlingPanel from './tilBehandling/TilBehandlingPanel';

describe('<NokkeltallPanel>', () => {
  it('skal vise grafpaneler', () => {
    const wrapper = shallow(<NokkeltallPanel />);

    expect(wrapper.find(FordelingAvBehandlingstypePanel)).to.have.length(1);
    expect(wrapper.find(TilBehandlingPanel)).to.have.length(1);
  });
});
