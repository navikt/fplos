import React, { KeyboardEvent } from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';
import { FormattedMessage } from 'react-intl';

import kodeverkTyper from 'kodeverk/kodeverkTyper';
import * as useKodeverk from 'data/useKodeverk';
import Image from 'sharedComponents/Image';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import Table from 'sharedComponents/table/Table';
import TableRow from 'sharedComponents/table/TableRow';
import TableColumn from 'sharedComponents/table/TableColumn';
import SletteSakslisteModal from './SletteSakslisteModal';
import { GjeldendeSakslisterTabell } from './GjeldendeSakslisterTabell';

describe('<GjeldendeSakslisterTabell>', () => {
  const behandlingstyper = [{
    kode: behandlingType.FORSTEGANGSSOKNAD,
    navn: '',
  }, {
    kode: behandlingType.KLAGE,
    navn: '',
  },
  ];
  const fagsakYtelseTyper = [{
    kode: fagsakYtelseType.ENGANGSSTONAD,
    navn: '',
  }, {
    kode: fagsakYtelseType.FORELDREPRENGER,
    navn: '',
  },
  ];

  let contextStub;
  before(() => {
    contextStub = sinon.stub(useKodeverk, 'default');
    contextStub.withArgs(kodeverkTyper.BEHANDLING_TYPE).callsFake(() => behandlingstyper)
      .withArgs(kodeverkTyper.FAGSAK_YTELSE_TYPE)
      .callsFake(() => fagsakYtelseTyper);
  });

  after(() => {
    contextStub.restore();
  });

  it('skal ikke vise tabell når ingen sakslister finnes', () => {
    const sakslister = [];

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    const tekstComp = wrapper.find(FormattedMessage);
    expect(tekstComp).to.have.length(4);
    expect(tekstComp.at(2).prop('id')).to.eql('GjeldendeSakslisterTabell.IngenLister');

    expect(wrapper.find(Table)).to.have.length(0);
  });

  it('skal vise to sakslister', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    }, {
      sakslisteId: 2,
      navn: 'Kun foreldrepenger',
      sistEndret: '2018-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    }];

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    expect(wrapper.find(FormattedMessage)).to.have.length(7);
    expect(wrapper.find(Table)).to.have.length(1);
    const rader = wrapper.find(TableRow);
    expect(rader).to.have.length(2);

    const kolonnerForRad1 = rader.first().find(TableColumn);
    expect(kolonnerForRad1).to.have.length(7);
    expect(kolonnerForRad1.first().childAt(0).text()).to.eql('Nyansatte');

    const kolonnerForRad2 = rader.last().find(TableColumn);
    expect(kolonnerForRad2).to.have.length(7);
    expect(kolonnerForRad2.first().childAt(0).text()).to.eql('Kun foreldrepenger');
  });

  it('skal legge til ny saksliste ved musklikk', () => {
    const sakslister = [];
    const lagNySakslisteFn = sinon.spy();

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={lagNySakslisteFn}
      fjernSaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    const leggTilListe = wrapper.find('div#leggTilListe');
    expect(leggTilListe).to.have.length(1);

    const clickFn = leggTilListe.prop('onClick') as () => void;
    clickFn();

    expect(lagNySakslisteFn.calledOnce).to.be.true;
  });

  it('skal legge til ny saksliste ved trykk på enter-knapp', () => {
    const sakslister = [];
    const lagNySakslisteFn = sinon.spy();

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={lagNySakslisteFn}
      fjernSaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    const leggTilListe = wrapper.find('div#leggTilListe');
    expect(leggTilListe).to.have.length(1);

    leggTilListe.prop('onKeyDown')({
      keyCode: 13,
    } as KeyboardEvent);

    expect(lagNySakslisteFn.calledOnce).to.be.true;
  });

  it('skal ikke legge til ny saksliste ved trykk på annen knapp enn enter', () => {
    const sakslister = [];
    const lagNySakslisteFn = sinon.spy();

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={lagNySakslisteFn}
      fjernSaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    const leggTilListe = wrapper.find('div#leggTilListe');
    expect(leggTilListe).to.have.length(1);

    leggTilListe.prop('onKeyDown')({
      keyCode: 10,
    } as KeyboardEvent);

    expect(lagNySakslisteFn.calledOnce).to.be.false;
  });

  it('skal sette valgt saksliste ved trykk på rad i tabell', async () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    }];
    const setValgtSakslisteIdFn = sinon.spy();

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={setValgtSakslisteIdFn}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    expect(rader).to.have.length(1);

    const keyFn = rader.prop('onKeyDown') as () => void;
    await keyFn();

    expect(setValgtSakslisteIdFn.calledOnce).to.be.true;
  });

  it('skal vise modal for å slette saksliste ved trykk på slette-knapp', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    }];
    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    expect(rader).to.have.length(1);

    const kolonner = rader.first().find(TableColumn);
    const bildeKnapp = kolonner.last().find(Image);
    expect(bildeKnapp).to.have.length(1);

    expect(wrapper.find(SletteSakslisteModal)).to.have.length(0);

    const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
    mouseFn();

    expect(wrapper.find(SletteSakslisteModal)).to.have.length(1);
  });

  it('skal lukke modal ved trykk på avbryt i modal', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    }];
    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    const kolonner = rader.first().find(TableColumn);
    const bildeKnapp = kolonner.last().find(Image);

    const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
    mouseFn();

    const modal = wrapper.find(SletteSakslisteModal);
    expect(modal).to.have.length(1);

    modal.prop('cancel')();

    expect(wrapper.find(SletteSakslisteModal)).to.have.length(0);
  });

  it('skal fjerne saksliste ved trykk på ok i modal', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    }];
    const fjernSakslisterFn = sinon.spy();
    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={fjernSakslisterFn}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    const kolonner = rader.first().find(TableColumn);
    const bildeKnapp = kolonner.last().find(Image);

    const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
    mouseFn();

    const modal = wrapper.find(SletteSakslisteModal);
    expect(modal).to.have.length(1);

    modal.prop('submit')(sakslister[0]);

    expect(wrapper.find(SletteSakslisteModal)).to.have.length(0);

    expect(fjernSakslisterFn.calledOnce).to.be.true;
    const { args } = fjernSakslisterFn.getCalls()[0];
    expect(args).to.have.length(2);
    expect(args[0]).to.eql(sakslister[0].sakslisteId);
    expect(args[1]).to.eql('2');
  });

  it('skal vise antall saksbehandlere tilknyttet sakslisten', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: ['U12332'],
      antallBehandlinger: 1,
    }];

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
    />);

    expect(wrapper.find(Table)).to.have.length(1);
    const rader = wrapper.find(TableRow);
    expect(rader).to.have.length(1);

    const kolonnerForRad = rader.first().find(TableColumn);
    expect(kolonnerForRad).to.have.length(7);
    expect(kolonnerForRad.at(3).childAt(0).text()).to.eql('1');
  });
});
