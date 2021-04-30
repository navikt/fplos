import React from 'react';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import { requestApi, RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import KodeverkType from 'kodeverk/kodeverkTyper';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { RadioOption, RadioGroupField } from 'form/FinalFields';

import FagsakYtelseTypeVelger from './FagsakYtelseTypeVelger';

describe('<FagsakYtelseTypeVelger>', () => {
  const fagsakYtelseTyper = [{
    kode: FagsakYtelseType.ENGANGSSTONAD,
    navn: 'Engangsstønad',
  }, {
    kode: FagsakYtelseType.FORELDREPRENGER,
    navn: 'Foreldrepenger',
  }, {
    kode: FagsakYtelseType.SVANGERSKAPPENGER,
    navn: 'Svangerskapspenger',
  }];

  const alleKodeverk = {
    [KodeverkType.FAGSAK_YTELSE_TYPE]: fagsakYtelseTyper,
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
    expect(radios).toHaveLength(4);
    expect(radios.first().prop('value')).toEqual(FagsakYtelseType.FORELDREPRENGER);
    expect(radios.at(1).prop('value')).toEqual(FagsakYtelseType.ENGANGSSTONAD);
    expect(radios.last().prop('value')).toEqual('');
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
    // @ts-ignore
    await radioGroup.prop('onChange')(FagsakYtelseType.ENGANGSSTONAD);

    expect(hentAvdelingensSakslister.calledOnce).toBe(true);
    const { args } = hentAvdelingensSakslister.getCalls()[0];
    expect(args).toHaveLength(1);
    expect(args[0]).toEqual({ avdelingEnhet: '3' });
    expect(hentAntallOppgaver.calledOnce).toBe(true);
  });
});
