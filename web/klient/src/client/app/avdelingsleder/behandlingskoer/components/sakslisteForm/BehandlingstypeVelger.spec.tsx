import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import kodeverkTyper from 'kodeverk/kodeverkTyper';
import behandlingType from 'kodeverk/behandlingType';
import { CheckboxField } from 'form/FinalFields';
import BehandlingstypeVelger from './BehandlingstypeVelger';

const alleKodeverk = {
  [kodeverkTyper.BEHANDLING_TYPE]: [{
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
  }],
};

describe('<BehandlingstypeVelger>', () => {
  it('skal vise checkboxer for behandlingstyper', () => {
    const wrapper = shallow(<BehandlingstypeVelger
      alleKodeverk={alleKodeverk}
      valgtSakslisteId={1}
      lagreSakslisteBehandlingstype={sinon.spy()}
      valgtAvdelingEnhet="3"
    />);

    const checkboxer = wrapper.find(CheckboxField);
    expect(checkboxer).to.have.length(7);
    expect(checkboxer.first().prop('name')).to.eql(behandlingType.FORSTEGANGSSOKNAD);
    expect(checkboxer.last().prop('name')).to.eql(behandlingType.TILBAKEBETALING_REVURDERING);
  });

  it('skal lagre behandlingstype ved klikk på checkbox', () => {
    const lagreBehandlingTypeFn = sinon.spy();

    const wrapper = shallow(<BehandlingstypeVelger
      alleKodeverk={alleKodeverk}
      valgtSakslisteId={1}
      lagreSakslisteBehandlingstype={lagreBehandlingTypeFn}
      valgtAvdelingEnhet="3"
    />);

    const checkbox = wrapper.find(CheckboxField);
    checkbox.first().prop('onChange')(true);

    expect(lagreBehandlingTypeFn.calledOnce).to.be.true;
    const { args } = lagreBehandlingTypeFn.getCalls()[0];
    expect(args).to.have.length(4);
    expect(args[0]).to.eql(1);
    expect(args[1]).to.eql(alleKodeverk[kodeverkTyper.BEHANDLING_TYPE][0]);
    expect(args[2]).is.true;
    expect(args[3]).to.eql('3');
  });
});
