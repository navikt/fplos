import React from 'react';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import { requestApi, RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import KodeverkType from 'kodeverk/kodeverkTyper';
import BehandlingType from 'kodeverk/behandlingType';
import { CheckboxField } from 'form/FinalFields';
import BehandlingstypeVelger from './BehandlingstypeVelger';

const behandlingTyper = [{
  kode: BehandlingType.FORSTEGANGSSOKNAD,
  navn: 'Førstegangssøknad',
}, {
  kode: BehandlingType.KLAGE,
  navn: 'Klage',
}, {
  kode: BehandlingType.DOKUMENTINNSYN,
  navn: 'Dokumentinnsyn',
}, {
  kode: BehandlingType.ANKE,
  navn: 'Anke',
}, {
  kode: BehandlingType.REVURDERING,
  navn: 'Revurdering',
}, {
  kode: BehandlingType.TILBAKEBETALING,
  navn: 'Tilbakebetaling',
}, {
  kode: BehandlingType.TILBAKEBETALING_REVURDERING,
  navn: 'Tilbakebetaling revurdering',
}];

const alleKodeverk = {
  [KodeverkType.BEHANDLING_TYPE]: behandlingTyper,
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
    expect(checkboxer.first().prop('name')).toEqual(BehandlingType.FORSTEGANGSSOKNAD);
    expect(checkboxer.last().prop('name')).toEqual(BehandlingType.TILBAKEBETALING_REVURDERING);
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
    // @ts-ignore
    checkbox.first().prop('onChange')(true);

    const lagreSakslisteBehandlingstypeCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE);
    expect(lagreSakslisteBehandlingstypeCallData).toHaveLength(1);
    expect(lagreSakslisteBehandlingstypeCallData[0].params.sakslisteId).toEqual(1);
    expect(lagreSakslisteBehandlingstypeCallData[0].params.behandlingType).toEqual(behandlingTyper[0]);
    expect(lagreSakslisteBehandlingstypeCallData[0].params.checked).toBe(true);
    expect(lagreSakslisteBehandlingstypeCallData[0].params.avdelingEnhet).toEqual('3');
  });
});
