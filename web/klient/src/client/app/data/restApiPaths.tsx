import { RestApiConfigBuilder } from './rest-api';

export enum RestApiPathsKeys {
  KODEVERK = 'KODEVERK',
  NAV_ANSATT = 'NAV_ANSATT',
  FPSAK_URL = 'FPSAK_URL',
  FPTILBAKE_URL = 'FPTILBAKE_URL',
  AVDELINGER = 'AVDELINGER',
  SAKSLISTE = 'SAKSLISTE',
  RESERVERTE_OPPGAVER = 'RESERVERTE_OPPGAVER',
  OPPGAVER_TIL_BEHANDLING = 'OPPGAVER_TIL_BEHANDLING',
  OPPHEV_OPPGAVERESERVASJON = 'OPPHEV_OPPGAVERESERVASJON',
  FORLENG_OPPGAVERESERVASJON = 'FORLENG_OPPGAVERESERVASJON',
  ENDRE_OPPGAVERESERVASJON = 'ENDRE_OPPGAVERESERVASJON',
  FLYTT_RESERVASJON = 'FLYTT_RESERVASJON',
  RESERVER_OPPGAVE = 'RESERVER_OPPGAVE',
  FLYTT_RESERVASJON_SAKSBEHANDLER_SOK = 'FLYTT_RESERVASJON_SAKSBEHANDLER_SOK',
}

export const endpoints = new RestApiConfigBuilder()
  .withGet('/api/saksbehandler', RestApiPathsKeys.NAV_ANSATT)
  .withGet('/api/konfig/fpsak-url', RestApiPathsKeys.FPSAK_URL)
  .withGet('/api/konfig/fptilbake-url', RestApiPathsKeys.FPTILBAKE_URL)
  .withGet('/api/kodeverk', RestApiPathsKeys.KODEVERK)

  // Avdelingsleder
  .withGet('/api/avdelingsleder/avdelinger', RestApiPathsKeys.AVDELINGER)

  // Saksbehandler
  .withGet('/api/saksbehandler/saksliste', RestApiPathsKeys.SAKSLISTE)
  .withGet('/api/saksbehandler/oppgaver/reserverte', RestApiPathsKeys.RESERVERTE_OPPGAVER)
  .withAsyncGet('/api/saksbehandler/oppgaver', RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { maxPollingLimit: 1800 })
  .withPost('/api/saksbehandler/oppgaver/opphev', RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON)
  .withPost('/api/saksbehandler/oppgaver/forleng', RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON)
  .withPost('/api/saksbehandler/oppgaver/reservasjon/endre', RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON)
  .withPost('/api/saksbehandler/oppgaver/flytt', RestApiPathsKeys.FLYTT_RESERVASJON)
  .withPost('/api/saksbehandler/oppgaver/reserver', RestApiPathsKeys.RESERVER_OPPGAVE)
  .withPost('/api/saksbehandler/oppgaver/flytt/sok', RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK)

  .build();
