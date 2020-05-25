import React from 'react';

import { SistBehandledeSaker } from 'saksbehandler/saksstotte/components/SistBehandledeSaker';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/saksstotte/SistBehandledeSaker',
  component: SistBehandledeSaker,
  decorators: [withIntl],
};

export const skalViseIngenBehandlinger = () => (
  <SistBehandledeSaker
    fpsakUrl=""
    fptilbakeUrl=""
    sistBehandledeSaker={[]}
    hentFpsakInternBehandlingId={() => Promise.resolve({ payload: 1 })}
  />
);

export const skalViseSistBehandlendeSaker = () => (
  <SistBehandledeSaker
    fpsakUrl=""
    fptilbakeUrl=""
    sistBehandledeSaker={[{
      id: 1,
      status: {
        erReservert: false,
        flyttetReservasjon: {
          tidspunkt: '2019-02-02',
          uid: '23423',
          navn: 'Espen Utvikler',
          begrunnelse: 'Flyttet',
        },
      },
      saksnummer: 1234,
      personnummer: '334342323',
      navn: 'Espen Utvikler',
      system: 'SAK',
      behandlingstype: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      behandlingStatus: {
        kode: behandlingStatus.BEHANDLING_UTREDES,
        navn: 'Behandling utredes',
      },
      opprettetTidspunkt: '2019-01-01',
      behandlingsfrist: '2019-01-01',
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'Foreldrepenger',
      },
      erTilSaksbehandling: true,
      behandlingId: '1',
      href: '',
    }]}
    hentFpsakInternBehandlingId={() => Promise.resolve({ payload: 1 })}
  />
);
