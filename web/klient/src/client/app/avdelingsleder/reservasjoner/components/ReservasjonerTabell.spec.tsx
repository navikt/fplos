import { shallow } from 'enzyme';
import React from 'react';
import { FormattedMessage } from 'react-intl';
import { expect } from 'chai';
import Table from 'sharedComponents/Table';
import TableRow from 'sharedComponents/TableRow';
import TableColumn from 'sharedComponents/TableColumn';
import { ReservasjonerTabell } from './ReservasjonerTabell';

describe('<ReservasjonerTabell>', () => {
  it('skal vise tekst som viser at ingen reservasjoner er lagt til', () => {
    const wrapper = shallow(<ReservasjonerTabell
      reservasjoner={[]}
      valgtAvdelingEnhet="2"
    />);

    const message = wrapper.find(FormattedMessage);
    expect(message).to.have.length(2);
    expect(message.last().prop('id')).to.eql('ReservasjonerTabell.IngenReservasjoner');

    expect(wrapper.find(Table)).to.have.length(0);
  });

  it('skal vise to reservasjoner sortert i tabell', () => {
    const reservasjoner = [{
      reservertAvUid: 'ESPEN',
      reservertAvNavn: 'Espen Utvikler',
      reservertTilTidspunkt: '14:30 02.02.2020',
      oppgaveId: 1,
      oppgaveSaksNr: 10,
    }, {
      reservertAvUid: 'JOACHIM',
      reservertAvNavn: 'Auto Joachim',
      reservertTilTidspunkt: '10:19 02.02.2020',
      oppgaveId: 3,
      oppgaveSaksNr: 25,
    }];
    const wrapper = shallow(<ReservasjonerTabell
      reservasjoner={reservasjoner}
      valgtAvdelingEnhet="2"
    />);

    expect(wrapper.find(FormattedMessage)).to.have.length(1);
    expect(wrapper.find(Table)).to.have.length(1);

    const rader = wrapper.find(TableRow);
    expect(rader).to.have.length(2);

    const kolonnerRad1 = rader.first().find(TableColumn);
    expect(kolonnerRad1).to.have.length(5);
    expect(kolonnerRad1.first().childAt(0).text()).to.eql('Auto Joachim');
    expect(kolonnerRad1.at(1).childAt(0).text()).to.eql('3');
    expect(kolonnerRad1.at(2).childAt(0).text()).to.eql('25');
    expect(kolonnerRad1.at(3).childAt(0).text()).to.eql('10:19 02.02.2020');

    const kolonnerRad2 = rader.last().find(TableColumn);
    expect(kolonnerRad2).to.have.length(5);
    expect(kolonnerRad2.first().childAt(0).text()).to.eql('Espen Utvikler');
    expect(kolonnerRad2.at(1).childAt(0).text()).to.eql('1');
    expect(kolonnerRad2.at(2).childAt(0).text()).to.eql('10');
    expect(kolonnerRad2.at(3).childAt(0).text()).to.eql('14:30 02.02.2020');
  });
});
