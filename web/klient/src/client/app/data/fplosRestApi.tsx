import { RestApiConfigBuilder, createRequestApi } from './rest-api';
import { RestApiHooks } from './rest-api-hooks';

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
  LAGRE_SAKSLISTE_SORTERING_INTERVALL = 'LAGRE_SAKSLISTE_SORTERING_INTERVALL',
  LAGRE_SAKSLISTE_ANDRE_KRITERIER = 'LAGRE_SAKSLISTE_ANDRE_KRITERIER',
  LAGRE_SAKSLISTE_SAKSBEHANDLER = 'LAGRE_SAKSLISTE_SAKSBEHANDLER',
  SAKSBEHANDLER_SOK = 'SAKSBEHANDLER_SOK',
  OPPRETT_NY_SAKSBEHANDLER = 'OPPRETT_NY_SAKSBEHANDLER',
  SLETT_SAKSBEHANDLER = 'SLETT_SAKSBEHANDLER',
  HENT_OPPGAVER_FOR_AVDELING = 'HENT_OPPGAVER_FOR_AVDELING',
  HENT_OPPGAVER_PER_DATO = 'HENT_OPPGAVER_PER_DATO',
  HENT_OPPGAVER_PER_FORSTE_STONADSDAG = 'HENT_OPPGAVER_PER_FORSTE_STONADSDAG',
  HENT_OPPGAVER_MANUELT_PA_VENT = 'HENT_OPPGAVER_MANUELT_PA_VENT',
  HENT_OPPGAVER_APNE_ELLER_PA_VENT = 'HENT_OPPGAVER_APNE_ELLER_PA_VENT',
  RESERVASJONER_FOR_AVDELING = 'RESERVASJONER_FOR_AVDELING',
  AVDELINGSLEDER_OPPHEVER_RESERVASJON = 'AVDELINGSLEDER_OPPHEVER_RESERVASJON',
}

