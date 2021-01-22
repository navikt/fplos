import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import GjeldendeSakslisterTabell from './GjeldendeSakslisterTabell';
import UtvalgskriterierForSakslisteForm from './sakslisteForm/UtvalgskriterierForSakslisteForm';
import EndreSakslisterPanel from './EndreSakslisterPanel';

describe('<EndreSakslisterPanel>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it(
    'skal vise tabell for sakslister, men ikke editeringspanel når ingen tabellrad er valgt',
    () => {
      const sakslister = [{
        sakslisteId: 1,
        navn: 'Espen Utvikler',
        sistEndret: '2017-08-31',
        erTilBeslutter: false,
        erRegistrerPapirsoknad: false,
        saksbehandlerIdenter: [],
        antallBehandlinger: 1,
      }];

      requestApi.mock(RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL, 1);
      requestApi.mock(RestApiPathsKeys.SAKSLISTER_FOR_AVDELING, sakslister);
      requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSLISTE, undefined);

      const wrapper = shallowWithIntl(<EndreSakslisterPanel.WrappedComponent
        intl={intl as IntlShape}
        setValgtSakslisteId={sinon.spy()}
        valgtAvdelingEnhet="test"
        avdelingensSaksbehandlere={[]}
        resetValgtSakslisteId={sinon.spy()}
      />);

      expect(wrapper.find(GjeldendeSakslisterTabell)).to.have.length(1);
      expect(wrapper.find(UtvalgskriterierForSakslisteForm)).to.have.length(0);
    },
  );

  it('skal vise editeringspanel når en har valgt tabellrad', async () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Espen Utvikler',
      sistEndret: '2017-08-31',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    }];

    requestApi.mock(RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL, 1);
    requestApi.mock(RestApiPathsKeys.SAKSLISTER_FOR_AVDELING, sakslister);
    requestApi.mock(RestApiPathsKeys.OPPRETT_NY_SAKSLISTE, undefined);

    const wrapper = shallowWithIntl(<EndreSakslisterPanel.WrappedComponent
      intl={intl as IntlShape}
      setValgtSakslisteId={sinon.spy()}
      valgtSakslisteId={1}
      valgtAvdelingEnhet="test"
      avdelingensSaksbehandlere={[]}
      resetValgtSakslisteId={sinon.spy()}
    />);

    const tabell = wrapper.find(GjeldendeSakslisterTabell);
    expect(tabell).to.have.length(1);

    await tabell.prop('hentAvdelingensSakslister')({ avdelingEnhet: '1' });

    expect(wrapper.find(UtvalgskriterierForSakslisteForm)).to.have.length(1);
  });
});
