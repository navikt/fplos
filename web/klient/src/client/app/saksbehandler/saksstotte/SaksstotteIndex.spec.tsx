import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';

import SaksstottePaneler from './components/SaksstottePaneler';
import SaksstotteIndex from './SaksstotteIndex';

describe('<SaksstotteIndex>', () => {
  it('skal vise alle saksstøttepanel', () => {
    const wrapper = shallow(<SaksstotteIndex
      valgtSakslisteId={1}
    />);

    expect(wrapper.find(SaksstottePaneler)).to.have.length(1);
  });
});
