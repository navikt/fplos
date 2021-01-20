import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import sinon from 'sinon';
import { FormattedMessage } from 'react-intl';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import Image from 'sharedComponents/Image';
import Table from 'sharedComponents/table/Table';
import TableRow from 'sharedComponents/table/TableRow';
import TableColumn from 'sharedComponents/table/TableColumn';
import SaksbehandlereTabell from './SaksbehandlereTabell';
import SletteSaksbehandlerModal from './SletteSaksbehandlerModal';

describe('<SaksbehandlereTabell>', () => {
  it('skal vise tekst som viser at ingen saksbehandlere er lagt til', () => {
    const wrapper = shallow(<SaksbehandlereTabell
      saksbehandlere={[]}
      valgtAvdelingEnhet="2"
      hentAvdelingensSaksbehandlere={sinon.spy()}
    />);

    const message = wrapper.find(FormattedMessage);
    expect(message).to.have.length(2);
    expect(message.last().prop('id')).to.eql('SaksbehandlereTabell.IngenSaksbehandlere');

    expect(wrapper.find(Table)).to.have.length(0);
    expect(wrapper.find(SletteSaksbehandlerModal)).to.have.length(0);
  });

  it('skal vise to saksbehandlere sortert i tabell', () => {
    const saksbehandlere = [{
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    }, {
      brukerIdent: 'TEST2',
      navn: 'Auto Joachim',
      avdelingsnavn: ['NAV Bærum'],
    }];

    const wrapper = shallow(<SaksbehandlereTabell
      saksbehandlere={saksbehandlere}
      valgtAvdelingEnhet="2"
      hentAvdelingensSaksbehandlere={sinon.spy()}
    />);

    expect(wrapper.find(FormattedMessage)).to.have.length(1);
    expect(wrapper.find(Table)).to.have.length(1);

    const rader = wrapper.find(TableRow);
    expect(rader).to.have.length(2);

    const kolonnerRad1 = rader.first().find(TableColumn);
    expect(kolonnerRad1).to.have.length(4);
    expect(kolonnerRad1.first().childAt(0).text()).to.eql('Auto Joachim');
    expect(kolonnerRad1.at(1).childAt(0).text()).to.eql('TEST2');
    expect(kolonnerRad1.at(2).childAt(0).text()).to.eql('NAV Bærum');

    const kolonnerRad2 = rader.last().find(TableColumn);
    expect(kolonnerRad2).to.have.length(4);
    expect(kolonnerRad2.first().childAt(0).text()).to.eql('Espen Utvikler');
    expect(kolonnerRad2.at(1).childAt(0).text()).to.eql('TEST1');
    expect(kolonnerRad2.at(2).childAt(0).text()).to.eql('NAV Oslo');
  });

  it('skal vise modal for å slette saksbehandler ved trykk på slette-knapp', () => {
    const saksbehandlere = [{
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    }];

    const wrapper = shallow(<SaksbehandlereTabell
      saksbehandlere={saksbehandlere}
      valgtAvdelingEnhet="2"
      hentAvdelingensSaksbehandlere={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    expect(rader).to.have.length(1);

    const kolonner = rader.first().find(TableColumn);
    const bildeKnapp = kolonner.last().find(Image);
    expect(bildeKnapp).to.have.length(1);

    expect(wrapper.find(SletteSaksbehandlerModal)).to.have.length(0);

    const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
    mouseFn();

    const modal = wrapper.find(SletteSaksbehandlerModal);
    expect(modal).to.have.length(1);
    expect(modal.props().valgtSaksbehandler).is.eql(saksbehandlere[0]);
  });

  it('skal lukke modal ved trykk på avbryt i modal', () => {
    const saksbehandlere = [{
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    }];

    const wrapper = shallow(<SaksbehandlereTabell
      saksbehandlere={saksbehandlere}
      valgtAvdelingEnhet="2"
      hentAvdelingensSaksbehandlere={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    const kolonner = rader.first().find(TableColumn);
    const bildeKnapp = kolonner.last().find(Image);

    const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
    mouseFn();

    const modal = wrapper.find(SletteSaksbehandlerModal);
    expect(modal).to.have.length(1);

    modal.prop('closeSletteModal')();

    expect(wrapper.find(SletteSaksbehandlerModal)).to.have.length(0);
  });

  it('skal fjerne saksbehandler ved trykk på ok i modal', () => {
    const saksbehandlere = [{
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    }];
    const fjernSaksbehandlerFn = sinon.spy();

    requestApi.mock(RestApiPathsKeys.SLETT_SAKSBEHANDLER, {});

    const wrapper = shallow(<SaksbehandlereTabell
      saksbehandlere={saksbehandlere}
      valgtAvdelingEnhet="2"
      hentAvdelingensSaksbehandlere={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    const kolonner = rader.first().find(TableColumn);
    const bildeKnapp = kolonner.last().find(Image);

    const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
    mouseFn();

    const modal = wrapper.find(SletteSaksbehandlerModal);
    expect(modal).to.have.length(1);

    modal.prop('fjernSaksbehandler')(saksbehandlere[0]);

    expect(wrapper.find(SletteSaksbehandlerModal)).to.have.length(0);

    expect(fjernSaksbehandlerFn.calledOnce).to.be.true;
    const { args } = fjernSaksbehandlerFn.getCalls()[0];
    expect(args).to.have.length(1);
    expect(args[0].brukerIdent).to.eql(saksbehandlere[0].brukerIdent);
    expect(args[0].avdelingEnhet).to.eql('2');
  });
});
