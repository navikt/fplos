import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';

import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import { RestApiPathsKeys } from 'data/restApiPaths';
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
      antallBehandlinger: 1,
    }];


    new RestApiTestMocker()
      .withRestCallRunner(RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL, { data: 1 })
      .withRestCallRunner(RestApiPathsKeys.SAKSLISTER_FOR_AVDELING, { data: sakslister })
      .withRestCallRunner(RestApiPathsKeys.OPPRETT_NY_SAKSLISTE, { data: undefined })
      .runTest(() => {
        const wrapper = shallowWithIntl(<EndreSakslisterPanel.WrappedComponent
          intl={intl as IntlShape}
          setValgtSakslisteId={sinon.spy()}
          valgtAvdelingEnhet="test"
          avdelingensSaksbehandlere={[]}
          resetValgtSakslisteId={sinon.spy()}
        />);

        expect(wrapper.find(GjeldendeSakslisterTabell)).to.have.length(1);
        expect(wrapper.find(UtvalgskriterierForSakslisteForm)).to.have.length(0);
      });
  });

  it('skal vise editeringspanel når en har valgt tabellrad', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Espen Utvikler',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    }];

    new RestApiTestMocker()
      .withRestCallRunner(RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL, { data: 1 })
      .withRestCallRunner(RestApiPathsKeys.SAKSLISTER_FOR_AVDELING, { data: sakslister })
      .withRestCallRunner(RestApiPathsKeys.OPPRETT_NY_SAKSLISTE, { data: undefined })
      .runTest(() => {
        const wrapper = shallowWithIntl(<EndreSakslisterPanel.WrappedComponent
          intl={intl as IntlShape}
          setValgtSakslisteId={sinon.spy()}
          valgtSakslisteId={1}
          valgtAvdelingEnhet="test"
          avdelingensSaksbehandlere={[]}
          resetValgtSakslisteId={sinon.spy()}
        />);

        expect(wrapper.find(GjeldendeSakslisterTabell)).to.have.length(1);
        expect(wrapper.find(UtvalgskriterierForSakslisteForm)).to.have.length(1);
      });
  });
});
