import React from 'react';
import { Undertekst } from 'nav-frontend-typografi';

import { shallow } from 'enzyme';
import DriftsmeldingPanel from './DriftsmeldingPanel';

describe('<DriftsmeldingPanel>', () => {
  it('skal ikke vises når det ikke finnes driftsmeldinger', () => {
    const wrapper = shallow(<DriftsmeldingPanel
      driftsmeldinger={[]}
    />);

    const div = wrapper.find(Undertekst);
    expect(div).toHaveLength(0);
  });

  it('skal vise driftsmeldinger når det finnes driftsmeldinger', () => {
    const melding = {
      id: '1',
      melding: 'Dette er driftsmeldingen',
      aktiv: true,
      opprettet: '2020-10-10',
    };

    const wrapper = shallow(<DriftsmeldingPanel
      driftsmeldinger={[melding]}
    />);

    const div = wrapper.find(Undertekst);
    expect(div).toHaveLength(1);
    expect(div.at(0).childAt(0).text()).toEqual('Dette er driftsmeldingen');
  });
});