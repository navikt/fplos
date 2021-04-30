import React from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';
import { FormattedMessage } from 'react-intl';

import Table from 'sharedComponents/table/Table';
import TableRow from 'sharedComponents/table/TableRow';
import BehandlingType from 'kodeverk/behandlingType';
import TableColumn from 'sharedComponents/table/TableColumn';
import ReservasjonerTabell from './ReservasjonerTabell';

describe('<ReservasjonerTabell>', () => {
  it('skal vise tekst som viser at ingen reservasjoner er lagt til', () => {
    const wrapper = shallow(<ReservasjonerTabell
      reservasjoner={[]}
      opphevReservasjon={sinon.spy()}
      hentAvdelingensReservasjoner={sinon.spy()}
    />);

    const message = wrapper.find(FormattedMessage);
    expect(message).toHaveLength(2);
    expect(message.last().prop('id')).toEqual('ReservasjonerTabell.IngenReservasjoner');

    expect(wrapper.find(Table)).toHaveLength(0);
  });

  it('skal vise to reservasjoner sortert i tabell', () => {
    const reservasjoner = [{
      oppgaveId: 1,
      reservertAvUid: 'ESPEN',
      reservertAvNavn: 'Espen Utvikler',
      reservertTilTidspunkt: '2020-02-02T14:30:00',
      behandlingType: {
        kode: BehandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangsbehandling',
      },
      oppgaveSaksNr: 10,
    }, {
      oppgaveId: 2,
      reservertAvUid: 'JOACHIM',
      reservertAvNavn: 'Auto Joachim',
      reservertTilTidspunkt: '2020-02-01T10:19:00',
      behandlingType: {
        kode: BehandlingType.REVURDERING,
        navn: 'Revurdering',
      },
      oppgaveSaksNr: 25,
    }];
    const wrapper = shallow(<ReservasjonerTabell
      reservasjoner={reservasjoner}
      opphevReservasjon={sinon.spy()}
      hentAvdelingensReservasjoner={sinon.spy()}
    />);

    expect(wrapper.find(FormattedMessage)).toHaveLength(3);
    expect(wrapper.find(Table)).toHaveLength(1);

    const rader = wrapper.find(TableRow);
    expect(rader).toHaveLength(2);

    const kolonnerRad1 = rader.first().find(TableColumn);
    expect(kolonnerRad1).toHaveLength(7);
    expect(kolonnerRad1.first().childAt(0).text()).toEqual('Auto Joachim');
    expect(kolonnerRad1.at(1).childAt(0).text()).toEqual('25');
    expect(kolonnerRad1.at(2).childAt(0).text()).toEqual('Revurdering');
    expect(kolonnerRad1.at(3).childAt(0).prop('id')).toEqual('ReservasjonerTabell.ReservertTilFormat');
    expect(kolonnerRad1.at(3).childAt(0).prop('values')).toEqual({ time: '10:19', date: '01.02.2020' });

    const kolonnerRad2 = rader.last().find(TableColumn);
    expect(kolonnerRad2).toHaveLength(7);
    expect(kolonnerRad2.first().childAt(0).text()).toEqual('Espen Utvikler');
    expect(kolonnerRad2.at(1).childAt(0).text()).toEqual('10');
    expect(kolonnerRad2.at(2).childAt(0).text()).toEqual('Førstegangsbehandling');
    expect(kolonnerRad2.at(3).childAt(0).prop('id')).toEqual('ReservasjonerTabell.ReservertTilFormat');
    expect(kolonnerRad2.at(3).childAt(0).prop('values')).toEqual({ time: '14:30', date: '02.02.2020' });
  });
});
