import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import sinon from 'sinon';

import SaksbehandlerePanel from './SaksbehandlerePanel';
import LeggTilSaksbehandlerForm from './LeggTilSaksbehandlerForm';
import SaksbehandlereTabell from './SaksbehandlereTabell';

describe('<SaksbehandlerePanel>', () => {
  it('skal vise tabell for saksbehandlere og panel for å legge til flere', () => {
    const wrapper = shallow(<SaksbehandlerePanel
      saksbehandlere={[]}
      avdelingensSaksbehandlere={[]}
      hentAvdelingensSaksbehandlere={sinon.spy()}
      valgtAvdelingEnhet="test"
    />);

    expect(wrapper.find(LeggTilSaksbehandlerForm)).to.have.length(1);
    expect(wrapper.find(SaksbehandlereTabell)).to.have.length(1);
  });
});
