import React from 'react';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { SletteSakslisteModal } from './SletteSakslisteModal';

describe('<SletteSakslisteModal>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it(
    'skal vise slette-modal med knapper for om en vil slette eller ikke',
    () => {
      const saksliste = {
        sakslisteId: 1,
        navn: 'Nyansatte',
        sistEndret: '2017-01-01',
        erTilBeslutter: false,
        erRegistrerPapirsoknad: false,
        saksbehandlerIdenter: [],
        antallBehandlinger: 1,
      };

      const wrapper = shallowWithIntl(<SletteSakslisteModal
        intl={intl as IntlShape}
        valgtSaksliste={saksliste}
        cancel={sinon.spy()}
        submit={sinon.spy()}
      />);

      expect(wrapper.find(Hovedknapp)).toHaveLength(1);
      expect(wrapper.find(Knapp)).toHaveLength(1);
    },
  );

  it('skal kjøre slettefunksjon ved trykk på Ja-knapp', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-01-01',
      erTilBeslutter: false,
      erRegistrerPapirsoknad: false,
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    };
    const submitFn = sinon.spy();

    const wrapper = shallowWithIntl(<SletteSakslisteModal
      intl={intl as IntlShape}
      valgtSaksliste={saksliste}
      cancel={sinon.spy()}
      submit={submitFn}
    />);

    const sletteknapp = wrapper.find(Hovedknapp);
    expect(sletteknapp).toHaveLength(1);

    const slettFn = sletteknapp.prop('onClick') as () => void;
    slettFn();

    expect(submitFn.calledOnce).toBe(true);
    const { args } = submitFn.getCalls()[0];
    expect(args).toHaveLength(1);
    expect(args[0]).toEqual(saksliste);
  });
});
