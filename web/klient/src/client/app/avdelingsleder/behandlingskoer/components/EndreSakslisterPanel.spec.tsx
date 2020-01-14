import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import GjeldendeSakslisterTabell from './GjeldendeSakslisterTabell';
import UtvalgskriterierForSakslisteForm from './sakslisteForm/UtvalgskriterierForSakslisteForm';
import EndreSakslisterPanel from './EndreSakslisterPanel';

describe('<EndreSakslisterPanel>', () => {
  it('skal vise tabell for sakslister, men ikke editeringspanel når ingen tabellrad er valgt', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Espen Utvikler',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
    }];

    const wrapper = shallow(<EndreSakslisterPanel
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={sinon.spy()}
      lagreSakslisteNavn={sinon.spy()}
      lagreSakslisteBehandlingstype={sinon.spy()}
      lagreSakslisteFagsakYtelseType={sinon.spy()}
      lagreSakslisteSortering={sinon.spy()}
      lagreSakslisteAndreKriterier={sinon.spy()}
      knyttSaksbehandlerTilSaksliste={sinon.spy()}
      hentSakslistensSaksbehandlere={sinon.spy()}
      hentAntallOppgaverForSaksliste={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
      showSaksbehandlerPanel
    />);

    expect(wrapper.find(GjeldendeSakslisterTabell)).to.have.length(1);
    expect(wrapper.find(UtvalgskriterierForSakslisteForm)).to.have.length(0);
  });

  it('skal vise editeringspanel når en har valgt tabellrad', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Espen Utvikler',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
    }];

    const wrapper = shallow(<EndreSakslisterPanel
      sakslister={sakslister}
      setValgtSakslisteId={sinon.spy()}
      lagNySaksliste={sinon.spy()}
      fjernSaksliste={sinon.spy()}
      lagreSakslisteNavn={sinon.spy()}
      lagreSakslisteBehandlingstype={sinon.spy()}
      lagreSakslisteFagsakYtelseType={sinon.spy()}
      lagreSakslisteSortering={sinon.spy()}
      lagreSakslisteAndreKriterier={sinon.spy()}
      valgtSakslisteId={1}
      knyttSaksbehandlerTilSaksliste={sinon.spy()}
      hentSakslistensSaksbehandlere={sinon.spy()}
      hentAntallOppgaverForSaksliste={sinon.spy()}
      hentAntallOppgaverForAvdeling={sinon.spy()}
      showSaksbehandlerPanel
    />);

    expect(wrapper.find(GjeldendeSakslisterTabell)).to.have.length(1);
    expect(wrapper.find(UtvalgskriterierForSakslisteForm)).to.have.length(1);
  });
});
