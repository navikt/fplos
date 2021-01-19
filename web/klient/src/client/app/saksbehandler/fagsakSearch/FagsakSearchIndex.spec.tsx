import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';

import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import { RestApiPathsKeys } from 'data/restApiPaths';
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

  it('skal sette opp søkeskjermbilde for fagsaker', () => {
    new RestApiTestMocker()
      .withRestCallRunner(RestApiPathsKeys.SEARCH_FAGSAK, { data: fagsaker })
      .withRestCallRunner(RestApiPathsKeys.RESERVER_OPPGAVE, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.HENT_RESERVASJONSSTATUS, { startRequest: () => undefined })
      .withRestCallRunner(RestApiPathsKeys.FPSAK_BEHANDLING_ID, { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallow(<FagsakSearchIndex
          fpsakUrl=""
          fptilbakeUrl=""
        />);

        const fagsakSearchIndex = wrapper.find(FagsakSearch);
        expect(fagsakSearchIndex).to.have.length(1);
        expect(fagsakSearchIndex.prop('fagsaker')).to.eql(fagsaker);
      });
  });
});
