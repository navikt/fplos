import sinon from 'sinon';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import React from 'react';

import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import { ReservasjonerIndex } from './ReservasjonerIndex';
import ReservasjonerTabell from './components/ReservasjonerTabell';

describe('<ReservasjonerIndex>', () => {
  it('skal hente reservasjoner ved lasting av komponent og så vise dem i panel', () => {
    const fetchAvdelingensReservasjoner = sinon.spy();

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(async () => {
        const wrapper = shallow(<ReservasjonerIndex
          valgtAvdelingEnhet="2"
        />);

        expect(wrapper.find(ReservasjonerTabell)).to.have.length(1);
        expect(fetchAvdelingensReservasjoner.calledOnce).to.be.true;
        const { args } = fetchAvdelingensReservasjoner.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0]).to.eql('2');
      });
  });
});
