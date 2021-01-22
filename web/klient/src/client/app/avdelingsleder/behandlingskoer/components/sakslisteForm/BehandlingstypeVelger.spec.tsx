import React from 'react';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import { requestApi, RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import behandlingType from 'kodeverk/behandlingType';
import { CheckboxField } from 'form/FinalFields';
import BehandlingstypeVelger from './BehandlingstypeVelger';

const behandlingTyper = [{
  kode: behandlingType.FORSTEGANGSSOKNAD,
  navn: 'Førstegangssøknad',
}, {
  kode: behandlingType.KLAGE,
  navn: 'Klage',
}, {
  kode: behandlingType.DOKUMENTINNSYN,
  navn: 'Dokumentinnsyn',
}, {
  kode: behandlingType.ANKE,
  navn: 'Anke',
}, {
  kode: behandlingType.REVURDERING,
  navn: 'Revurdering',
}, {
  kode: behandlingType.TILBAKEBETALING,
  navn: 'Tilbakebetaling',
}, {
  kode: behandlingType.TILBAKEBETALING_REVURDERING,
  navn: 'Tilbakebetaling revurdering',
}];

const alleKodeverk = {
  [kodeverkTyper.BEHANDLING_TYPE]: behandlingTyper,
};

describe('<BehandlingstypeVelger>', () => {
  it('skal vise checkboxer for behandlingstyper', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<BehandlingstypeVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    const checkboxer = wrapper.find(CheckboxField);
    expect(checkboxer).toHaveLength(7);
    expect(checkboxer.first().prop('name')).toEqual(behandlingType.FORSTEGANGSSOKNAD);
    expect(checkboxer.last().prop('name')).toEqual(behandlingType.TILBAKEBETALING_REVURDERING);
  });

  it('skal lagre behandlingstype ved klikk på checkbox', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    requestApi.mock(RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE, {});

    const wrapper = shallow(<BehandlingstypeVelger
      valgtSakslisteId={1}
      valgtAvdelingEnhet="3"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaver={sinon.spy()}
    />);

    const checkbox = wrapper.find(CheckboxField);
    checkbox.first().prop('onChange')(true);

    const lagreSakslisteBehandlingstypeCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE);
    expect(lagreSakslisteBehandlingstypeCallData).toHaveLength(1);
    expect(lagreSakslisteBehandlingstypeCallData[0].params.sakslisteId).toEqual(1);
    expect(lagreSakslisteBehandlingstypeCallData[0].params.behandlingType).toEqual(behandlingTyper[0]);
    expect(lagreSakslisteBehandlingstypeCallData[0].params.checked).toBe(true);
    expect(lagreSakslisteBehandlingstypeCallData[0].params.avdelingEnhet).toEqual('3');
  });
});
