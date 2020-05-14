import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import GjeldendeSakslisterTabell from './GjeldendeSakslisterTabell';
import UtvalgskriterierForSakslisteForm from './sakslisteForm/UtvalgskriterierForSakslisteForm';
import EndreSakslisterPanel from './EndreSakslisterPanel';

describe('<EndreSakslisterPanel>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it('skal vise tabell for sakslister, men ikke editeringspanel når ingen tabellrad er valgt', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Espen Utvikler',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
    }];

    const wrapper = shallowWithIntl(<EndreSakslisterPanel.WrappedComponent
      intl={intl as IntlShape}
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

    const wrapper = shallowWithIntl(<EndreSakslisterPanel.WrappedComponent
      intl={intl as IntlShape}
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
