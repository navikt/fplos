import React from 'react';
import { shallow } from 'enzyme';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import FagsakSearch from './components/FagsakSearch';
import FagsakSearchIndex from './FagsakSearchIndex';

describe('<FagsakSearchIndex>', () => {
  const fagsak = {
    saksnummer: 12345,
    system: 'FPSAK',
    sakstype: {
      kode: 'ES',
      navn: 'test',
    },
    status: {
      kode: 'OPPR',
      navn: 'test',
    },
    barnFodt: '10.10.2017',
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
    requestApi.mock(RestApiPathsKeys.SEARCH_FAGSAK, fagsaker);
    requestApi.mock(RestApiPathsKeys.RESERVER_OPPGAVE);
    requestApi.mock(RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER);
    requestApi.mock(RestApiPathsKeys.HENT_RESERVASJONSSTATUS);
    requestApi.mock(RestApiPathsKeys.FPSAK_BEHANDLING_ID);

    const wrapper = shallow(<FagsakSearchIndex
      fpsakUrl=""
      fptilbakeUrl=""
    />);

    const fagsakSearchIndex = wrapper.find(FagsakSearch);
    expect(fagsakSearchIndex).toHaveLength(1);

    await fagsakSearchIndex.prop('searchFagsakCallback')({ searchString: 'test', skalReservere: false });

    expect(wrapper.find(FagsakSearch).prop('fagsaker')).toEqual(fagsaker);
  });
});
