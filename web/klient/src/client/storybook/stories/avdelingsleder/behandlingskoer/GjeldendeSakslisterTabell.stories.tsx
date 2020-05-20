import React, { useState } from 'react';
import { action } from '@storybook/addon-actions';

import { GjeldendeSakslisterTabell } from 'avdelingsleder/behandlingskoer/components/GjeldendeSakslisterTabell';
import Saksliste from 'avdelingsleder/behandlingskoer/sakslisteTsType';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/behandlingskoer/GjeldendeSakslisterTabell',
  component: GjeldendeSakslisterTabell,
  decorators: [withIntl],
};

export const skalVisePanelNårDetIkkeFinnesBehandlingskøer = () => {
  const [sakslister, setSaksliste] = useState([]);
  return (
    <GjeldendeSakslisterTabell
      sakslister={sakslister}
      setValgtSakslisteId={action('button-click')}
      lagNySaksliste={() => setSaksliste([{
        sakslisteId: 1,
        navn: 'Ny liste',
        sistEndret: '2020-01-01',
        saksbehandlerIdenter: [],
        antallBehandlinger: 1,
      }])}
      fjernSaksliste={action('button-click')}
      behandlingTyper={[]}
      fagsakYtelseTyper={[]}
      valgtAvdelingEnhet=""
      hentAvdelingensSakslister={action('button-click') as () => Saksliste[]}
      hentAntallOppgaverForAvdeling={action('button-click') as () => Promise<string>}
    />
  );
};

export const skalVisePanelNårDetFinnesEnBehandlingskø = () => {
  const [sakslister, setSaksliste] = useState([{
    sakslisteId: 1,
    navn: 'Saksliste 1',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: ['R23233'],
    antallBehandlinger: 1,
  }]);
  return (
    <GjeldendeSakslisterTabell
      sakslister={sakslister}
      valgtSakslisteId={1}
      setValgtSakslisteId={action('button-click')}
      lagNySaksliste={() => setSaksliste([{
        sakslisteId: 2,
        navn: 'Ny liste',
        sistEndret: '2020-01-01',
        saksbehandlerIdenter: [],
        antallBehandlinger: 1,
      }])}
      fjernSaksliste={action('button-click')}
      behandlingTyper={[]}
      fagsakYtelseTyper={[]}
      valgtAvdelingEnhet=""
      hentAvdelingensSakslister={action('button-click') as () => Saksliste[]}
      hentAntallOppgaverForAvdeling={action('button-click') as () => Promise<string>}
      oppgaverForAvdeling={23}
    />
  );
};
