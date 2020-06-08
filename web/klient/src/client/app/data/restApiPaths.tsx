import { RestApiConfigBuilder } from './rest-api';

export enum RestApiGlobalStatePathsKeys {
  KODEVERK = 'KODEVERK',
  NAV_ANSATT = 'NAV_ANSATT',
  FPSAK_URL = 'FPSAK_URL',
  FPTILBAKE_URL = 'FPTILBAKE_URL',
  AVDELINGER = 'AVDELINGER',
}

export enum RestApiPathsKeys {
  SEARCH_FAGSAK = 'SEARCH_FAGSAK',
  OPPGAVER_FOR_FAGSAKER = 'OPPGAVER_FOR_FAGSAKER',
  SAKSLISTE = 'SAKSLISTE',
  RESERVERTE_OPPGAVER = 'RESERVERTE_OPPGAVER',
  OPPGAVER_TIL_BEHANDLING = 'OPPGAVER_TIL_BEHANDLING',
  OPPHEV_OPPGAVERESERVASJON = 'OPPHEV_OPPGAVERESERVASJON',
  FORLENG_OPPGAVERESERVASJON = 'FORLENG_OPPGAVERESERVASJON',
  ENDRE_OPPGAVERESERVASJON = 'ENDRE_OPPGAVERESERVASJON',
  FLYTT_RESERVASJON = 'FLYTT_RESERVASJON',
  RESERVER_OPPGAVE = 'RESERVER_OPPGAVE',
  FLYTT_RESERVASJON_SAKSBEHANDLER_SOK = 'FLYTT_RESERVASJON_SAKSBEHANDLER_SOK',
  HENT_RESERVASJONSSTATUS = 'HENT_RESERVASJONSSTATUS',
  FPSAK_BEHANDLING_ID = 'FPSAK_BEHANDLING_ID',
  HENT_NYE_OG_FERDIGSTILTE_OPPGAVER = 'HENT_NYE_OG_FERDIGSTILTE_OPPGAVER',
  SAKSLISTE_SAKSBEHANDLERE = 'SAKSLISTE_SAKSBEHANDLERE',
  BEHANDLINGSKO_OPPGAVE_ANTALL = 'BEHANDLINGSKO_OPPGAVE_ANTALL',
  BEHANDLEDE_OPPGAVER = 'BEHANDLEDE_OPPGAVER',
}

export const endpoints = new RestApiConfigBuilder()
  .withGet('/api/saksbehandler', RestApiGlobalStatePathsKeys.NAV_ANSATT)
  .withGet('/api/konfig/fpsak-url', RestApiGlobalStatePathsKeys.FPSAK_URL)
  .withGet('/api/konfig/fptilbake-url', RestApiGlobalStatePathsKeys.FPTILBAKE_URL)
  .withGet('/api/kodeverk', RestApiGlobalStatePathsKeys.KODEVERK)


  // Avdelingsleder
  .withGet('/api/avdelingsleder/avdelinger', RestApiGlobalStatePathsKeys.AVDELINGER)

  // Saksbehandler
  .withPost('/api/fagsak/sok', RestApiPathsKeys.SEARCH_FAGSAK)
  .withGet('/api/saksbehandler/saksliste', RestApiPathsKeys.SAKSLISTE)
  .withGet('/api/saksbehandler/oppgaver/reserverte', RestApiPathsKeys.RESERVERTE_OPPGAVER)
  .withAsyncGet('/api/saksbehandler/oppgaver', RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { maxPollingLimit: 1800 })
  .withPost('/api/saksbehandler/oppgaver/opphev', RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON)
  .withPost('/api/saksbehandler/oppgaver/forleng', RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON)
  .withPost('/api/saksbehandler/oppgaver/reservasjon/endre', RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON)
  .withPost('/api/saksbehandler/oppgaver/flytt', RestApiPathsKeys.FLYTT_RESERVASJON)
  .withPost('/api/saksbehandler/oppgaver/reserver', RestApiPathsKeys.RESERVER_OPPGAVE)
  .withPost('/api/saksbehandler/oppgaver/flytt/sok', RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK)
  .withGet('/api/saksbehandler/oppgaver/oppgaver-for-fagsaker', RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER)
  .withGet('/api/saksbehandler/oppgaver/reservasjon-status', RestApiPathsKeys.HENT_RESERVASJONSSTATUS)
  .withGet('/api/saksbehandler/nokkeltall/nye-og-ferdigstilte-oppgaver', RestApiPathsKeys.HENT_NYE_OG_FERDIGSTILTE_OPPGAVER)
  .withGet('/api/fpsak/behandlingId', RestApiPathsKeys.FPSAK_BEHANDLING_ID)
  .withGet('/api/saksbehandler/saksliste/saksbehandlere', RestApiPathsKeys.SAKSLISTE_SAKSBEHANDLERE)
  .withGet('/api/saksbehandler/oppgaver/antall', RestApiPathsKeys.BEHANDLINGSKO_OPPGAVE_ANTALL)
  .withGet('/api/saksbehandler/oppgaver/behandlede', RestApiPathsKeys.BEHANDLEDE_OPPGAVER)

  .build();
