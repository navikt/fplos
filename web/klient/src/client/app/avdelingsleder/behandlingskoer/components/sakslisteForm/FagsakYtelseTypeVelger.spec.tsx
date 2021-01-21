import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import { requestApi, RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
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

  const alleKodeverk = {
    [kodeverkTyper.FAGSAK_YTELSE_TYPE]: fagsakYtelseTyper,
  };

  it('skal vise checkboxer for ytelsetyper', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
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

  it('skal lagre ytelsetype ved klikk på checkbox', async () => {
    const hentAvdelingensSakslister = sinon.spy();
    const hentAntallOppgaver = sinon.spy();
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    requestApi.mock(RestApiPathsKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE, {});

    const wrapper = shallow(<FagsakYtelseTypeVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      hentAvdelingensSakslister={hentAvdelingensSakslister}
      hentAntallOppgaver={hentAntallOppgaver}
    />);

    const radioGroup = wrapper.find(RadioGroupField);
    await radioGroup.prop('onChange')(fagsakYtelseType.ENGANGSSTONAD);

    expect(hentAvdelingensSakslister.calledOnce).to.be.true;
    const { args } = hentAvdelingensSakslister.getCalls()[0];
    expect(args).to.have.length(1);
    expect(args[0]).to.eql({ avdelingEnhet: '3' });
    expect(hentAntallOppgaver.calledOnce).to.be.true;
  });
});
