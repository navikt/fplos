import { shallow } from 'enzyme';
import sinon from 'sinon';
import React from 'react';
import { expect } from 'chai';

import ReservasjonerPanel from 'avdelingsleder/reservasjoner/components/ReservasjonerPanel';
import ReservasjonerTabell from 'avdelingsleder/reservasjoner/components/ReservasjonerTabell';

describe('<ReservasjonerPanel>', () => {
  it('skal vise tabell for reservasjoner', () => {
    const wrapper = shallow(<ReservasjonerPanel
      reservasjoner={[]}
      opphevReservasjon={sinon.spy()}
    />);
    expect(wrapper.find(ReservasjonerTabell)).to.have.length(1);
  });
});
