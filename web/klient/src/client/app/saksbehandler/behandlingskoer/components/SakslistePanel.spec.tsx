
import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import sinon from 'sinon';

import SakslisteVelgerForm from './SakslisteVelgerForm';
import OppgaverTabell from './OppgaverTabell';

import SakslistePanel from './SakslistePanel';

describe('<SakslistePanel>', () => {
  it('skal vise kriterievelger og liste over neste saker', () => {
    const fetchFn = sinon.spy();
    const sakslister = [];
    const wrapper = shallow(<SakslistePanel
      fetchSakslisteOppgaver={fetchFn}
      sakslister={sakslister}
      reserverOppgave={sinon.spy()}
      opphevOppgaveReservasjon={sinon.spy()}
      forlengOppgaveReservasjon={sinon.spy()}
      endreOppgaveReservasjon={sinon.spy()}
      flyttReservasjon={sinon.spy()}
    />);

    expect(wrapper.find(SakslisteVelgerForm)).to.have.length(1);
    expect(wrapper.find(OppgaverTabell)).to.have.length(1);
  });
});
