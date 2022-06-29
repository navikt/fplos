import NavAnsatt from 'types/navAnsattTsType';
import Driftsmelding from 'types/driftsmeldingTsType';
import Avdeling from 'types/avdelingsleder/avdelingTsType';
import SakslisteAvdeling from 'types/avdelingsleder/sakslisteAvdelingTsType';
import Saksliste from 'types/saksbehandler/sakslisteTsType';
import SaksbehandlerAvdeling from 'types/avdelingsleder/saksbehandlerAvdelingTsType';
import Saksbehandler from 'types/saksbehandler/saksbehandlerTsType';
import OppgaverForAvdeling from 'types/avdelingsleder/oppgaverForAvdelingTsType';
import OppgaveForDato from 'types/avdelingsleder/oppgaverForDatoTsType';
import OppgaverManueltPaVent from 'types/avdelingsleder/oppgaverManueltPaVentTsType';
import OppgaverForForsteStonadsdag from 'types/avdelingsleder/oppgaverForForsteStonadsdagTsType';
import OppgaverSomErApneEllerPaVent from 'types/avdelingsleder/oppgaverSomErApneEllerPaVentTsType';
import Reservasjon from 'types/avdelingsleder/reservasjonTsType';
import Oppgave from 'types/saksbehandler/oppgaveTsType';
import NyeOgFerdigstilteOppgaver from 'types/saksbehandler/nyeOgFerdigstilteOppgaverTsType';
import OppgaveStatus from 'types/saksbehandler/oppgaveStatusTsType';
import SaksbehandlerForFlytting from 'types/saksbehandler/saksbehandlerForFlyttingTsType';
import Fagsak from 'types/saksbehandler/fagsakTsType';
import { AlleKodeverk } from '@navikt/ft-types';

import { RestApiConfigBuilder, createRequestApi, RestKey } from './rest-api';
import { RestApiHooks } from './rest-api-hooks';

export const RestApiGlobalStatePathsKeys = {
  KODEVERK: new RestKey<AlleKodeverk, void>('KODEVERK'),
  NAV_ANSATT: new RestKey<NavAnsatt, void>('NAV_ANSATT'),
  FPSAK_URL: new RestKey<{ verdi: string }, void>('FPSAK_URL'),
  FPTILBAKE_URL: new RestKey<{ verdi: string }, void>('FPTILBAKE_URL'),
  AVDELINGER: new RestKey<Avdeling[], void>('AVDELINGER'),
  DRIFTSMELDINGER: new RestKey<Driftsmelding[], void>('DRIFTSMELDINGER'),
};

