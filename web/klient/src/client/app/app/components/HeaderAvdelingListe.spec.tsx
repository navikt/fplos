import React from 'react';
import sinon from 'sinon';
import { shallow } from 'enzyme';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';

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
    requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, avdelingerData);
    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);

    const wrapper = shallow(<HeaderAvdelingListe
      erLenkePanelApent
      setLenkePanelApent={sinon.spy()}
      erAvdelingerPanelApent
      setAvdelingerPanelApent={sinon.spy()}
      setValgtAvdelingEnhet={sinon.spy()}
      valgtAvdelingEnhet={avdelingerData[0].avdelingEnhet}
    />);

    const boxedList = wrapper.last().prop('popperProps').children();

    expect(boxedList.props.items).toEqual([{
      name: `${avdelingerData[0].avdelingEnhet} ${avdelingerData[0].navn}`,
      selected: true,
    }, {
      name: `${avdelingerData[1].avdelingEnhet} ${avdelingerData[1].navn}`,
      selected: false,
    }]);
  });

  it('skal velge ny avdeling', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, avdelingerData);
    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);

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
    expect(setValgtAvdelingFn.calledOnce).toBe(true);
    const { args } = setValgtAvdelingFn.getCalls()[0];
    expect(args).toHaveLength(1);
    expect(args[0]).toEqual('4323');
  });
});
