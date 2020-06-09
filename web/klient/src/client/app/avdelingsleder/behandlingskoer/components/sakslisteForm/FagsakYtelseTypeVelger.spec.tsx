import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import * as useKodeverk from 'data/rest-api-hooks/src/global-data/useKodeverk';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { RadioOption, RadioGroupField } from 'form/FinalFields';

import FagsakYtelseTypeVelger from './FagsakYtelseTypeVelger';

describe('<FagsakYtelseTypeVelger>', () => {
  const fagsakYtelseTyper = [{
    kode: fagsakYtelseType.ENGANGSSTONAD,
    navn: 'Engangsstønad',
  }, {
    kode: fagsakYtelseType.FORELDREPRENGER,
    navn: 'Foreldrepenger',
  }, {
    kode: fagsakYtelseType.SVANGERSKAPPENGER,
    navn: 'Svangerskapspenger',
  }];

  let contextStub;
  before(() => {
    contextStub = sinon.stub(useKodeverk, 'default');
    contextStub.withArgs(kodeverkTyper.FAGSAK_YTELSE_TYPE).callsFake(() => fagsakYtelseTyper);
  });

  after(() => {
    contextStub.restore();
  });

  it('skal vise checkboxer for ytelsetyper', () => {
    const wrapper = shallow(<FagsakYtelseTypeVelger
      valgtSakslisteId={1}
      lagreSakslisteFagsakYtelseType={sinon.spy()}
      valgtAvdelingEnhet="3"
    />);

    const radios = wrapper.find(RadioOption);
    expect(radios).to.have.length(4);
    expect(radios.first().prop('value')).to.eql(fagsakYtelseType.FORELDREPRENGER);
    expect(radios.at(1).prop('value')).to.eql(fagsakYtelseType.ENGANGSSTONAD);
    expect(radios.last().prop('value')).to.eql('');
  });

  it('skal lagre ytelsetype ved klikk på checkbox', () => {
    const lagreYtelseTypeFn = sinon.spy();

    const wrapper = shallow(<FagsakYtelseTypeVelger
      valgtSakslisteId={1}
      lagreSakslisteFagsakYtelseType={lagreYtelseTypeFn}
      valgtAvdelingEnhet="3"
    />);

    const radioGroup = wrapper.find(RadioGroupField);
    radioGroup.prop('onChange')(fagsakYtelseType.ENGANGSSTONAD);

    expect(lagreYtelseTypeFn.calledOnce).to.be.true;
    const { args } = lagreYtelseTypeFn.getCalls()[0];
    expect(args).to.have.length(3);
    expect(args[0]).to.eql(1);
    expect(args[1]).to.eql(fagsakYtelseType.ENGANGSSTONAD);
    expect(args[2]).to.eql('3');
  });
});
