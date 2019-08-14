import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import { NokkeltallIndex } from './NokkeltallIndex';
import NokkeltallPanel from './components/NokkeltallPanel';

describe('<NokkeltallIndex>', () => {
  it('skal hente statistikk ved lasting av komponent', () => {
    const fetchOppgaverForAvdelingFn = sinon.spy();
    const fetchOppgaverPerDatoFn = sinon.spy();
    const fetchOppgaverAvdelingManueltPaVentFn = sinon.spy();
    const fetchOppgaverPerForsteStonadsdagFn = sinon.spy();

    const wrapper = shallow(<NokkeltallIndex
      fetchOppgaverForAvdeling={fetchOppgaverForAvdelingFn}
      fetchOppgaverPerDato={fetchOppgaverPerDatoFn}
      fetchOppgaverAvdelingManueltPaVent={fetchOppgaverAvdelingManueltPaVentFn}
      fetchOppgaverPerForsteStonadsdag={fetchOppgaverPerForsteStonadsdagFn}
      valgtAvdelingEnhet="2"
    />);

    expect(wrapper.find(NokkeltallPanel)).to.have.length(1);

    expect(fetchOppgaverForAvdelingFn.calledOnce).to.be.true;
    const { args: args1 } = fetchOppgaverForAvdelingFn.getCalls()[0];
    expect(args1).to.have.length(1);
    expect(args1[0]).to.eql('2');

    expect(fetchOppgaverPerDatoFn.calledOnce).to.be.true;
    const { args: args2 } = fetchOppgaverPerDatoFn.getCalls()[0];
    expect(args2).to.have.length(1);
    expect(args2[0]).to.eql('2');

    expect(fetchOppgaverAvdelingManueltPaVentFn.calledOnce).to.be.true;
    const { args: args3 } = fetchOppgaverAvdelingManueltPaVentFn.getCalls()[0];
    expect(args3).to.have.length(1);
    expect(args3[0]).to.eql('2');

    expect(fetchOppgaverPerForsteStonadsdagFn.calledOnce).to.be.true;
    const { args: args4 } = fetchOppgaverPerForsteStonadsdagFn.getCalls()[0];
    expect(args4).to.have.length(1);
    expect(args4[0]).to.eql('2');
  });
});
