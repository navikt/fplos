import React from 'react';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import { requestApi, RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import KodeverkType from 'kodeverk/kodeverkTyper';
import AndreKriterierType from 'kodeverk/andreKriterierType';
import { CheckboxField, RadioGroupField, RadioOption } from 'form/FinalFields';
import AndreKriterierVelger from './AndreKriterierVelger';

describe('<AndreKriterierVelger>', () => {
  const andreKriterier = [{
    kode: AndreKriterierType.TIL_BESLUTTER,
    navn: 'Til beslutter',
  }, {
    kode: AndreKriterierType.REGISTRER_PAPIRSOKNAD,
    navn: 'Registrer papirsøknad',
  }];

  const alleKodeverk = {
    [KodeverkType.ANDRE_KRITERIER_TYPE]: andreKriterier,
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
    expect(checkboxer).toHaveLength(2);
    const tilBeslutterCheckbox = checkboxer.first();
    expect(tilBeslutterCheckbox.prop('name')).toEqual(AndreKriterierType.TIL_BESLUTTER);

    expect(wrapper.find(RadioGroupField)).toHaveLength(0);
    expect(wrapper.find(RadioOption)).toHaveLength(0);
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
    expect(checkboxer).toHaveLength(2);
    const tilBeslutterCheckbox = checkboxer.last();
    expect(tilBeslutterCheckbox.prop('name')).toEqual(AndreKriterierType.REGISTRER_PAPIRSOKNAD);
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
    // @ts-ignore
    checkbox.prop('onChange')(true);

    const lagreSakslisteAndreKriterierCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER);
    expect(lagreSakslisteAndreKriterierCallData).toHaveLength(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.sakslisteId).toEqual(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.andreKriterierType.kode).toEqual(AndreKriterierType.TIL_BESLUTTER);
    expect(lagreSakslisteAndreKriterierCallData[0].params.checked).toBe(true);
    expect(lagreSakslisteAndreKriterierCallData[0].params.inkluder).toBe(true);
    expect(lagreSakslisteAndreKriterierCallData[0].params.avdelingEnhet).toEqual('3');
  });

  it(
    'skal lagre valgt for Registrere papirsoknad ved klikk på checkbox',
    () => {
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
      // @ts-ignore
      checkbox.prop('onChange')(true);

      const lagreSakslisteAndreKriterierCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER);
      expect(lagreSakslisteAndreKriterierCallData).toHaveLength(1);
      expect(lagreSakslisteAndreKriterierCallData[0].params.sakslisteId).toEqual(1);
      expect(lagreSakslisteAndreKriterierCallData[0].params.andreKriterierType.kode).toEqual(AndreKriterierType.REGISTRER_PAPIRSOKNAD);
      expect(lagreSakslisteAndreKriterierCallData[0].params.checked).toBe(true);
      expect(lagreSakslisteAndreKriterierCallData[0].params.inkluder).toBe(true);
      expect(lagreSakslisteAndreKriterierCallData[0].params.avdelingEnhet).toEqual('3');
    },
  );

  it('skal vise radioknapper for å ta med eller fjerne', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    const wrapper = shallow(<AndreKriterierVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      values={{
        [AndreKriterierType.TIL_BESLUTTER]: true,
        [`${AndreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
      }}
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    expect(wrapper.find(RadioGroupField)).toHaveLength(1);
    expect(wrapper.find(RadioOption)).toHaveLength(2);
  });

  it('skal valge å fjerne inkludering av beslutter', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    requestApi.mock(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER, {});
    const wrapper = shallow(<AndreKriterierVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      values={{
        [AndreKriterierType.TIL_BESLUTTER]: true,
        [`${AndreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
      }}
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    // @ts-ignore
    wrapper.find(RadioGroupField).prop('onChange')(false);

    const lagreSakslisteAndreKriterierCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER);
    expect(lagreSakslisteAndreKriterierCallData).toHaveLength(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.sakslisteId).toEqual(1);
    expect(lagreSakslisteAndreKriterierCallData[0].params.andreKriterierType.kode).toEqual(AndreKriterierType.TIL_BESLUTTER);
    expect(lagreSakslisteAndreKriterierCallData[0].params.checked).toBe(true);
    expect(lagreSakslisteAndreKriterierCallData[0].params.inkluder).toBe(false);
    expect(lagreSakslisteAndreKriterierCallData[0].params.avdelingEnhet).toEqual('3');
  });
});
