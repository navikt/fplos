import React from 'react';
import sinon from 'sinon';
import { expect } from 'chai';
import { shallow } from 'enzyme';

import * as useRestApiData from 'data/rest-api-hooks/useGlobalStateRestApiData';
import * as useRestApi from 'data/rest-api-hooks/useRestApi';

import HeaderAvdelingListe from './HeaderAvdelingListe';

const navAnsatt = {
  navn: 'Per',
};

const avdelingerData = {
  state: useRestApi.ApiState.SUCCESS,
  data: [{
    avdelingEnhet: '2323',
    navn: 'NAV Drammen',
    kreverKode6: false,
  }, {
    avdelingEnhet: '4323',
    navn: 'NAV Oslo',
    kreverKode6: false,
  }],
};

describe('<HeaderAvdelingListe>', () => {
  let contextStubRequest;
  let contextStubData;
  before(() => {
    contextStubRequest = sinon.stub(useRestApi, 'default').callsFake(() => avdelingerData);
    contextStubData = sinon.stub(useRestApiData, 'default').callsFake(() => navAnsatt);
  });

  after(() => {
    contextStubRequest.restore();
    contextStubData.restore();
  });

  it('skal vise to avdelinger i header', () => {
    const wrapper = shallow(<HeaderAvdelingListe
      erLenkePanelApent
      setLenkePanelApent={sinon.spy()}
      erAvdelingerPanelApent
      setAvdelingerPanelApent={sinon.spy()}
      setValgtAvdelingEnhet={sinon.spy()}
      valgtAvdelingEnhet={avdelingerData.data[0].avdelingEnhet}
    />);

    const boxedList = wrapper.last().prop('popperProps').children();

    expect(boxedList.props.items).to.eql([{
      name: `${avdelingerData.data[0].avdelingEnhet} ${avdelingerData.data[0].navn}`,
      selected: true,
    }, {
      name: `${avdelingerData.data[1].avdelingEnhet} ${avdelingerData.data[1].navn}`,
      selected: false,
    }]);
  });

  it('skal velge ny avdeling', () => {
    const setValgtAvdelingFn = sinon.spy();

    const wrapper = shallow(<HeaderAvdelingListe
      erLenkePanelApent
      setLenkePanelApent={sinon.spy()}
      erAvdelingerPanelApent
      setAvdelingerPanelApent={sinon.spy()}
      setValgtAvdelingEnhet={setValgtAvdelingFn}
      valgtAvdelingEnhet={avdelingerData.data[0].avdelingEnhet}
    />);

    const boxedList = wrapper.prop('popperProps').children();
    boxedList.props.onClick(1);
    expect(setValgtAvdelingFn.calledOnce).to.be.true;
    const { args } = setValgtAvdelingFn.getCalls()[0];
    expect(args).to.have.length(1);
    expect(args[0]).to.eql('4323');
  });
});
