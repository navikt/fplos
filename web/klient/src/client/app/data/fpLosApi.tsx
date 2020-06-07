import {
  RestApiConfigBuilder, ReduxRestApi, ReduxRestApiBuilder, ReduxEvents,
} from './rest-api-redux/index';
import errorHandler from './error-api-redux';

export const fpLosApiKeys = {
  BEHANDLEDE_OPPGAVER: 'BEHANDLEDE_OPPGAVER',
  FORLENG_OPPGAVERESERVASJON: 'FORLENG_OPPGAVERESERVASJON',
  SAKSLISTER_FOR_AVDELING: 'SAKSLISTER_FOR_AVDELING',
  OPPRETT_NY_SAKSLISTE: 'OPPRETT_NY_SAKSLISTE',
  SLETT_SAKSLISTE: 'SLETT_SAKSLISTE',
  LAGRE_SAKSLISTE_NAVN: 'LAGRE_SAKSLISTE_NAVN',
  LAGRE_SAKSLISTE_BEHANDLINGSTYPE: 'LAGRE_SAKSLISTE_BEHANDLINGSTYPE',
  LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE: 'LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE',
  LAGRE_SAKSLISTE_ANDRE_KRITERIER: 'LAGRE_SAKSLISTE_ANDRE_KRITERIER',
  LAGRE_SAKSLISTE_SORTERING: 'LAGRE_SAKSLISTE_SORTERING',
  LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE: 'LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE',
  LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DAGER: 'LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DAGER',
  LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO: 'LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO',
  RESERVASJONER_FOR_AVDELING: 'RESERVASJONER_FOR_AVDELING',
  AVDELINGSLEDER_OPPHEVER_RESERVASJON: 'AVDELINGSLEDER_OPPHEVER_RESERVASJON',
  SAKSBEHANDLER_SOK: 'SAKSBEHANDLER_SOK',
  SAKSBEHANDLERE_FOR_AVDELING: 'SAKSBEHANDLERE_FOR_AVDELING',
  OPPRETT_NY_SAKSBEHANDLER: 'OPPRETT_NY_SAKSBEHANDLER',
  SLETT_SAKSBEHANDLER: 'SLETT_SAKSBEHANDLER',
  LAGRE_SAKSLISTE_SAKSBEHANDLER: 'LAGRE_SAKSLISTE_SAKSBEHANDLER',
  HENT_OPPGAVER_FOR_AVDELING: 'HENT_OPPGAVER_FOR_AVDELING',
  HENT_OPPGAVER_PER_DATO: 'HENT_OPPGAVER_PER_DATO',
  HENT_OPPGAVER_PER_FORSTE_STONADSDAG: 'HENT_OPPGAVER_PER_FORSTE_STONADSDAG',
  HENT_OPPGAVER_MANUELT_PA_VENT: 'HENT_OPPGAVER_MANUELT_PA_VENT',
  OPPGAVE_ANTALL: 'OPPGAVE_ANTALL',
  OPPGAVE_AVDELING_ANTALL: 'OPPGAVE_AVDELING_ANTALL',
  SAKSLISTE_SAKSBEHANDLERE: 'SAKSLISTE_SAKSBEHANDLERE',
  BEHANDLINGSKO_OPPGAVE_ANTALL: 'BEHANDLINGSKO_OPPGAVE_ANTALL',
  // Delvis flytta
  FPSAK_BEHANDLING_ID: 'FPSAK_BEHANDLING_ID',
  FLYTT_RESERVASJON_SAKSBEHANDLER_SOK: 'FLYTT_RESERVASJON_SAKSBEHANDLER_SOK',
};

