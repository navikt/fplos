import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { Form } from 'react-final-form';

import kodeverkTyper from 'kodeverk/kodeverkTyper';
import * as useKodeverk from 'data/useKodeverk';
import { shallowWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import behandlingType from 'kodeverk/behandlingType';
import { RadioOption } from 'form/FinalFields';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { FordelingAvBehandlingstypePanel } from './FordelingAvBehandlingstypePanel';
import FordelingAvBehandlingstypeGraf from './FordelingAvBehandlingstypeGraf';

describe('<FordelingAvBehandlingstypePanel>', () => {
  const fagsakYtelseTyper = [{
    kode: fagsakYtelseType.ENGANGSSTONAD,
    navn: 'Engangsstønad',
  }, {
    kode: fagsakYtelseType.FORELDREPRENGER,
    navn: 'Foreldrepenger',
  }];
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
    kode: behandlingType.REVURDERING,
    navn: 'Revurdering',
  }, {
    kode: behandlingType.TILBAKEBETALING,
    navn: 'Tilbakebetaling',
  }, {
    kode: behandlingType.TILBAKEBETALING_REVURDERING,
    navn: 'Tilbakebet-rev',
  }];
  const forstegangssoknad = {
    kode: behandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegangssøknad',
  };

  let contextStub;
  before(() => {
    contextStub = sinon.stub(useKodeverk, 'default');
    contextStub.withArgs(kodeverkTyper.BEHANDLING_TYPE).callsFake(() => behandlingTyper)
      .withArgs(kodeverkTyper.FAGSAK_YTELSE_TYPE)
      .callsFake(() => fagsakYtelseTyper);
  });

  after(() => {
    contextStub.restore();
  });

  it('skal vise ytelsetyper i radioknapper', () => {
    const valuesMock = {
      valgtYtelseType: 'ALLE',
    };
    const oppgaverForAvdeling = [];

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypePanel
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      initialValues={{ valgtYtelseType: valuesMock.valgtYtelseType }}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const radioOptions = wrapper.find(RadioOption);
    expect(radioOptions).to.have.length(4);
    expect(radioOptions.first().prop('value')).to.eql('FP');
    expect(radioOptions.first().prop('label')).to.eql('Foreldrepenger');
    expect(radioOptions.at(1).prop('value')).to.eql('ES');
    expect(radioOptions.at(1).prop('label')).to.eql('Engangsstønad');
    expect(radioOptions.last().prop('value')).to.eql('ALLE');
  });


  it('skal filtrere bort engangsstønader', () => {
    const valuesMock = {
      valgtYtelseType: fagsakYtelseType.FORELDREPRENGER,
    };
    const oppgaverForAvdeling = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: forstegangssoknad,
      tilBehandling: true,
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingType: forstegangssoknad,
      tilBehandling: true,
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypePanel
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      initialValues={{ valgtYtelseType: valuesMock.valgtYtelseType }}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(FordelingAvBehandlingstypeGraf);
    expect(graf).to.have.length(1);
    expect(graf.prop('oppgaverForAvdeling')).is.eql([oppgaverForAvdeling[1]]);
  });

  it('skal filtrere bort foreldrepenger', () => {
    const valuesMock = {
      valgtYtelseType: fagsakYtelseType.ENGANGSSTONAD,
    };
    const oppgaverForAvdeling = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: forstegangssoknad,
      tilBehandling: true,
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingType: forstegangssoknad,
      tilBehandling: true,
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypePanel
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      initialValues={{ valgtYtelseType: valuesMock.valgtYtelseType }}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(FordelingAvBehandlingstypeGraf);
    expect(graf).to.have.length(1);
    expect(graf.prop('oppgaverForAvdeling')).is.eql([oppgaverForAvdeling[0]]);
  });
});
