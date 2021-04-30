import React from 'react';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';

import { requestApi, RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import KodeverkType from 'kodeverk/kodeverkTyper';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import KoSortering from 'kodeverk/KoSortering';
import { RadioGroupField, RadioOption } from 'form/FinalFields';
import SorteringVelger from './SorteringVelger';

describe('<SorteringVelger>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  const koSorteringTyper = [{
    kode: KoSortering.OPPRETT_BEHANDLING,
    navn: 'opprett',
    felttype: '',
    feltkategori: '',
  }, {
    kode: KoSortering.BEHANDLINGSFRIST,
    navn: 'frist',
    felttype: '',
    feltkategori: '',
  }];

  const alleKodeverk = {
    [KodeverkType.KO_SORTERING]: koSorteringTyper,
  };

  it('skal vise radioknapper for alle sorteringsvalg', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallowWithIntl(<SorteringVelger.WrappedComponent
      intl={intl as IntlShape}
      valgtSakslisteId={1}
      valgtAvdelingEnhet="1"
      erDynamiskPeriode={false}
      valgteBehandlingtyper={[]}
      fra={10}
      til={10}
      fomDato="2020.01.01"
      tomDato="2020.10.01"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    const options = wrapper.find(RadioOption);
    expect(options).toHaveLength(2);
    expect(options.first().prop('value')).toEqual(KoSortering.OPPRETT_BEHANDLING);
    expect(options.last().prop('value')).toEqual(KoSortering.BEHANDLINGSFRIST);
  });

  it('skal lagre sortering ved klikk på radioknapp', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    requestApi.mock(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING, {});
    requestApi.mock(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_INTERVALL);

    const wrapper = shallowWithIntl(<SorteringVelger.WrappedComponent
      intl={intl as IntlShape}
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      erDynamiskPeriode={false}
      valgteBehandlingtyper={[]}
      fra={10}
      til={10}
      fomDato="2020.01.01"
      tomDato="2020.10.01"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    const felt = wrapper.find(RadioGroupField);
    // @ts-ignore
    felt.prop('onChange')(KoSortering.OPPRETT_BEHANDLING);

    const lagreSakslisteSorteringCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING);
    expect(lagreSakslisteSorteringCallData).toHaveLength(1);
    expect(lagreSakslisteSorteringCallData[0].params.sakslisteId).toEqual(1);
    expect(lagreSakslisteSorteringCallData[0].params.sakslisteSorteringValg).toEqual(KoSortering.OPPRETT_BEHANDLING);
    expect(lagreSakslisteSorteringCallData[0].params.avdelingEnhet).toEqual('3');
  });
});
