import React, { KeyboardEvent } from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';
import { FormattedMessage } from 'react-intl';

import { requestApi, RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
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

  const alleKodeverk = {
    [kodeverkTyper.BEHANDLING_TYPE]: behandlingstyper,
    [kodeverkTyper.FAGSAK_YTELSE_TYPE]: fagsakYtelseTyper,
  };

  it('skal ikke vise tabell når ingen sakslister finnes', () => {
    const sakslister = [];

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      valgtAvdelingEnhet="2"
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      resetValgtSakslisteId={sinon.spy()}
      hentAvdelingensSakslister={sinon.spy()}
    />);

    const tekstComp = wrapper.find(FormattedMessage);
    expect(tekstComp).toHaveLength(4);
    expect(tekstComp.at(2).prop('id')).toEqual('GjeldendeSakslisterTabell.IngenLister');

    expect(wrapper.find(Table)).toHaveLength(0);
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

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      valgtAvdelingEnhet="2"
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      hentAvdelingensSakslister={sinon.spy()}
      resetValgtSakslisteId={sinon.spy()}
    />);

    expect(wrapper.find(FormattedMessage)).toHaveLength(7);
    expect(wrapper.find(Table)).toHaveLength(1);
    const rader = wrapper.find(TableRow);
    expect(rader).toHaveLength(2);

    const kolonnerForRad1 = rader.first().find(TableColumn);
    expect(kolonnerForRad1).toHaveLength(7);
    expect(kolonnerForRad1.first().childAt(0).text()).toEqual('Nyansatte');

    const kolonnerForRad2 = rader.last().find(TableColumn);
    expect(kolonnerForRad2).toHaveLength(7);
    expect(kolonnerForRad2.first().childAt(0).text()).toEqual('Kun foreldrepenger');
  });

  it('skal legge til ny saksliste ved musklikk', () => {
    const sakslister = [];
    const lagNySakslisteFn = sinon.spy();

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={lagNySakslisteFn}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      resetValgtSakslisteId={sinon.spy()}
    />);

    const leggTilListe = wrapper.find('div#leggTilListe');
    expect(leggTilListe).toHaveLength(1);

    const clickFn = leggTilListe.prop('onClick') as () => void;
    clickFn();

    expect(lagNySakslisteFn.calledOnce).toBe(true);
  });

  it('skal legge til ny saksliste ved trykk på enter-knapp', () => {
    const sakslister = [];
    const lagNySakslisteFn = sinon.spy();

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={lagNySakslisteFn}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      resetValgtSakslisteId={sinon.spy()}
    />);

    const leggTilListe = wrapper.find('div#leggTilListe');
    expect(leggTilListe).toHaveLength(1);

    leggTilListe.prop('onKeyDown')({
      keyCode: 13,
    } as KeyboardEvent);

    expect(lagNySakslisteFn.calledOnce).toBe(true);
  });

  it(
    'skal ikke legge til ny saksliste ved trykk på annen knapp enn enter',
    () => {
      const sakslister = [];
      const lagNySakslisteFn = sinon.spy();

      requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

      const wrapper = shallow(<GjeldendeSakslisterTabell
        sakslister={sakslister}
        setValgtSakslisteId={sinon.spy()}
        lagNySaksliste={lagNySakslisteFn}
        valgtAvdelingEnhet="2"
        hentAvdelingensSakslister={sinon.spy()}
        resetValgtSakslisteId={sinon.spy()}
      />);

      const leggTilListe = wrapper.find('div#leggTilListe');
      expect(leggTilListe).toHaveLength(1);

      leggTilListe.prop('onKeyDown')({
        keyCode: 10,
      } as KeyboardEvent);

      expect(lagNySakslisteFn.calledOnce).toBe(false);
    },
  );

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

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={setValgtSakslisteIdFn}
      lagNySaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      resetValgtSakslisteId={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    expect(rader).toHaveLength(1);

    const keyFn = rader.prop('onKeyDown') as () => void;
    await keyFn();

    expect(setValgtSakslisteIdFn.calledOnce).toBe(true);
  });

  it(
    'skal vise modal for å slette saksliste ved trykk på slette-knapp',
    () => {
      const sakslister = [{
        sakslisteId: 1,
        navn: 'Nyansatte',
        sistEndret: '2017-08-31',
        erTilBeslutter: false,
        erRegistrerPapirsoknad: false,
        saksbehandlerIdenter: [],
        antallBehandlinger: 1,
      }];

      requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

      const wrapper = shallow(<GjeldendeSakslisterTabell
        sakslister={sakslister}
        setValgtSakslisteId={sinon.spy()}
        lagNySaksliste={sinon.spy()}
        valgtAvdelingEnhet="2"
        hentAvdelingensSakslister={sinon.spy()}
        resetValgtSakslisteId={sinon.spy()}
      />);

      const rader = wrapper.find(TableRow);
      expect(rader).toHaveLength(1);

      const kolonner = rader.first().find(TableColumn);
      const bildeKnapp = kolonner.last().find(Image);
      expect(bildeKnapp).toHaveLength(1);

      expect(wrapper.find(SletteSakslisteModal)).toHaveLength(0);

      const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
      mouseFn();

      expect(wrapper.find(SletteSakslisteModal)).toHaveLength(1);
    },
  );

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

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      resetValgtSakslisteId={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    const kolonner = rader.first().find(TableColumn);
    const bildeKnapp = kolonner.last().find(Image);

    const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
    mouseFn();

    const modal = wrapper.find(SletteSakslisteModal);
    expect(modal).toHaveLength(1);

    modal.prop('cancel')();

    expect(wrapper.find(SletteSakslisteModal)).toHaveLength(0);
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

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);
    requestApi.mock(RestApiPathsKeys.SLETT_SAKSLISTE, {});

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      resetValgtSakslisteId={sinon.spy()}
    />);

    const rader = wrapper.find(TableRow);
    const kolonner = rader.first().find(TableColumn);
    const bildeKnapp = kolonner.last().find(Image);

    const mouseFn = bildeKnapp.prop('onMouseDown') as () => void;
    mouseFn();

    const modal = wrapper.find(SletteSakslisteModal);
    expect(modal).toHaveLength(1);

    modal.prop('submit')(sakslister[0]);

    expect(wrapper.find(SletteSakslisteModal)).toHaveLength(0);

    const fjernSakslisterCallData = requestApi.getRequestMockData(RestApiPathsKeys.SLETT_SAKSLISTE);
    expect(fjernSakslisterCallData).toHaveLength(1);
    expect(fjernSakslisterCallData[0].params.sakslisteId).toEqual(1);
    expect(fjernSakslisterCallData[0].params.avdelingEnhet).toEqual('2');
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

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(<GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      valgtAvdelingEnhet="2"
      hentAvdelingensSakslister={sinon.spy()}
      resetValgtSakslisteId={sinon.spy()}
    />);

    expect(wrapper.find(Table)).toHaveLength(1);
    const rader = wrapper.find(TableRow);
    expect(rader).toHaveLength(1);

    const kolonnerForRad = rader.first().find(TableColumn);
    expect(kolonnerForRad).toHaveLength(7);
    expect(kolonnerForRad.at(3).childAt(0).text()).toEqual('1');
  });
});
