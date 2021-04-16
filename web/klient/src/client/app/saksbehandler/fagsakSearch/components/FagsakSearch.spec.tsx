import React from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';
import { FormattedMessage } from 'react-intl';

import FagsakSearch from './FagsakSearch';
import PersonInfo from './person/PersonInfo';
import SearchForm from './SearchForm';
import FagsakList from './FagsakList';

describe('<FagsakSearch>', () => {
  const fagsak = {
    saksnummer: 12345,
    saksnummerString: '12345',
    system: 'FPSAK',
    fagsakYtelseType: {
      navn: 'Engangsstonad',
      kode: 'TEST',
    },
    status: {
      navn: 'Under behandling',
      kode: 'UBEH',
    },
    barnFodt: '13‎.‎02‎.‎2017‎',
    opprettet: '13‎.‎02‎.‎2017‎ ‎09‎:‎54‎:‎22',
    endret: '13‎.‎02‎.‎2017‎',
    person: {
      navn: 'Frida',
      alder: 44,
      personnummer: '0405198632231',
      erKvinne: true,
      erDod: false,
    },
  };

  it('skal kun vise søkefelt før søk er startet', () => {
    const searchFagsakFunction = sinon.spy();
    const wrapper = shallow(<FagsakSearch
      fagsaker={[]}
      fagsakOppgaver={[]}
      searchFagsakCallback={searchFagsakFunction}
      selectOppgaveCallback={sinon.spy()}
      searchResultReceived={false}
      selectFagsakCallback={sinon.spy()}
      searchStarted
      resetSearch={sinon.spy()}
    />);

    expect(wrapper.find(SearchForm)).toHaveLength(1);
    expect(wrapper.find(PersonInfo)).toHaveLength(0);
    expect(wrapper.find(FagsakList)).toHaveLength(0);
  });

  it('skal vise søkefelt og label for ingen søketreff når ingen fagsaker blir hentet', () => {
    const wrapper = shallow(<FagsakSearch
      fagsaker={[]}
      fagsakOppgaver={[]}
      searchFagsakCallback={sinon.spy()}
      selectOppgaveCallback={sinon.spy()}
      searchResultReceived
      selectFagsakCallback={sinon.spy()}
      searchStarted
      resetSearch={sinon.spy()}
    />);

    expect(wrapper.find(SearchForm)).toHaveLength(1);
    const labelComp = wrapper.find('Normaltekst');
    expect(labelComp).toHaveLength(1);
    expect(labelComp.find(FormattedMessage).prop('id')).toEqual('FagsakSearch.ZeroSearchResults');
  });

  it(
    'skal vise søkefelt og søketreff der person og to fagsaker blir vist',
    () => {
      const searchFagsakFunction = sinon.spy();
      const selectFagsakFunction = sinon.spy();
      const wrapper = shallow(<FagsakSearch
        fagsaker={[fagsak, fagsak]}
        fagsakOppgaver={[]}
        searchFagsakCallback={searchFagsakFunction}
        selectOppgaveCallback={sinon.spy()}
        searchResultReceived
        selectFagsakCallback={selectFagsakFunction}
        searchStarted
        resetSearch={sinon.spy()}
      />);

      expect(wrapper.find(SearchForm)).toHaveLength(1);

      const personComp = wrapper.find(PersonInfo);
      expect(personComp).toHaveLength(1);

      const fagsakListComp = wrapper.find(FagsakList);
      expect(fagsakListComp).toHaveLength(1);
      expect(fagsakListComp.prop('selectFagsakCallback')).toEqual(selectFagsakFunction);
    },
  );
});
