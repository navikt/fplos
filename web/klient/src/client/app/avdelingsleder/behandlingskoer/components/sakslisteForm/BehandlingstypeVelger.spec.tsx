import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { RestApiPathsKeys } from 'data/restApiPaths';
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

describe('<BehandlingstypeVelger>', () => {
  it('skal vise checkboxer for behandlingstyper', () => {
    new RestApiTestMocker()
      .withKodeverk(kodeverkTyper.BEHANDLING_TYPE, behandlingTyper)
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallow(<BehandlingstypeVelger
          valgtSakslisteId={1}
          valgtAvdelingEnhet="3"
          hentAvdelingensSakslister={sinon.spy()}
          hentAntallOppgaver={sinon.spy()}
        />);

        const checkboxer = wrapper.find(CheckboxField);
        expect(checkboxer).to.have.length(7);
        expect(checkboxer.first().prop('name')).to.eql(behandlingType.FORSTEGANGSSOKNAD);
        expect(checkboxer.last().prop('name')).to.eql(behandlingType.TILBAKEBETALING_REVURDERING);
      });
  });

  it('skal lagre behandlingstype ved klikk på checkbox', () => {
    const lagreBehandlingTypeFn = sinon.spy();
    new RestApiTestMocker()
      .withKodeverk(kodeverkTyper.BEHANDLING_TYPE, behandlingTyper)
      .withRestCallRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE,
        { startRequest: (params) => { lagreBehandlingTypeFn(params); return Promise.resolve(); } })
      .runTest(() => {
        const wrapper = shallow(<BehandlingstypeVelger
          valgtSakslisteId={1}
          valgtAvdelingEnhet="3"
          hentAvdelingensSakslister={sinon.spy()}
          hentAntallOppgaver={sinon.spy()}
        />);

        const checkbox = wrapper.find(CheckboxField);
        checkbox.first().prop('onChange')(true);

        expect(lagreBehandlingTypeFn.calledOnce).to.be.true;
        const { args } = lagreBehandlingTypeFn.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0].sakslisteId).to.eql(1);
        expect(args[0].behandlingType).to.eql(behandlingTyper[0]);
        expect(args[0].checked).is.true;
        expect(args[0].avdelingEnhet).to.eql('3');
      });
  });
});
