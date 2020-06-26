import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { RadioOption, RadioGroupField } from 'form/FinalFields';
import { RestApiPathsKeys } from 'data/restApiPaths';

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

  it('skal vise checkboxer for ytelsetyper', () => {
    new RestApiTestMocker()
      .withKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE, fagsakYtelseTyper)
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallow(<FagsakYtelseTypeVelger
          valgtSakslisteId={1}
          valgtAvdelingEnhet="3"
          hentAvdelingensSakslister={sinon.spy()}
          hentAntallOppgaver={sinon.spy()}
        />);

        const radios = wrapper.find(RadioOption);
        expect(radios).to.have.length(4);
        expect(radios.first().prop('value')).to.eql(fagsakYtelseType.FORELDREPRENGER);
        expect(radios.at(1).prop('value')).to.eql(fagsakYtelseType.ENGANGSSTONAD);
        expect(radios.last().prop('value')).to.eql('');
      });
  });

  it('skal lagre ytelsetype ved klikk på checkbox', () => {
    const lagreYtelseTypeFn = sinon.spy();

    new RestApiTestMocker()
      .withKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE, fagsakYtelseTyper)
      .withRestCallRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE,
        { startRequest: (params) => { lagreYtelseTypeFn(params); return Promise.resolve(); } })
      .runTest(() => {
        const wrapper = shallow(<FagsakYtelseTypeVelger
          valgtSakslisteId={1}
          valgtAvdelingEnhet="3"
          hentAvdelingensSakslister={sinon.spy()}
          hentAntallOppgaver={sinon.spy()}
        />);

        const radioGroup = wrapper.find(RadioGroupField);
        radioGroup.prop('onChange')(fagsakYtelseType.ENGANGSSTONAD);

        expect(lagreYtelseTypeFn.calledOnce).to.be.true;
        const { args } = lagreYtelseTypeFn.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0].sakslisteId).to.eql(1);
        expect(args[0].fagsakYtelseType).to.eql(fagsakYtelseType.ENGANGSSTONAD);
        expect(args[0].avdelingEnhet).to.eql('3');
      });
  });
});
