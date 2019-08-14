import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';
import { Form } from 'react-final-form';
import { FormattedMessage } from 'react-intl';
import { Column } from 'nav-frontend-grid';

import andreKriterierType from 'kodeverk/andreKriterierType';
import { CheckboxField } from 'form/FinalFields';
import { SaksbehandlereForSakslisteForm } from './SaksbehandlereForSakslisteForm';

describe('<SaksbehandlereForSakslisteForm>', () => {
  const saksliste = {
    sakslisteId: 1,
    navn: 'Nyansatte',
    sistEndret: '2017-08-31',
    andreKriterierTyper: [{
      kode: andreKriterierType.TIL_BESLUTTER,
      navn: 'Til beslutter',
    }, {
      kode: andreKriterierType.REGISTRER_PAPIRSOKNAD,
      navn: 'Registrer papirsøknad',
    }],
    saksbehandlerIdenter: [],
  };

  it('skal vise tekst når avdelingen ikke har tilordnede saksbehandlere', () => {
    const wrapper = shallow(<SaksbehandlereForSakslisteForm
      valgtSaksliste={saksliste}
      knyttSaksbehandlerTilSaksliste={sinon.spy()}
      valgtAvdelingEnhet="1"
    />).find(Form).drill(props => props.render()).shallow();

    const melding = wrapper.find(FormattedMessage);
    expect(melding).to.have.length(2);
    expect(melding.last().prop('id')).to.eql('SaksbehandlereForSakslisteForm.IngenSaksbehandlere');
  });

  it('skal vise kun en kolonne med saksbehandlere når det er tilordnet en saksbehandler', () => {
    const saksbehandlere = [{
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    }];

    const wrapper = shallow(<SaksbehandlereForSakslisteForm
      valgtSaksliste={saksliste}
      avdelingensSaksbehandlere={saksbehandlere}
      knyttSaksbehandlerTilSaksliste={sinon.spy()}
      valgtAvdelingEnhet="1"
    />).find(Form).drill(props => props.render()).shallow();

    expect(wrapper.find(FormattedMessage)).to.have.length(1);

    const kolonner = wrapper.find(Column);
    expect(kolonner).to.have.length(2);

    const checkBox = kolonner.first().find(CheckboxField);
    expect(checkBox).to.have.length(1);
    expect(checkBox.prop('name')).is.eql('TEST1');
    expect(checkBox.prop('label')).is.eql('Espen Utvikler');

    expect(kolonner.last().find(CheckboxField)).to.have.length(0);
  });

  it('skal vise to kolonner med saksbehandlere når det er tilordnet to saksbehandler', () => {
    const saksbehandlere = [{
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    }, {
      brukerIdent: 'TEST2',
      navn: 'Auto Joachim',
      avdelingsnavn: ['NAV Bærum'],
    }];

    const wrapper = shallow(<SaksbehandlereForSakslisteForm
      valgtSaksliste={saksliste}
      avdelingensSaksbehandlere={saksbehandlere}
      knyttSaksbehandlerTilSaksliste={sinon.spy()}
      valgtAvdelingEnhet="1"
    />).find(Form).drill(props => props.render()).shallow();

    expect(wrapper.find(FormattedMessage)).to.have.length(1);

    const kolonner = wrapper.find(Column);
    expect(kolonner).to.have.length(2);

    const checkBox1 = kolonner.first().find(CheckboxField);
    expect(checkBox1).to.have.length(1);
    expect(checkBox1.prop('name')).is.eql('TEST1');
    expect(checkBox1.prop('label')).is.eql('Espen Utvikler');

    const checkBox2 = kolonner.last().find(CheckboxField);
    expect(checkBox2).to.have.length(1);
    expect(checkBox2.prop('name')).is.eql('TEST2');
    expect(checkBox2.prop('label')).is.eql('Auto Joachim');
  });
});