export const RestApiPathsKeys = {
  SEARCH_FAGSAK: new RestKey<Fagsak[], {searchString: string; skalReservere: boolean}>('SEARCH_FAGSAK'),
  OPPGAVER_FOR_FAGSAKER: new RestKey<Oppgave[], { saksnummerListe: string }>('OPPGAVER_FOR_FAGSAKER'),
  SAKSLISTE: new RestKey<Saksliste[], void>('SAKSLISTE'),
  RESERVERTE_OPPGAVER: new RestKey<Oppgave[], void>('RESERVERTE_OPPGAVER'),
  OPPGAVER_TIL_BEHANDLING: new RestKey<Oppgave[], { sakslisteId: number, oppgaveIder?: string }>('OPPGAVER_TIL_BEHANDLING'),
  OPPHEV_OPPGAVERESERVASJON: new RestKey<void, { oppgaveId: number, begrunnelse: string }>('OPPHEV_OPPGAVERESERVASJON'),
  FORLENG_OPPGAVERESERVASJON: new RestKey<Oppgave[], { oppgaveId: number }>('FORLENG_OPPGAVERESERVASJON'),
  ENDRE_OPPGAVERESERVASJON: new RestKey<Oppgave[], { oppgaveId: number, reserverTil: string }>('ENDRE_OPPGAVERESERVASJON'),
  FLYTT_RESERVASJON: new RestKey<void, { oppgaveId: number, brukerIdent: string, begrunnelse: string }>('FLYTT_RESERVASJON'),
  RESERVER_OPPGAVE: new RestKey<OppgaveStatus, { oppgaveId: number }>('RESERVER_OPPGAVE'),
  FLYTT_RESERVASJON_SAKSBEHANDLER_SOK: new RestKey<SaksbehandlerForFlytting, string>('FLYTT_RESERVASJON_SAKSBEHANDLER_SOK'),
  HENT_RESERVASJONSSTATUS: new RestKey<OppgaveStatus, { oppgaveId: number }>('HENT_RESERVASJONSSTATUS'),
  HENT_NYE_OG_FERDIGSTILTE_OPPGAVER: new RestKey<NyeOgFerdigstilteOppgaver[], { sakslisteId: number }>('HENT_NYE_OG_FERDIGSTILTE_OPPGAVER'),
  SAKSLISTE_SAKSBEHANDLERE: new RestKey<Saksbehandler[], { sakslisteId: number }>('SAKSLISTE_SAKSBEHANDLERE'),
  BEHANDLINGSKO_OPPGAVE_ANTALL: new RestKey<number, {sakslisteId: number}>('BEHANDLINGSKO_OPPGAVE_ANTALL'),
  BEHANDLEDE_OPPGAVER: new RestKey<Oppgave[], void>('BEHANDLEDE_OPPGAVER'),
  SAKSLISTER_FOR_AVDELING: new RestKey<SakslisteAvdeling[], { avdelingEnhet: string }>('SAKSLISTER_FOR_AVDELING'),
  SAKSBEHANDLERE_FOR_AVDELING: new RestKey<SaksbehandlerAvdeling[], { avdelingEnhet: string }>('SAKSBEHANDLERE_FOR_AVDELING'),
  OPPGAVE_AVDELING_ANTALL: new RestKey<number, { avdelingEnhet: string }>('OPPGAVE_AVDELING_ANTALL'),
  OPPGAVE_ANTALL: new RestKey<number, { sakslisteId: number, avdelingEnhet: string }>('OPPGAVE_ANTALL'),
  SAKSBEHANDLER_SOK: new RestKey<SaksbehandlerAvdeling, { brukerIdent: string }>('SAKSBEHANDLER_SOK'),
  OPPRETT_NY_SAKSBEHANDLER: new RestKey<void, { brukerIdent: string, avdelingEnhet: string }>('OPPRETT_NY_SAKSBEHANDLER'),
  SLETT_SAKSBEHANDLER: new RestKey<void, { brukerIdent: string, avdelingEnhet: string }>('SLETT_SAKSBEHANDLER'),
  HENT_OPPGAVER_FOR_AVDELING: new RestKey<OppgaverForAvdeling[], { avdelingEnhet: string }>('HENT_OPPGAVER_FOR_AVDELING'),
  HENT_OPPGAVER_PER_DATO: new RestKey<OppgaveForDato[], { avdelingEnhet: string }>('HENT_OPPGAVER_PER_DATO'),
  HENT_OPPGAVER_PER_FORSTE_STONADSDAG: new RestKey<OppgaverForForsteStonadsdag[], { avdelingEnhet: string }>('HENT_OPPGAVER_PER_FORSTE_STONADSDAG'),
  HENT_OPPGAVER_MANUELT_PA_VENT: new RestKey<OppgaverManueltPaVent[], { avdelingEnhet: string }>('HENT_OPPGAVER_MANUELT_PA_VENT'),
  HENT_OPPGAVER_APNE_ELLER_PA_VENT: new RestKey<OppgaverSomErApneEllerPaVent[], { avdelingEnhet: string }>('HENT_OPPGAVER_APNE_ELLER_PA_VENT'),
  RESERVASJONER_FOR_AVDELING: new RestKey<Reservasjon[], { avdelingEnhet: string }>('RESERVASJONER_FOR_AVDELING'),
  AVDELINGSLEDER_OPPHEVER_RESERVASJON: new RestKey<void, { oppgaveId: number }>('AVDELINGSLEDER_OPPHEVER_RESERVASJON'),
  OPPRETT_NY_SAKSLISTE: new RestKey<{sakslisteId: string}, { avdelingEnhet: string }>('OPPRETT_NY_SAKSLISTE'),
  SLETT_SAKSLISTE: new RestKey<void, { sakslisteId: number, avdelingEnhet: string }>('SLETT_SAKSLISTE'),
  LAGRE_SAKSLISTE_NAVN: new RestKey<void, { sakslisteId: number, navn: string, avdelingEnhet: string }>('LAGRE_SAKSLISTE_NAVN'),
  LAGRE_SAKSLISTE_BEHANDLINGSTYPE: new RestKey<void, {
    sakslisteId: number, avdelingEnhet: string, behandlingType: string, checked: boolean,
  }>('LAGRE_SAKSLISTE_BEHANDLINGSTYPE'),
  LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE: new RestKey<void, {
    sakslisteId: number, avdelingEnhet: string, fagsakYtelseType: string, checked: boolean
  }>('LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE'),
  LAGRE_SAKSLISTE_SORTERING: new RestKey<void, {
    sakslisteId: number, sakslisteSorteringValg: string, avdelingEnhet: string,
  }>('LAGRE_SAKSLISTE_SORTERING'),
  LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE: new RestKey<void, {
    sakslisteId: number, avdelingEnhet: string,
  }>('LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE'),
  LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO: new RestKey<void, {
    sakslisteId: number, avdelingEnhet: string, fomDato: string, tomDato: string,
  }>('LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO'),
  LAGRE_SAKSLISTE_SORTERING_INTERVALL: new RestKey<void, {
    sakslisteId: number, fra: number, til: number, avdelingEnhet: string,
  }>('LAGRE_SAKSLISTE_SORTERING_INTERVALL'),
  LAGRE_SAKSLISTE_ANDRE_KRITERIER: new RestKey<void, {
    sakslisteId: number, avdelingEnhet: string, andreKriterierType: string, checked: boolean, inkluder: boolean,
  }>('LAGRE_SAKSLISTE_ANDRE_KRITERIER'),
  LAGRE_SAKSLISTE_SAKSBEHANDLER: new RestKey<void, {
    sakslisteId: number, brukerIdent: string, checked: boolean, avdelingEnhet: string,
  }>('LAGRE_SAKSLISTE_SAKSBEHANDLER'),
};

