import React from 'react';
import sinon from 'sinon';
import { expect } from 'chai';
import { shallow } from 'enzyme';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import RestApiTestMocker from 'testHelpers/RestApiTestMocker';

import HeaderAvdelingListe from './HeaderAvdelingListe';

const navAnsatt = {
  navn: 'Per',
};

const avdelingerData = [{
  avdelingEnhet: '2323',
  navn: 'NAV Drammen',
  kreverKode6: false,
}, {
  avdelingEnhet: '4323',
  navn: 'NAV Oslo',
  kreverKode6: false,
}];

describe('<HeaderAvdelingListe>', () => {
  it('skal vise to avdelinger i header', () => {
    new RestApiTestMocker()
      .withGlobalRestCall(RestApiGlobalStatePathsKeys.AVDELINGER, avdelingerData)
      .withGlobalData(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt)
      .runTest(() => {
        const wrapper = shallow(<HeaderAvdelingListe
          erLenkePanelApent
          setLenkePanelApent={sinon.spy()}
          erAvdelingerPanelApent
          setAvdelingerPanelApent={sinon.spy()}
          setValgtAvdelingEnhet={sinon.spy()}
          valgtAvdelingEnhet={avdelingerData[0].avdelingEnhet}
        />);

        const boxedList = wrapper.last().prop('popperProps').children();

        expect(boxedList.props.items).to.eql([{
          name: `${avdelingerData[0].avdelingEnhet} ${avdelingerData[0].navn}`,
          selected: true,
        }, {
          name: `${avdelingerData[1].avdelingEnhet} ${avdelingerData[1].navn}`,
          selected: false,
        }]);
      });
  });

  it('skal velge ny avdeling', () => {
    new RestApiTestMocker()
      .withGlobalRestCall(RestApiGlobalStatePathsKeys.AVDELINGER, avdelingerData)
      .withGlobalData(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt)
      .runTest(() => {
        const setValgtAvdelingFn = sinon.spy();

        const wrapper = shallow(<HeaderAvdelingListe
          erLenkePanelApent
          setLenkePanelApent={sinon.spy()}
          erAvdelingerPanelApent
          setAvdelingerPanelApent={sinon.spy()}
          setValgtAvdelingEnhet={setValgtAvdelingFn}
          valgtAvdelingEnhet={avdelingerData[0].avdelingEnhet}
        />);

        const boxedList = wrapper.prop('popperProps').children();
        boxedList.props.onClick(1);
        expect(setValgtAvdelingFn.calledOnce).to.be.true;
        const { args } = setValgtAvdelingFn.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0]).to.eql('4323');
      });
  });
});
