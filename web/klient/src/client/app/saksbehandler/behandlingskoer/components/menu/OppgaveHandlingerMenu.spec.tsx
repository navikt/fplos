import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import sinon from 'sinon';
import { FormattedMessage } from 'react-intl';

import behandlingStatus from 'kodeverk/behandlingStatus';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import behandlingType from 'kodeverk/behandlingType';
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
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: '',
    },
    opprettetTidspunkt: '2017-01-01',
    behandlingsfrist: '2017-01-01',
    erTilSaksbehandling: true,
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'FP',
    },
    behandlingStatus: {
      kode: behandlingStatus.OPPRETTET,
      navn: '',
    },
    href: '',
  };

  it('skal rendre meny med to knapper og vise tidspunkt reservasjonen gjelder til', () => {
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

    expect(wrapper.find(MenuButton)).has.length(4);
    const message = wrapper.find(FormattedMessage).first();
    const values = message.prop('values') as { date: string; time: string };
    expect(values.date).is.eql('02.02.2020');
    expect(values.time).is.eql('23:59');
  });

  it('skal vise modal for oppheving av reservasjon ved klikk på menyknapp og så lukke den ved å avbryte i modal', () => {
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
    expect(wrapper.find(OpphevReservasjonModal)).has.length(0);

    const menuButton = wrapper.find(MenuButton).first();
    menuButton.prop('onClick')();

    const modal = wrapper.find(OpphevReservasjonModal);
    expect(modal).has.length(1);

    modal.prop('cancel')();

    expect(wrapper.find(OpphevReservasjonModal)).has.length(0);
  });

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
    expect(modal).has.length(1);
  });

  it('skal vise modal for forlenging av reservasjon', async () => {
    const forlengOppgaveReservasjonFn = (oppgaveId) => Promise.resolve(`${oppgaveId}`);
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
    expect(modal).has.length(1);
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

    expect(wrapper.find(FlyttReservasjonModal)).has.length(1);
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
    expect(modal).to.have.length(1);
  });
});