export const endpoints = new RestApiConfigBuilder()
  .withGet('/fplos/api/saksbehandler', RestApiGlobalStatePathsKeys.NAV_ANSATT)
  .withGet('/fplos/api/konfig/fpsak-url', RestApiGlobalStatePathsKeys.FPSAK_URL)
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
  .withPost('/fplos/api/avdelingsleder/sakslister/ytelsetyper', RestApiPathsKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE)
  .withPost('/fplos/api/avdelingsleder/sakslister/sortering', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING)
  .withPost('/fplos/api/avdelingsleder/sakslister/sortering-tidsintervall-type', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE)
  .withPost('/fplos/api/avdelingsleder/sakslister/sortering-tidsintervall-dato', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO)
  .withPost('/fplos/api/avdelingsleder/sakslister/sortering-numerisk-intervall', RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_INTERVALL)
  .withPost('/fplos/api/avdelingsleder/sakslister/andre-kriterier', RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER)
  .withPost('/fplos/api/avdelingsleder/sakslister/saksbehandler', RestApiPathsKeys.LAGRE_SAKSLISTE_SAKSBEHANDLER)
  .withPost('/fplos/api/avdelingsleder/saksbehandlere/søk', RestApiPathsKeys.SAKSBEHANDLER_SOK)
  .withPost('/fplos/api/avdelingsleder/saksbehandlere', RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER)
  .withPost('/fplos/api/avdelingsleder/saksbehandlere/slett', RestApiPathsKeys.SLETT_SAKSBEHANDLER)
  .withGet('/fplos/api/avdelingsleder/nøkkeltall/behandlinger-under-arbeid', RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING)
  .withGet('/fplos/api/avdelingsleder/nøkkeltall/behandlinger-under-arbeid-historikk', RestApiPathsKeys.HENT_OPPGAVER_PER_DATO)
  .withGet('/fplos/api/avdelingsleder/nøkkeltall/behandlinger-manuelt-vent-historikk', RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT)
  .withGet('/fplos/api/avdelingsleder/nøkkeltall/behandlinger-første-stønadsdag', RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG)
  .withGet('/fplos/api/avdelingsleder/nøkkeltall/åpne-behandlinger', RestApiPathsKeys.HENT_OPPGAVER_APNE_ELLER_PA_VENT)
  .withGet('/fplos/api/avdelingsleder/reservasjoner', RestApiPathsKeys.RESERVASJONER_FOR_AVDELING)
  .withPost('/fplos/api/avdelingsleder/reservasjoner/opphev', RestApiPathsKeys.AVDELINGSLEDER_OPPHEVER_RESERVASJON)

  // Saksbehandler
  .withPost('/fplos/api/fagsak/søk', RestApiPathsKeys.SEARCH_FAGSAK)
  .withGet('/fplos/api/saksbehandler/saksliste', RestApiPathsKeys.SAKSLISTE)
  .withGet('/fplos/api/saksbehandler/oppgaver/reserverte', RestApiPathsKeys.RESERVERTE_OPPGAVER)
  .withAsyncGet('/fplos/api/saksbehandler/oppgaver', RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, { maxPollingLimit: 1800 })
  .withPost('/fplos/api/saksbehandler/oppgaver/opphev', RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON)
  .withPost('/fplos/api/saksbehandler/oppgaver/forleng', RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON)
  .withPost('/fplos/api/saksbehandler/oppgaver/reservasjon/endre', RestApiPathsKeys.ENDRE_OPPGAVERESERVASJON)
  .withPost('/fplos/api/saksbehandler/oppgaver/flytt', RestApiPathsKeys.FLYTT_RESERVASJON)
  .withPost('/fplos/api/saksbehandler/oppgaver/reserver', RestApiPathsKeys.RESERVER_OPPGAVE)
  .withPost('/fplos/api/saksbehandler/oppgaver/flytt/søk', RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK)
  .withGet('/fplos/api/saksbehandler/oppgaver/oppgaver-for-fagsaker', RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER)
  .withGet('/fplos/api/saksbehandler/oppgaver/reservasjon-status', RestApiPathsKeys.HENT_RESERVASJONSSTATUS)
  .withGet('/fplos/api/saksbehandler/nøkkeltall/nye-og-ferdigstilte-oppgaver', RestApiPathsKeys.HENT_NYE_OG_FERDIGSTILTE_OPPGAVER)
  .withGet('/fplos/api/saksbehandler/saksliste/saksbehandlere', RestApiPathsKeys.SAKSLISTE_SAKSBEHANDLERE)
  .withGet('/fplos/api/saksbehandler/oppgaver/antall', RestApiPathsKeys.BEHANDLINGSKO_OPPGAVE_ANTALL)
  .withGet('/fplos/api/saksbehandler/oppgaver/behandlede', RestApiPathsKeys.BEHANDLEDE_OPPGAVER)

  .build();

export const requestApi = createRequestApi(endpoints);

export const restApiHooks = RestApiHooks.initHooks(requestApi);
