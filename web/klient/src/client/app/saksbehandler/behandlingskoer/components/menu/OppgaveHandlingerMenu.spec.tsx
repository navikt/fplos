import React from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';
import { FormattedMessage } from 'react-intl';

import BehandlingStatus from 'kodeverk/behandlingStatus';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import BehandlingType from 'kodeverk/behandlingType';
import OppgaveHandlingerMenu from './OppgaveHandlingerMenu';
import OpphevReservasjonModal from './OpphevReservasjonModal';
import OppgaveReservasjonForlengetModal from './OppgaveReservasjonForlengetModal';
import FlyttReservasjonModal from './FlyttReservasjonModal';
import MenuButton from './MenuButton';

describe('<OppgaveHandlingerMenu>', () => {
  const oppgave = {
    id: 1,
    status: {
      erReservert: false,
      reservertTilTidspunkt: '2020-02-02T23:59',
    },
    saksnummer: 1,
    behandlingId: '2',
    personnummer: '1234567',
    navn: 'Espen Utvikler',
    system: 'FPSAK',
    behandlingstype: {
      kode: BehandlingType.FORSTEGANGSSOKNAD,
      navn: '',
    },
    opprettetTidspunkt: '2017-01-01',
    behandlingsfrist: '2017-01-01',
    erTilSaksbehandling: true,
    fagsakYtelseType: {
      kode: FagsakYtelseType.FORELDREPRENGER,
      navn: 'FP',
    },
    behandlingStatus: {
      kode: BehandlingStatus.OPPRETTET,
      navn: '',
    },
    href: '',
  };

  it(
    'skal rendre meny med to knapper og vise tidspunkt reservasjonen gjelder til',
    () => {
      const wrapper = shallow(
        <OppgaveHandlingerMenu
          toggleMenu={sinon.spy()}
          offset={{
            top: 10,
            left: 20,
          }}
          oppgave={oppgave}
          imageNode={<div />}
          forlengOppgaveReservasjon={sinon.spy()}
          hentReserverteOppgaver={sinon.spy()}
        />,
      );

      expect(wrapper.find(MenuButton)).toHaveLength(4);
      const message = wrapper.find(FormattedMessage).first();
      const values = message.prop('values') as { date: string; time: string };
      expect(values.date).toEqual('02.02.2020');
      expect(values.time).toEqual('23:59');
    },
  );

  it(
    'skal vise modal for oppheving av reservasjon ved klikk på menyknapp og så lukke den ved å avbryte i modal',
    () => {
      const wrapper = shallow(
        <OppgaveHandlingerMenu
          toggleMenu={sinon.spy()}
          offset={{
            top: 10,
            left: 20,
          }}
          oppgave={oppgave}
          imageNode={<div />}
          forlengOppgaveReservasjon={sinon.spy()}
          hentReserverteOppgaver={sinon.spy()}
        />,
      );
      expect(wrapper.find(OpphevReservasjonModal)).toHaveLength(0);

      const menuButton = wrapper.find(MenuButton).first();
      menuButton.prop('onClick')();

      const modal = wrapper.find(OpphevReservasjonModal);
      expect(modal).toHaveLength(1);

      modal.prop('cancel')();

      expect(wrapper.find(OpphevReservasjonModal)).toHaveLength(0);
    },
  );

  it('skal vise modal for oppheving av reservasjon', () => {
    const wrapper = shallow(
      <OppgaveHandlingerMenu
        toggleMenu={sinon.spy()}
        offset={{
          top: 10,
          left: 20,
        }}
        oppgave={oppgave}
        imageNode={<div />}
        forlengOppgaveReservasjon={sinon.spy()}
        hentReserverteOppgaver={sinon.spy()}
      />,
    );

    const menuButton = wrapper.find(MenuButton).first();
    menuButton.prop('onClick')();

    const modal = wrapper.find(OpphevReservasjonModal);
    expect(modal).toHaveLength(1);
  });

  it('skal vise modal for forlenging av reservasjon', async () => {
    const forlengOppgaveReservasjonFn = (oppgaveId: number) => Promise.resolve(`${oppgaveId}`);
    const wrapper = shallow(
      <OppgaveHandlingerMenu
        toggleMenu={sinon.spy()}
        offset={{
          top: 10,
          left: 20,
        }}
        oppgave={oppgave}
        imageNode={<div />}
        forlengOppgaveReservasjon={forlengOppgaveReservasjonFn}
        hentReserverteOppgaver={sinon.spy()}
      />,
    );

    const menuButton = wrapper.find(MenuButton).at(1);
    await menuButton.prop('onClick')();

    const modal = wrapper.find(OppgaveReservasjonForlengetModal);
    expect(modal).toHaveLength(1);
  });

  it('skal vise modal for flytting av reservasjon', () => {
    const wrapper = shallow(
      <OppgaveHandlingerMenu
        toggleMenu={sinon.spy()}
        offset={{
          top: 10,
          left: 20,
        }}
        oppgave={oppgave}
        imageNode={<div />}
        forlengOppgaveReservasjon={sinon.spy()}
        hentReserverteOppgaver={sinon.spy()}
      />,
    );

    const menuButton = wrapper.find(MenuButton).last();
    menuButton.prop('onClick')();

    expect(wrapper.find(FlyttReservasjonModal)).toHaveLength(1);
  });

  it('skal vise flytt reservasjonsmodal', () => {
    const wrapper = shallow(
      <OppgaveHandlingerMenu
        toggleMenu={sinon.spy()}
        offset={{
          top: 10,
          left: 20,
        }}
        oppgave={oppgave}
        imageNode={<div />}
        forlengOppgaveReservasjon={sinon.spy()}
        hentReserverteOppgaver={sinon.spy()}
      />,
    );

    wrapper.setState({ showFlyttReservasjonModal: true });

    const modal = wrapper.find(FlyttReservasjonModal);
    expect(modal).toHaveLength(1);
  });
});
