import React from 'react';
import { expect } from 'chai';

import { Form } from 'react-final-form';

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
  const forstegangssoknad = {
    kode: behandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegangssøknad',
  };

  it('skal vise ytelsetyper i radioknapper', () => {
    const valuesMock = {
      valgtYtelseType: 'ALLE',
    };
    const oppgaverForAvdeling = [];

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypePanel
      width={300}
      height={200}
      fagsakYtelseTyper={fagsakYtelseTyper}
      oppgaverForAvdeling={oppgaverForAvdeling}
      initialValues={{ valgtYtelseType: valuesMock.valgtYtelseType }}
    />).find(Form).drill(props => props.render({ values: valuesMock })).shallow();

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
      fagsakYtelseTyper={fagsakYtelseTyper}
      oppgaverForAvdeling={oppgaverForAvdeling}
      initialValues={{ valgtYtelseType: valuesMock.valgtYtelseType }}
    />).find(Form).drill(props => props.render({ values: valuesMock })).shallow();

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
      fagsakYtelseTyper={fagsakYtelseTyper}
      oppgaverForAvdeling={oppgaverForAvdeling}
      initialValues={{ valgtYtelseType: valuesMock.valgtYtelseType }}
    />).find(Form).drill(props => props.render({ values: valuesMock })).shallow();

    const graf = wrapper.find(FordelingAvBehandlingstypeGraf);
    expect(graf).to.have.length(1);
    expect(graf.prop('oppgaverForAvdeling')).is.eql([oppgaverForAvdeling[0]]);
  });
});
