import React from 'react';
import sinon from 'sinon';
import { IntlShape, FormattedMessage } from 'react-intl';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import BehandlingStatus from 'kodeverk/behandlingStatus';
import BehandlingType from 'kodeverk/behandlingType';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { OppgaveReservasjonForlengetModal } from './OppgaveReservasjonForlengetModal';

describe('<OppgaveReservasjonForlengetModal>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  const oppgave = {
    id: 1,
    status: {
      erReservert: false,
      reservertTilTidspunkt: '2017-08-02T00:54:25.455',
    },
    saksnummer: 1,
    behandlingId: 'TEST-EKSTERN',
    personnummer: '1234567',
    navn: 'Espen Utvikler',
    system: 'FPSAK',
    behandlingstype: {
      kode: BehandlingType.FORSTEGANGSSOKNAD,
      navn: '',
    },
    opprettetTidspunkt: '2017-01-01',
    behandlingsfrist: '2017-01-01',
    erTilSaksbehandling: true,
    fagsakYtelseType: {
      kode: FagsakYtelseType.ENGANGSSTONAD,
      navn: '',
    },
    behandlingStatus: {
      kode: BehandlingStatus.OPPRETTET,
      navn: '',
    },
    href: '',
  };

  it(
    'skal rendre modal for å gi tilbakemelding om at reservasjon er forlenget',
    () => {
      const wrapper = shallowWithIntl(
        <OppgaveReservasjonForlengetModal
          intl={intl as IntlShape}
          oppgave={oppgave}
          showModal
          closeModal={sinon.spy()}
        />,
      );

      const messages = wrapper.find(FormattedMessage);
      expect(messages).toHaveLength(2);

      expect(messages.last().prop('values')).toEqual({
        date: '02.08.2017',
        time: '00:54',
      });
    },
  );
});
