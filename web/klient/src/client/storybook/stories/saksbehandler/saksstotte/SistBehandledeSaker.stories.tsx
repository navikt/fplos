import React from 'react';

import { RestApiPathsKeys } from 'data/restApiPaths';
import { RestDataProvider } from 'data/RestDataContext';
import { SistBehandledeSaker } from 'saksbehandler/saksstotte/components/SistBehandledeSaker';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiPathsKeys.FPSAK_URL]: {
    value: 'fpsak-url',
  },
  [RestApiPathsKeys.FPTILBAKE_URL]: {
    value: 'fptilbake-url',
  },
};

export default {
  title: 'saksbehandler/saksstotte/SistBehandledeSaker',
  component: SistBehandledeSaker,
  decorators: [
    withIntl,
    (getStory) => <RestDataProvider initialState={initialState as {[key in RestApiPathsKeys]: any}}>{getStory()}</RestDataProvider>,
  ],
};

export const skalViseIngenBehandlinger = () => (
  <SistBehandledeSaker
    sistBehandledeSaker={[]}
    hentFpsakInternBehandlingId={() => Promise.resolve({ payload: 1 })}
  />
);

export const skalViseSistBehandlendeSaker = () => (
  <SistBehandledeSaker
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
