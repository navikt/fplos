import React from 'react';
import { expect } from 'chai';

import { Form } from 'react-final-form';
import moment from 'moment';

import { ISO_DATE_FORMAT } from 'utils/formats';
import { RadioOption, SelectField } from 'form/FinalFields';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { ManueltPaVentPanel, ALLE_YTELSETYPER_VALGT } from './ManueltPaVentPanel';
import ManueltPaVentGraf from './ManueltPaVentGraf';

describe('<ManueltPaVentPanel>', () => {
  const fagsakYtelseTyper = [{
    kode: fagsakYtelseType.ENGANGSSTONAD,
    navn: 'Engangsstønad',
  }, {
    kode: fagsakYtelseType.FORELDREPRENGER,
    navn: 'Foreldrepenger',
  }];

  it('skal vise ukevalg i dropdown og valg av ytelsetype i radioknapper', () => {
    const valuesMock = {
      valgtYtelsetype: ALLE_YTELSETYPER_VALGT,
      ukevalg: '4',
    };
    const oppgaverManueltPaVent = [];

    const wrapper = shallowWithIntl(<ManueltPaVentPanel
      intl={intlMock}
      width={300}
      height={200}
      fagsakYtelseTyper={fagsakYtelseTyper}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      initialValues={{ valgtYtelsetype: valuesMock.valgtYtelsetype, ukevalg: valuesMock.ukevalg }}
    />).find(Form).drill(props => props.render({ values: valuesMock })).shallow();

    const select = wrapper.find(SelectField);
    expect(select).to.have.length(1);

    const options = select.prop('selectValues');
    expect(options).to.have.length(2);
    expect(options[0].props.value).to.eql('4');
    expect(options[0].props.children).to.eql('4 uker frem');
    expect(options[1].props.value).to.eql('8');
    expect(options[1].props.children).to.eql('8 uker frem');


    const radioOptions = wrapper.find(RadioOption);
    expect(radioOptions).to.have.length(4);
    expect(radioOptions.first().prop('value')).to.eql('FP');
    expect(radioOptions.first().prop('label')).to.eql('Foreldrepenger');
    expect(radioOptions.at(1).prop('value')).to.eql('ES');
    expect(radioOptions.at(1).prop('label')).to.eql('Engangsstønad');
    expect(radioOptions.last().prop('value')).to.eql('ALLE');
  });

  it('skal filtrere bort alt som er lengre frem i tid enn fire uker', () => {
    const valuesMock = {
      valgtYtelsetype: ALLE_YTELSETYPER_VALGT,
      ukevalg: '4',
    };
    const oppgaverManueltPaVent = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingFrist: moment().add(5, 'w').format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingFrist: moment().add(3, 'w').format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<ManueltPaVentPanel
      intl={intlMock}
      width={300}
      height={200}
      fagsakYtelseTyper={fagsakYtelseTyper}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      initialValues={{ valgtYtelsetype: valuesMock.valgtYtelsetype, ukevalg: valuesMock.ukevalg }}
    />).find(Form).drill(props => props.render({ values: valuesMock })).shallow();

    const graf = wrapper.find(ManueltPaVentGraf);
    expect(graf).to.have.length(1);
    expect(graf.prop('isFireUkerValgt')).is.true;
    expect(graf.prop('oppgaverManueltPaVent')).is.eql([oppgaverManueltPaVent[1]]);
  });

  it('skal ikke filtrere bort alt som er lengre frem i tid enn fire uker når åtte uker er valgt i filter', () => {
    const valuesMock = {
      valgtYtelsetype: ALLE_YTELSETYPER_VALGT,
      ukevalg: '8',
    };
    const oppgaverManueltPaVent = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingFrist: moment().add(4, 'w').format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingFrist: moment().add(7, 'w').format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<ManueltPaVentPanel
      intl={intlMock}
      width={300}
      height={200}
      fagsakYtelseTyper={fagsakYtelseTyper}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      initialValues={{ valgtYtelsetype: valuesMock.valgtYtelsetype, ukevalg: valuesMock.ukevalg }}
    />).find(Form).drill(props => props.render({ values: valuesMock })).shallow();

    const graf = wrapper.find(ManueltPaVentGraf);
    expect(graf).to.have.length(1);
    expect(graf.prop('isFireUkerValgt')).is.false;
    expect(graf.prop('oppgaverManueltPaVent')).is.eql(oppgaverManueltPaVent);
  });

  it('skal filtrere bort engangsstønader', () => {
    const valuesMock = {
      valgtYtelsetype: fagsakYtelseType.FORELDREPRENGER,
      ukevalg: '4',
    };
    const oppgaverManueltPaVent = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingFrist: moment().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingFrist: moment().format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<ManueltPaVentPanel
      intl={intlMock}
      width={300}
      height={200}
      fagsakYtelseTyper={fagsakYtelseTyper}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      initialValues={{ valgtYtelsetype: valuesMock.valgtYtelsetype, ukevalg: valuesMock.ukevalg }}
    />).find(Form).drill(props => props.render({ values: valuesMock })).shallow();

    const graf = wrapper.find(ManueltPaVentGraf);
    expect(graf).to.have.length(1);
    expect(graf.prop('oppgaverManueltPaVent')).is.eql([oppgaverManueltPaVent[1]]);
  });

  it('skal filtrere bort foreldrepenger', () => {
    const valuesMock = {
      valgtYtelsetype: fagsakYtelseType.ENGANGSSTONAD,
      ukevalg: '4',
    };
    const oppgaverManueltPaVent = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingFrist: moment().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingFrist: moment().format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<ManueltPaVentPanel
      intl={intlMock}
      width={300}
      height={200}
      fagsakYtelseTyper={fagsakYtelseTyper}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      initialValues={{ valgtYtelsetype: valuesMock.valgtYtelsetype, ukevalg: valuesMock.ukevalg }}
    />).find(Form).drill(props => props.render({ values: valuesMock })).shallow();

    const graf = wrapper.find(ManueltPaVentGraf);
    expect(graf).to.have.length(1);
    expect(graf.prop('oppgaverManueltPaVent')).is.eql([oppgaverManueltPaVent[0]]);
  });
});
