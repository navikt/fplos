import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import EndreSakslisterPanel from './components/EndreSakslisterPanel';
import { EndreBehandlingskoerIndex } from './EndreBehandlingskoerIndex';

describe('<EndreBehandlingskoerIndex>', () => {
  it('skal hente sakslister når sakliste-fanen blir valgt', () => {
    const fetchAvdelingensSakslisterFn = sinon.spy();
    const fetchAvdelingensSaksbehandlereFn = sinon.spy();
    const wrapper = shallow(<EndreBehandlingskoerIndex
      fetchAvdelingensSakslister={fetchAvdelingensSakslisterFn}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={sinon.spy()}
      lagreSakslisteNavn={sinon.spy()}
      lagreSakslisteBehandlingstype={sinon.spy()}
      lagreSakslisteFagsakYtelseType={sinon.spy()}
      lagreSakslisteAndreKriterier={sinon.spy()}
      knyttSaksbehandlerTilSaksliste={sinon.spy()}
      fetchAvdelingensSaksbehandlere={fetchAvdelingensSaksbehandlereFn}
      valgtAvdelingEnhet="1"
      fetchAntallOppgaverForSaksliste={sinon.spy()}
      fetchAntallOppgaverForAvdeling={sinon.spy()}
    />);

    expect(wrapper.find(EndreSakslisterPanel)).to.have.length(1);
    expect(fetchAvdelingensSakslisterFn.calledOnce).to.be.true;
    expect(fetchAvdelingensSaksbehandlereFn.calledOnce).to.be.true;
  });
});
