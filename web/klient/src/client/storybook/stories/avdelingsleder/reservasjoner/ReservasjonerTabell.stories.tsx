import React, { useState, useCallback } from 'react';
import { action } from '@storybook/addon-actions';

import ReservasjonerTabell from 'avdelingsleder/reservasjoner/components/ReservasjonerTabell';
import behandlingType from 'kodeverk/behandlingType';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/reservasjoner/ReservasjonerTabell',
  component: ReservasjonerTabell,
  decorators: [withIntl],
};

export const skalViseAtIngenReservasjonerBleFunnet = () => (
  <ReservasjonerTabell
    reservasjoner={[]}
    opphevReservasjon={action('button-click') as () => Promise<string>}
    endreOppgaveReservasjon={action('button-click') as () => Promise<string>}
    flyttReservasjon={action('button-click') as () => Promise<string>}
    finnSaksbehandler={action('button-click') as () => Promise<string>}
    nullstillSaksbehandler={action('button-click') as () => Promise<string>}
  />
);

export const skalViseTabellMedReservasjoner = () => {
  const [reservasjoner, fjernReservasjon] = useState([{
    reservertAvUid: 'wsfwer-sdsfd',
    reservertAvNavn: 'Espen Utvikler',
    reservertTilTidspunkt: '2020-01-10',
    oppgaveId: 1,
    oppgaveSaksNr: 122234,
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
  }, {
    reservertAvUid: 'gtfbrt-tbrtb',
    reservertAvNavn: 'Eirik Utvikler',
    reservertTilTidspunkt: '2020-01-01',
    oppgaveId: 2,
    oppgaveSaksNr: 23454,
    behandlingType: {
      kode: behandlingType.KLAGE,
      navn: 'Klage',
    },
  }]);

  const opphevReservasjon = useCallback((oppgaveId) => {
    fjernReservasjon((oldState) => oldState.filter((s) => s.oppgaveId !== oppgaveId));
  }, []);

  return (
    <ReservasjonerTabell
      reservasjoner={reservasjoner}
      opphevReservasjon={opphevReservasjon as () => Promise<string>}
      endreOppgaveReservasjon={action('button-click') as () => Promise<string>}
      flyttReservasjon={action('button-click') as () => Promise<string>}
      finnSaksbehandler={action('button-click') as () => Promise<string>}
      nullstillSaksbehandler={action('button-click') as () => Promise<string>}
    />
  );
};
