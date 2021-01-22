import React from 'react';
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
    expect(message).toHaveLength(2);
    expect(message.last().prop('id')).toEqual('SaksbehandlereTabell.IngenSaksbehandlere');

    expect(wrapper.find(Table)).toHaveLength(0);
    expect(wrapper.find(SletteSaksbehandlerModal)).toHaveLength(0);
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

    expect(wrapper.find(FormattedMessage)).toHaveLength(1);
    expect(wrapper.find(Table)).toHaveLength(1);

    const rader = wrapper.find(TableRow);
    expect(rader).toHaveLength(2);

    const kolonnerRad1 = rader.first().find(TableColumn);
    expect(kolonnerRad1).toHaveLength(4);
    expect(kolonnerRad1.first().childAt(0).text()).toEqual('Auto Joachim');
    expect(kolonnerRad1.at(1).childAt(0).text()).toEqual('TEST2');
    expect(kolonnerRad1.at(2).childAt(0).text()).toEqual('NAV Bærum');

    const kolonnerRad2 = rader.last().find(TableColumn);
    expect(kolonnerRad2).toHaveLength(4);
    expect(kolonnerRad2.first().childAt(0).text()).toEqual('Espen Utvikler');
    expect(kolonnerRad2.at(1).childAt(0).text()).toEqual('TEST1');
    expect(kolonnerRad2.at(2).childAt(0).text()).toEqual('NAV Oslo');
  });

  it(
    'skal vise modal for å slette saksbehandler ved trykk på slette-knapp',
    () => {
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
      expect(rader).toHaveLength(1);

      const kolonner = rader.first().find(TableColumn);
      const bildeKnapp = kolonner.last().find(Image);
      expect(bildeKnapp).toHaveLength(1);

      expect(wrapper.find(SletteSaksbehandlerModal)).toHaveLength(0);

      const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
      mouseFn();

      const modal = wrapper.find(SletteSaksbehandlerModal);
      expect(modal).toHaveLength(1);
      expect(modal.props().valgtSaksbehandler).toEqual(saksbehandlere[0]);
    },
  );

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
    expect(modal).toHaveLength(1);

    modal.prop('closeSletteModal')();

    expect(wrapper.find(SletteSaksbehandlerModal)).toHaveLength(0);
  });

  it('skal fjerne saksbehandler ved trykk på ok i modal', async () => {
    const saksbehandlere = [{
      brukerIdent: 'TEST1',
      navn: 'Espen Utvikler',
      avdelingsnavn: ['NAV Oslo'],
    }];

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
    expect(modal).toHaveLength(1);

    await modal.prop('fjernSaksbehandler')(saksbehandlere[0]);

    expect(wrapper.find(SletteSaksbehandlerModal)).toHaveLength(0);

    const fjernSaksbehandlerCallData = requestApi.getRequestMockData(RestApiPathsKeys.SLETT_SAKSBEHANDLER);
    expect(fjernSaksbehandlerCallData).toHaveLength(1);
    expect(fjernSaksbehandlerCallData[0].params.brukerIdent).toEqual(saksbehandlere[0].brukerIdent);
    expect(fjernSaksbehandlerCallData[0].params.avdelingEnhet).toEqual('2');
  });
});
