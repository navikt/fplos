import sinon from 'sinon';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import React from 'react';

import { ReservasjonerIndex } from './ReservasjonerIndex';
import ReservasjonerPanel from './components/ReservasjonerPanel';

describe('<ReservasjonerIndex>', () => {
  it('skal hente reservasjoner ved lasting av komponent og så vise dem i panel', () => {
    const fetchAvdelingensReservasjoner = sinon.spy();
    const wrapper = shallow(<ReservasjonerIndex
      fetchAvdelingensReservasjoner={fetchAvdelingensReservasjoner}
      avdelingensReservasjoner={[]}
      opphevReservasjon={sinon.spy()}
      endreOppgaveReservasjon={sinon.spy()}
      valgtAvdelingEnhet="2"
    />);

    expect(wrapper.find(ReservasjonerPanel)).to.have.length(1);
    expect(fetchAvdelingensReservasjoner.calledOnce).to.be.true;
    const { args } = fetchAvdelingensReservasjoner.getCalls()[0];
    expect(args).to.have.length(1);
    expect(args[0]).to.eql('2');
  });
});
