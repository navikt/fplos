import { RestApiConfigBuilder, createRequestApi } from './rest-api';

export enum RestApiGlobalStatePathsKeys {
  KODEVERK = 'KODEVERK',
  NAV_ANSATT = 'NAV_ANSATT',
  FPSAK_URL = 'FPSAK_URL',
  FPTILBAKE_URL = 'FPTILBAKE_URL',
  AVDELINGER = 'AVDELINGER',
  DRIFTSMELDINGER = 'DRIFTSMELDINGER',
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
  SAKSLISTER_FOR_AVDELING = 'SAKSLISTER_FOR_AVDELING',
  SAKSBEHANDLERE_FOR_AVDELING = 'SAKSBEHANDLERE_FOR_AVDELING',
  OPPGAVE_AVDELING_ANTALL = 'OPPGAVE_AVDELING_ANTALL',
  OPPGAVE_ANTALL = 'OPPGAVE_ANTALL',
  OPPRETT_NY_SAKSLISTE = 'OPPRETT_NY_SAKSLISTE',
  SLETT_SAKSLISTE = 'SLETT_SAKSLISTE',
  LAGRE_SAKSLISTE_NAVN = 'LAGRE_SAKSLISTE_NAVN',
  LAGRE_SAKSLISTE_BEHANDLINGSTYPE = 'LAGRE_SAKSLISTE_BEHANDLINGSTYPE',
  LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE = 'LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE',
  LAGRE_SAKSLISTE_SORTERING = 'LAGRE_SAKSLISTE_SORTERING',
  LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE = 'LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE',
  LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO = 'LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO',
  LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DAGER = 'LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DAGER',
  LAGRE_SAKSLISTE_ANDRE_KRITERIER = 'LAGRE_SAKSLISTE_ANDRE_KRITERIER',
  LAGRE_SAKSLISTE_SAKSBEHANDLER = 'LAGRE_SAKSLISTE_SAKSBEHANDLER',
  SAKSBEHANDLER_SOK = 'SAKSBEHANDLER_SOK',
  OPPRETT_NY_SAKSBEHANDLER = 'OPPRETT_NY_SAKSBEHANDLER',
  SLETT_SAKSBEHANDLER = 'SLETT_SAKSBEHANDLER',
  HENT_OPPGAVER_FOR_AVDELING = 'HENT_OPPGAVER_FOR_AVDELING',
  HENT_OPPGAVER_PER_DATO = 'HENT_OPPGAVER_PER_DATO',
  HENT_OPPGAVER_PER_FORSTE_STONADSDAG = 'HENT_OPPGAVER_PER_FORSTE_STONADSDAG',
  HENT_OPPGAVER_MANUELT_PA_VENT = 'HENT_OPPGAVER_MANUELT_PA_VENT',
  RESERVASJONER_FOR_AVDELING = 'RESERVASJONER_FOR_AVDELING',
  AVDELINGSLEDER_OPPHEVER_RESERVASJON = 'AVDELINGSLEDER_OPPHEVER_RESERVASJON',
}

const CONTEXT_PATH = 'fplos';

export const endpoints = new RestApiConfigBuilder(CONTEXT_PATH)
  .withGet('/api/saksbehandler', RestApiGlobalStatePathsKeys.NAV_ANSATT)
  .withGet('/api/konfig/fpsak-url', RestApiGlobalStatePathsKeys.FPSAK_URL)
  .withGet('/api/konfig/fptilbake-url', RestApiGlobalStatePathsKeys.FPTILBAKE_URL)
  .withGet('/api/kodeverk', RestApiGlobalStatePathsKeys.KODEVERK)
  .withGet('/api/driftsmeldinger', RestApiGlobalStatePathsKeys.DRIFTSMELDINGER)

  // Avdelingsleder
  .withGet('/api/avdelingsleder/avdelinger', RestApiGlobalStatePathsKeys.AVDELINGER)
  .withGet('/api/avdelingsleder/sakslister', RestApiPathsKeys.SAKSLISTER_FOR_AVDELING)
  .withGet('/api/avdelingsleder/saksbehandlere', RestApiPathsKeys.SAKSBEHANDLERE_FOR_AVDELING)
  .withGet('/api/avdelingsleder/oppgaver/avdelingantall', RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL)
  .withGet('/api/avdelingsleder/oppgaver/antall', RestApiPathsKeys.OPPGAVE_ANTALL)
  .withPost('/api/avdelingsleder/sakslister', RestApiPathsKeys.OPPRETT_NY_SAKSLISTE)
  .withPost('/api/avdelingsleder/sakslister/slett', RestApiPathsKeys.SLETT_SAKSLISTE)
  .withPost('/api/avdelingsleder/sakslister/navn', RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN)
  .withPost('/api/avdelingsleder/sakslister/behandlingstype', RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE)
  .withPost('/api/avdelingsleder/sakslister/ytelsetype', RestApiPathsKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE)
  .withPost('/api/avdelingsleder/sakslister/sortering', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING)
  .withPost('/api/avdelingsleder/sakslister/sortering-tidsintervall-type', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE)
  .withPost('/api/avdelingsleder/sakslister/sortering-tidsintervall-dato', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO)
  .withPost('/api/avdelingsleder/sakslister/sortering-tidsintervall-dager', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DAGER)
  .withPost('/api/avdelingsleder/sakslister/andre-kriterier', RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER)
  .withPost('/api/avdelingsleder/sakslister/saksbehandler', RestApiPathsKeys.LAGRE_SAKSLISTE_SAKSBEHANDLER)
  .withPost('/api/avdelingsleder/saksbehandlere/sok', RestApiPathsKeys.SAKSBEHANDLER_SOK)
  .withPost('/api/avdelingsleder/saksbehandlere', RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER)
  .withPost('/api/avdelingsleder/saksbehandlere/slett', RestApiPathsKeys.SLETT_SAKSBEHANDLER)
  .withGet('/api/avdelingsleder/nokkeltall/behandlinger-under-arbeid', RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING)
  .withGet('/api/avdelingsleder/nokkeltall/behandlinger-under-arbeid-historikk', RestApiPathsKeys.HENT_OPPGAVER_PER_DATO)
  .withGet('/api/avdelingsleder/nokkeltall/behandlinger-manuelt-vent-historikk', RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT)
  .withGet('/api/avdelingsleder/nokkeltall/behandlinger-forste-stonadsdag', RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG)
  .withGet('/api/avdelingsleder/reservasjoner', RestApiPathsKeys.RESERVASJONER_FOR_AVDELING)
  .withPost('/api/avdelingsleder/reservasjoner/opphev', RestApiPathsKeys.AVDELINGSLEDER_OPPHEVER_RESERVASJON)

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

export const requestApi = createRequestApi(endpoints);
