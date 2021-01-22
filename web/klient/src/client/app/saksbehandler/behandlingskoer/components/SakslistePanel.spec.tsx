import React from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';

import SakslisteVelgerForm from './SakslisteVelgerForm';
import OppgaverTabell from './OppgaverTabell';

import SakslistePanel from './SakslistePanel';

describe('<SakslistePanel>', () => {
  it('skal vise kriterievelger og liste over neste saker', () => {
    const sakslister = [];
    const wrapper = shallow(<SakslistePanel
      sakslister={sakslister}
      reserverOppgave={sinon.spy()}
      setValgtSakslisteId={sinon.spy()}
      valgtSakslisteId={1}
    />);

    expect(wrapper.find(SakslisteVelgerForm)).toHaveLength(1);
    expect(wrapper.find(OppgaverTabell)).toHaveLength(1);
  });
});
