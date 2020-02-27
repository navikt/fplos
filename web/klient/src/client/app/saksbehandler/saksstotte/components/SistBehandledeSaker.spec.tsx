
import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import Lenke from 'nav-frontend-lenker';
import sinon from 'sinon';
import { SistBehandledeSaker } from './SistBehandledeSaker';

describe('<SistBehandledeSaker>', () => {
  it('skal vise sist behandlede saker som lenker i en liste', () => {
    const oppgaver = [{
      id: 3,
      status: {
        erReservert: false,
      },
      saksnummer: 1,
      eksternId: '9280ce92-0958-43b5-89ef-9c57838f9e6d',
      personnummer: '123456789',
      navn: 'Espen Utvikler',
      system: 'FPSAK',
      behandlingstype: {
        kode: 'test',
        navn: 'test',
      },
      behandlingStatus: {
        kode: 'test',
        navn: 'test',
      },
      opprettetTidspunkt: '2018-01-01',
      behandlingsfrist: '2018-01-01',
      fagsakYtelseType: {
        kode: 'test',
        navn: 'test',
      },
      erTilSaksbehandling: true,
    }, {
      id: 4,
      status: {
        erReservert: false,
      },
      saksnummer: 2,
      eksternId: 'd2e74e79-5662-4a09-be3f-52fbcdf109ad',
      personnummer: '657643535',
      navn: 'Espen Solstråle',
      system: 'FPSAK',
      behandlingstype: {
        kode: 'test',
        navn: 'test',
      },
      behandlingStatus: {
        kode: 'test',
        navn: 'test',
      },
      opprettetTidspunkt: '2018-01-01',
      behandlingsfrist: '2018-01-01',
      fagsakYtelseType: {
        kode: 'test',
        navn: 'test',
      },
      erTilSaksbehandling: true,
    }];

    const wrapper = shallow(<SistBehandledeSaker
      fpsakUrl="www.fpsak.no"
      fptilbakeUrl="www.fptilbake.no"
      sistBehandledeSaker={oppgaver}
      hentFpsakBehandlingId={sinon.spy()}
    />);

    const links = wrapper.find(Lenke);
    expect(links).to.have.length(2);
    expect(links.first().childAt(0).text()).to.eql('Espen Utvikler 123456789');
    expect(links.last().childAt(0).text()).to.eql('Espen Solstråle 657643535');
  });

  it('skal ikke vise noen lenker når ingen behandlede saker blir funnet', () => {
    const oppgaver = [];
    const wrapper = shallow(<SistBehandledeSaker
      fpsakUrl="www.fpsak.no"
      fptilbakeUrl="www.fptilbake.no"
      sistBehandledeSaker={oppgaver}
      hentFpsakBehandlingId={sinon.spy()}
    />);

    expect(wrapper.find(Lenke)).to.have.length(0);
  });
});