const endpoints = new RestApiConfigBuilder()
  .withPost('/api/saksbehandler/oppgaver/flytt/sok', fpLosApiKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK)

  /* /api/saksbehandler/saksliste */
  .withGet('/api/saksbehandler/saksliste/saksbehandlere', fpLosApiKeys.SAKSLISTE_SAKSBEHANDLERE)

  .withGet('/api/saksbehandler/oppgaver/behandlede', fpLosApiKeys.BEHANDLEDE_OPPGAVER)
  .withGet('/api/saksbehandler/oppgaver/antall', fpLosApiKeys.BEHANDLINGSKO_OPPGAVE_ANTALL)

  /* /api/avdelingsleder/sakslister */
  .withGet('/api/avdelingsleder/sakslister', fpLosApiKeys.SAKSLISTER_FOR_AVDELING)
  .withPost('/api/avdelingsleder/sakslister', fpLosApiKeys.OPPRETT_NY_SAKSLISTE)
  .withPost('/api/avdelingsleder/sakslister/slett', fpLosApiKeys.SLETT_SAKSLISTE)
  .withPost('/api/avdelingsleder/sakslister/navn', fpLosApiKeys.LAGRE_SAKSLISTE_NAVN)
  .withPost('/api/avdelingsleder/sakslister/behandlingstype', fpLosApiKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE)
  .withPost('/api/avdelingsleder/sakslister/ytelsetype', fpLosApiKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE)
  .withPost('/api/avdelingsleder/sakslister/andre-kriterier', fpLosApiKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER)
  .withPost('/api/avdelingsleder/sakslister/sortering', fpLosApiKeys.LAGRE_SAKSLISTE_SORTERING)
  .withPost('/api/avdelingsleder/sakslister/sortering-tidsintervall-type', fpLosApiKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE)
  .withPost('/api/avdelingsleder/sakslister/sortering-tidsintervall-dager', fpLosApiKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DAGER)
  .withPost('/api/avdelingsleder/sakslister/sortering-tidsintervall-dato', fpLosApiKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO)
  .withPost('/api/avdelingsleder/sakslister/saksbehandler', fpLosApiKeys.LAGRE_SAKSLISTE_SAKSBEHANDLER)

  /* /api/avdelingsleder/reservasjoner */
  .withGet('/api/avdelingsleder/reservasjoner', fpLosApiKeys.RESERVASJONER_FOR_AVDELING)
  .withPost('/api/avdelingsleder/reservasjoner/opphev', fpLosApiKeys.AVDELINGSLEDER_OPPHEVER_RESERVASJON)

  /* /api/avdelingsleder/saksbehandlere */
  .withPost('/api/avdelingsleder/saksbehandlere/sok', fpLosApiKeys.SAKSBEHANDLER_SOK)
  .withGet('/api/avdelingsleder/saksbehandlere', fpLosApiKeys.SAKSBEHANDLERE_FOR_AVDELING)
  .withPost('/api/avdelingsleder/saksbehandlere', fpLosApiKeys.OPPRETT_NY_SAKSBEHANDLER)
  .withPost('/api/avdelingsleder/saksbehandlere/slett', fpLosApiKeys.SLETT_SAKSBEHANDLER)

  /* /api/avdelingsleder/oppgaver */
  .withGet('/api/avdelingsleder/oppgaver/antall', fpLosApiKeys.OPPGAVE_ANTALL)
  .withGet('/api/avdelingsleder/oppgaver/avdelingantall', fpLosApiKeys.OPPGAVE_AVDELING_ANTALL)

  /* /api/avdelingsleder/nokkeltall */
  .withGet('/api/avdelingsleder/nokkeltall/behandlinger-under-arbeid', fpLosApiKeys.HENT_OPPGAVER_FOR_AVDELING)
  .withGet('/api/avdelingsleder/nokkeltall/behandlinger-under-arbeid-historikk', fpLosApiKeys.HENT_OPPGAVER_PER_DATO)
  .withGet('/api/avdelingsleder/nokkeltall/behandlinger-manuelt-vent-historikk', fpLosApiKeys.HENT_OPPGAVER_MANUELT_PA_VENT)
  .withGet('/api/avdelingsleder/nokkeltall/behandlinger-forste-stonadsdag', fpLosApiKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG)

  /* /api/fpsak/behandlinger */
  .withGet('/api/fpsak/behandlingId', fpLosApiKeys.FPSAK_BEHANDLING_ID)

  .build();

export const reduxRestApi: ReduxRestApi = new ReduxRestApiBuilder(endpoints, 'dataContext')
  .withContextPath('fplos')
  .withReduxEvents(new ReduxEvents()
    .withErrorActionCreator(errorHandler.getErrorActionCreator()))
  .build();

const fpLosApi = reduxRestApi.getEndpointApi();
export default fpLosApi;
