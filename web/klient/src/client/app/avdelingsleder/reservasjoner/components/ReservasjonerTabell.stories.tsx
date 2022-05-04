import React, { useState, useCallback } from 'react';
import { action } from '@storybook/addon-actions';

import ReservasjonerTabell from 'avdelingsleder/reservasjoner/components/ReservasjonerTabell';
import behandlingType from 'kodeverk/behandlingType';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

import withIntl from 'storybookUtils/decorators/withIntl';

export default {
  title: 'avdelingsleder/reservasjoner/ReservasjonerTabell',
  component: ReservasjonerTabell,
  decorators: [withIntl],
};

export const ViseAtIngenReservasjonerBleFunnet = () => (
  <ReservasjonerTabell
    reservasjoner={[]}
    opphevReservasjon={action('button-click') as () => Promise<string>}
    hentAvdelingensReservasjoner={action('button-click')}
    alleKodeverk={alleKodeverk as any}
  />
);

export const VisTabellMedReservasjoner = () => {
  const [reservasjoner, fjernReservasjon] = useState([{
    reservertAvUid: 'wsfwer-sdsfd',
    reservertAvNavn: 'Espen Utvikler',
    reservertTilTidspunkt: '2020-01-10',
    oppgaveId: 1,
    oppgaveSaksNr: 122234,
    behandlingType: behandlingType.FORSTEGANGSSOKNAD,
  }, {
    reservertAvUid: 'gtfbrt-tbrtb',
    reservertAvNavn: 'Eirik Utvikler',
    reservertTilTidspunkt: '2020-01-01',
    oppgaveId: 2,
    oppgaveSaksNr: 23454,
    behandlingType: behandlingType.KLAGE,
  }]);

  const opphevReservasjon = useCallback((oppgaveId) => {
    fjernReservasjon((oldState) => oldState.filter((s) => s.oppgaveId !== oppgaveId));
  }, []);

  return (
    <ReservasjonerTabell
      reservasjoner={reservasjoner}
      opphevReservasjon={opphevReservasjon as () => Promise<string>}
      hentAvdelingensReservasjoner={action('button-click')}
      alleKodeverk={alleKodeverk as any}
    />
  );
};
