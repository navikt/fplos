import React from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import Table from 'sharedComponents/table/Table';
import FagsakList, { getSorterteFagsaker } from './FagsakList';

describe('<FagsakList>', () => {
  const person = {
    navn: 'Frida',
    alder: 44,
    personnummer: '0405198632231',
    erKvinne: true,
    erDod: false,
  };

  const fagsak = {
    saksnummer: 12345,
    system: 'FPSAK',
    sakstype: {
      navn: '',
      kode: 'ES',
    },
    status: {
      navn: '',
      kode: 'UBEH',
    },
    barnFodt: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22',
    opprettet: '16‎.‎07‎.‎2004‎ ‎17‎:‎35‎:‎21',
    endret: '16‎.‎07‎.‎2004‎ ‎17‎:‎35‎:‎21',
    person,
  };

  const fagsakStatusTyper = [{
    navn: 'Under behandling',
    kode: 'UBEH',
  }, {
    navn: 'Avsluttet',
    kode: 'AVSLU',
  }];
  const fagsakYtelseTyper = [{
    navn: 'Engangsstonad',
    kode: 'ES',
  }, {
    navn: 'Engangsstonad',
    kode: 'TEST',
  }];

  const headerTextCodes = [
    'FagsakList.Saksnummer',
    'FagsakList.Stonadstype',
    'FagsakList.Behandlingstype',
    'FagsakList.Status',
    'FagsakList.BarnFodt',
    'EMPTY_1',
  ];

  const alleKodeverk = {
    [kodeverkTyper.FAGSAK_STATUS]: fagsakStatusTyper,
    [kodeverkTyper.FAGSAK_YTELSE_TYPE]: fagsakYtelseTyper,
  };

  it('skal vise en tabell med en rad og tilhørende kolonnedata', () => {
    const clickFunction = sinon.spy();
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(
      <FagsakList
        fagsaker={[fagsak]}
        fagsakOppgaver={[]}
        selectFagsakCallback={clickFunction}
        selectOppgaveCallback={sinon.spy()}
      />,
    );

    const table = wrapper.find(Table);
    expect(table).toHaveLength(1);

    expect(table.prop('headerTextCodes')).toEqual(headerTextCodes);

    const tableRows = table.children();
    expect(tableRows).toHaveLength(1);
    const tableColumns = tableRows.children();
    expect(tableColumns).toHaveLength(6);
    expect(tableColumns.first().childAt(0).text()).toEqual('12345');
    expect(tableColumns.at(1).childAt(0).text()).toEqual('Engangsstonad');
    expect(tableColumns.at(3).childAt(0).text()).toEqual('Under behandling');
  });

  it(
    'skal sortere søkeresultat der avsluttede skal vises sist, mens sist endrede skal vises først',
    () => {
      const fagsak2 = {
        saksnummer: 23456,
        system: 'FPSAK',
        sakstype: {
          navn: '',
          kode: 'TEST',
        },
        status: {
          navn: '',
          kode: 'UBEH',
        },
        opprettet: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22 ',
        endret: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22',
        person,
        oppgaver: [],
      };
      const fagsak3 = {
        saksnummer: 34567,
        system: 'FPSAK',
        sakstype: {
          navn: '',
          kode: 'TEST',
        },
        status: {
          navn: '',
          kode: 'AVSLU',
        },
        opprettet: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22',
        endret: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22',
        person,
      };

      const fagsaker = [fagsak, fagsak2, fagsak3];

      const sorterteFagsaker = getSorterteFagsaker(fagsaker);

      expect(sorterteFagsaker).toHaveLength(3);
      expect(sorterteFagsaker[0].saksnummer).toEqual(23456);
      expect(sorterteFagsaker[1].saksnummer).toEqual(12345);
      expect(sorterteFagsaker[2].saksnummer).toEqual(34567);
    },
  );

  it('skal vise DateLabel i tabell kun om barn er født', () => {
    const fagsak4 = {
      saksnummer: 23456,
      system: 'FPSAK',
      sakstype: {
        navn: '',
        kode: 'TEST',
      },
      status: {
        navn: '',
        kode: 'UBEH',
      },
      barnFodt: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22',
      opprettet: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22',
      endret: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22',
      person,
    };

    const clickFunction = sinon.spy();

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallow(
      <FagsakList
        fagsaker={[fagsak, fagsak4]}
        selectOppgaveCallback={sinon.spy()}
        selectFagsakCallback={clickFunction}
        fagsakOppgaver={[]}
      />,
    );

    const table = wrapper.find(Table);
    const tableRows = table.children();
    expect(tableRows).toHaveLength(2);
    const tableColumnsRow1 = tableRows.first().children();
    expect(tableColumnsRow1.at(4).childAt(0).text()).toEqual('<DateLabel />');
  });
});
