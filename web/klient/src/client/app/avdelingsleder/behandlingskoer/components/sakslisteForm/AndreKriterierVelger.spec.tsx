import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import { requestApi, RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import andreKriterierType from 'kodeverk/andreKriterierType';
import { CheckboxField, RadioGroupField, RadioOption } from 'form/FinalFields';
import AndreKriterierVelger from './AndreKriterierVelger';

describe('<AndreKriterierVelger>', () => {
  const andreKriterier = [{
    kode: andreKriterierType.TIL_BESLUTTER,
    navn: 'Til beslutter',
  }, {
    kode: andreKriterierType.REGISTRER_PAPIRSOKNAD,
    navn: 'Registrer papirsøknad',
  }];

  const alleKodeverk = {
    [kodeverkTyper.ANDRE_KRITERIER_TYPE]: andreKriterier,
  };

  it('skal vise checkbox for Til beslutter', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<AndreKriterierVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      values={{}}
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    const checkboxer = wrapper.find(CheckboxField);
    expect(checkboxer).to.have.length(2);
    const tilBeslutterCheckbox = checkboxer.first();
    expect(tilBeslutterCheckbox.prop('name')).to.eql(andreKriterierType.TIL_BESLUTTER);

    expect(wrapper.find(RadioGroupField)).to.have.length(0);
    expect(wrapper.find(RadioOption)).to.have.length(0);
  });

  it('skal vise checkbox for Registrere papirsøknad', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<AndreKriterierVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      values={{}}
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    const checkboxer = wrapper.find(CheckboxField);
    expect(checkboxer).to.have.length(2);
    const tilBeslutterCheckbox = checkboxer.last();
    expect(tilBeslutterCheckbox.prop('name')).to.eql(andreKriterierType.REGISTRER_PAPIRSOKNAD);
  });

  it('skal lagre valgt for Til beslutter ved klikk på checkbox', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    requestApi.mock(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER, {});

    const wrapper = shallow(<AndreKriterierVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      values={{}}
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    const checkbox = wrapper.find(CheckboxField).first();
    checkbox.prop('onChange')(true);

    const lagreSakslisteAndreKriterierCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER);
    expect(lagreSakslisteAndreKriterierCallData).to.have.length(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.sakslisteId).is.eql(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.andreKriterierType.kode).is.eql(andreKriterierType.TIL_BESLUTTER);
    expect(lagreSakslisteAndreKriterierCallData[0].params.checked).is.true;
    expect(lagreSakslisteAndreKriterierCallData[0].params.inkluder).is.true;
    expect(lagreSakslisteAndreKriterierCallData[0].params.avdelingEnhet).is.eql('3');
  });

  it('skal lagre valgt for Registrere papirsoknad ved klikk på checkbox', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    requestApi.mock(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER, {});

    const wrapper = shallow(<AndreKriterierVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      values={{}}
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    const checkbox = wrapper.find(CheckboxField).last();
    checkbox.prop('onChange')(true);

    const lagreSakslisteAndreKriterierCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER);
    expect(lagreSakslisteAndreKriterierCallData).to.have.length(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.sakslisteId).is.eql(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.andreKriterierType.kode).is.eql(andreKriterierType.REGISTRER_PAPIRSOKNAD);
    expect(lagreSakslisteAndreKriterierCallData[0].params.checked).is.true;
    expect(lagreSakslisteAndreKriterierCallData[0].params.inkluder).is.true;
    expect(lagreSakslisteAndreKriterierCallData[0].params.avdelingEnhet).is.eql('3');
  });

  it('skal vise radioknapper for å ta med eller fjerne', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    const wrapper = shallow(<AndreKriterierVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      values={{
        [andreKriterierType.TIL_BESLUTTER]: true,
        [`${andreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
      }}
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    expect(wrapper.find(RadioGroupField)).to.have.length(1);
    expect(wrapper.find(RadioOption)).to.have.length(2);
  });

  it('skal valge å fjerne inkludering av beslutter', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    requestApi.mock(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER, {});
    const wrapper = shallow(<AndreKriterierVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      values={{
        [andreKriterierType.TIL_BESLUTTER]: true,
        [`${andreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
      }}
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    wrapper.find(RadioGroupField).prop('onChange')(false);

    const lagreSakslisteAndreKriterierCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER);
    expect(lagreSakslisteAndreKriterierCallData).to.have.length(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.sakslisteId).is.eql(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.andreKriterierType.kode).is.eql(andreKriterierType.TIL_BESLUTTER);
    expect(lagreSakslisteAndreKriterierCallData[0].params.checked).is.true;
    expect(lagreSakslisteAndreKriterierCallData[0].params.inkluder).is.false;
    expect(lagreSakslisteAndreKriterierCallData[0].params.avdelingEnhet).is.eql('3');
  });
});
