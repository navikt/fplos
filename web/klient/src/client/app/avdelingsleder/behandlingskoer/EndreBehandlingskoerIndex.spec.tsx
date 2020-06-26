import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import EndreSakslisterPanel from './components/EndreSakslisterPanel';
import EndreBehandlingskoerIndex from './EndreBehandlingskoerIndex';

describe('<EndreBehandlingskoerIndex>', () => {
  it('skal hente sakslister når sakliste-fanen blir valgt', () => {
    const wrapper = shallow(<EndreBehandlingskoerIndex
      valgtAvdelingEnhet="1"
      avdelingensSaksbehandlere={[]}
    />);

    expect(wrapper.find(EndreSakslisterPanel)).to.have.length(1);
  });
});
