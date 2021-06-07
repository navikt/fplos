import React from 'react';
import { shallow } from 'enzyme';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import FagsakSearch from './components/FagsakSearch';
import FagsakSearchIndex from './FagsakSearchIndex';

describe('<FagsakSearchIndex>', () => {
  const fagsak = {
    saksnummer: 12345,
    system: 'FPSAK',
    fagsakYtelseType: {
      kode: 'ES',
      navn: 'test',
    },
    status: {
      kode: 'OPPR',
      navn: 'test',
    },
    barnFødt: '10.10.2017',
    person: {
      navn: 'Espen',
      alder: 38,
      personnummer: '123456789',
      erKvinne: true,
    },
    opprettet: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22',
  };
  const fagsaker = [fagsak, { ...fagsak, saksnummer: 23456 }];

  it('skal sette opp søkeskjermbilde for fagsaker', async () => {
    requestApi.mock(RestApiPathsKeys.SEARCH_FAGSAK.name, fagsaker);
    requestApi.mock(RestApiPathsKeys.RESERVER_OPPGAVE.name);
    requestApi.mock(RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER.name);
    requestApi.mock(RestApiPathsKeys.HENT_RESERVASJONSSTATUS.name);

    const wrapper = shallow(<FagsakSearchIndex
      fpsakUrl=""
    />);

    const fagsakSearchIndex = wrapper.find(FagsakSearch);
    expect(fagsakSearchIndex).toHaveLength(1);

    await fagsakSearchIndex.prop('searchFagsakCallback')({ searchString: 'test', skalReservere: false });

    expect(wrapper.find(FagsakSearch).prop('fagsaker')).toEqual(fagsaker);
  });
});
