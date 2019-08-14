
import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import sinon from 'sinon';

import { EndreSaksbehandlereIndex } from './EndreSaksbehandlereIndex';
import SaksbehandlerePanel from './components/SaksbehandlerePanel';

describe('<EndreSaksbehandlereIndex>', () => {
  it('skal hente saksbehandlere ved lasting av komponent og så vise desse i panel', () => {
    const fetchAvdelingensSaksbehandlere = sinon.spy();
    const wrapper = shallow(<EndreSaksbehandlereIndex
      fetchAvdelingensSaksbehandlere={fetchAvdelingensSaksbehandlere}
      findSaksbehandler={sinon.spy()}
      resetSaksbehandlerSok={sinon.spy()}
      addSaksbehandler={sinon.spy()}
      avdelingensSaksbehandlere={[]}
      removeSaksbehandler={sinon.spy()}
      valgtAvdelingEnhet="2"
    />);

    expect(wrapper.find(SaksbehandlerePanel)).to.have.length(1);
    expect(fetchAvdelingensSaksbehandlere.calledOnce).to.be.true;
    const { args } = fetchAvdelingensSaksbehandlere.getCalls()[0];
    expect(args).to.have.length(1);
    expect(args[0]).to.eql('2');
  });
});