export const endpoints = new RestApiConfigBuilder()
  .withGet('/fplos/api/saksbehandler', RestApiGlobalStatePathsKeys.NAV_ANSATT)
  .withGet('/fplos/api/konfig/fpsak-url', RestApiGlobalStatePathsKeys.FPSAK_URL)
  .withGet('/fplos/api/konfig/fptilbake-url', RestApiGlobalStatePathsKeys.FPTILBAKE_URL)
  .withGet('/fplos/api/kodeverk', RestApiGlobalStatePathsKeys.KODEVERK)
  .withGet('/fplos/api/driftsmeldinger', RestApiGlobalStatePathsKeys.DRIFTSMELDINGER)

  // Avdelingsleder
  .withGet('/fplos/api/avdelingsleder/avdelinger', RestApiGlobalStatePathsKeys.AVDELINGER)
  .withGet('/fplos/api/avdelingsleder/sakslister', RestApiPathsKeys.SAKSLISTER_FOR_AVDELING)
  .withGet('/fplos/api/avdelingsleder/saksbehandlere', RestApiPathsKeys.SAKSBEHANDLERE_FOR_AVDELING)
  .withGet('/fplos/api/avdelingsleder/oppgaver/avdelingantall', RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL)
  .withGet('/fplos/api/avdelingsleder/oppgaver/antall', RestApiPathsKeys.OPPGAVE_ANTALL)
  .withPost('/fplos/api/avdelingsleder/sakslister', RestApiPathsKeys.OPPRETT_NY_SAKSLISTE)
  .withPost('/fplos/api/avdelingsleder/sakslister/slett', RestApiPathsKeys.SLETT_SAKSLISTE)
  .withPost('/fplos/api/avdelingsleder/sakslister/navn', RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN)
  .withPost('/fplos/api/avdelingsleder/sakslister/behandlingstype', RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE)
  .withPost('/fplos/api/avdelingsleder/sakslister/ytelsetype', RestApiPathsKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE)
  .withPost('/fplos/api/avdelingsleder/sakslister/sortering', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING)
  .withPost('/fplos/api/avdelingsleder/sakslister/sortering-tidsintervall-type', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE)
  .withPost('/fplos/api/avdelingsleder/sakslister/sortering-tidsintervall-dato', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO)
  .withPost('/fplos/api/avdelingsleder/sakslister/sortering-numerisk-intervall', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_INTERVALL)
  .withPost('/fplos/api/avdelingsleder/sakslister/andre-kriterier', RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER)
  .withPost('/fplos/api/avdelingsleder/sakslister/saksbehandler', RestApiPathsKeys.LAGRE_SAKSLISTE_SAKSBEHANDLER)
  .withPost('/fplos/api/avdelingsleder/saksbehandlere/sok', RestApiPathsKeys.SAKSBEHANDLER_SOK)
  .withPost('/fplos/api/avdelingsleder/saksbehandlere', RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER)
  .withPost('/fplos/api/avdelingsleder/saksbehandlere/slett', RestApiPathsKeys.SLETT_SAKSBEHANDLER)
  .withGet('/fplos/api/avdelingsleder/nokkeltall/behandlinger-under-arbeid', RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING)
  .withGet('/fplos/api/avdelingsleder/nokkeltall/behandlinger-under-arbeid-historikk', RestApiPathsKeys.HENT_OPPGAVER_PER_DATO)
  .withGet('/fplos/api/avdelingsleder/nokkeltall/behandlinger-manuelt-vent-historikk', RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT)
  .withGet('/fplos/api/avdelingsleder/nokkeltall/behandlinger-forste-stonadsdag', RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG)
  .withGet('/fplos/api/avdelingsleder/nokkeltall/aapne-behandlinger', RestApiPathsKeys.HENT_OPPGAVER_APNE_ELLER_PA_VENT)
  .withGet('/fplos/api/avdelingsleder/reservasjoner', RestApiPathsKeys.RESERVASJONER_FOR_AVDELING)
  .withPost('/fplos/api/avdelingsleder/reservasjoner/opphev', RestApiPathsKeys.AVDELINGSLEDER_OPPHEVER_RESERVASJON)

  // Saksbehandler
  .withPost('/fplos/api/fagsak/sok', RestApiPathsKeys.SEARCH_FAGSAK)
  .withGet('/fplos/api/saksbehandler/saksliste', RestApiPathsKeys.SAKSLISTE)
  .withGet('/fplos/api/saksbehandler/oppgaver/reserverte', RestApiPathsKeys.RESERVERTE_OPPGAVER)
  .withAsyncGet('/fplos/api/saksbehandler/oppgaver', RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { maxPollingLimit: 1800 })
  .withPost('/fplos/api/saksbehandler/oppgaver/opphev', RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON)
  .withPost('/fplos/api/saksbehandler/oppgaver/forleng', RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON)
  .withPost('/fplos/api/saksbehandler/oppgaver/reservasjon/endre', RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON)
  .withPost('/fplos/api/saksbehandler/oppgaver/flytt', RestApiPathsKeys.FLYTT_RESERVASJON)
  .withPost('/fplos/api/saksbehandler/oppgaver/reserver', RestApiPathsKeys.RESERVER_OPPGAVE)
  .withPost('/fplos/api/saksbehandler/oppgaver/flytt/sok', RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK)
  .withGet('/fplos/api/saksbehandler/oppgaver/oppgaver-for-fagsaker', RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER)
  .withGet('/fplos/api/saksbehandler/oppgaver/reservasjon-status', RestApiPathsKeys.HENT_RESERVASJONSSTATUS)
  .withGet('/fplos/api/saksbehandler/nokkeltall/nye-og-ferdigstilte-oppgaver', RestApiPathsKeys.HENT_NYE_OG_FERDIGSTILTE_OPPGAVER)
  .withGet('/fplos/api/fpsak/behandlingId', RestApiPathsKeys.FPSAK_BEHANDLING_ID)
  .withGet('/fplos/api/saksbehandler/saksliste/saksbehandlere', RestApiPathsKeys.SAKSLISTE_SAKSBEHANDLERE)
  .withGet('/fplos/api/saksbehandler/oppgaver/antall', RestApiPathsKeys.BEHANDLINGSKO_OPPGAVE_ANTALL)
  .withGet('/fplos/api/saksbehandler/oppgaver/behandlede', RestApiPathsKeys.BEHANDLEDE_OPPGAVER)

  .build();

export const requestApi = createRequestApi(endpoints);

export const restApiHooks = RestApiHooks.initHooks(requestApi);
