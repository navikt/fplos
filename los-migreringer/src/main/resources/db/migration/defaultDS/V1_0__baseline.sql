--------------------------------------------------------
--  File created - fredag-september-06-2019
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence SEQ_AVDELING
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_AVDELING"  MINVALUE 1 INCREMENT BY 50 START WITH 451 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_EVENTMOTTAK_FEILLOGG
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_EVENTMOTTAK_FEILLOGG"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_FILTR_BEHANDLING_TYPE
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_FILTR_BEHANDLING_TYPE"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_FILTRERING_ANDRE_KRIT
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_FILTRERING_ANDRE_KRIT"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_FILTRERING_YTELSE_TYPE
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_FILTRERING_YTELSE_TYPE"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_KODELISTE
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_KODELISTE"  MINVALUE 1 INCREMENT BY 50 START WITH 1007850 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_KODELISTE_NAVN_I18N
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_KODELISTE_NAVN_I18N"  MINVALUE 1 INCREMENT BY 50 START WITH 2751 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_KONFIG_VERDI
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_KONFIG_VERDI"  MINVALUE 1 INCREMENT BY 50 START WITH 100700 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_OPPGAVE
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_OPPGAVE"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE  ORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_OPPGAVE_EGENSKAP
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_OPPGAVE_EGENSKAP"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE  ORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_OPPGAVE_EVENT_LOGG
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_OPPGAVE_EVENT_LOGG"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE  ORDER  NOCYCLE  ;
--------------------------------------------------------
--  DDL for Sequence SEQ_OPPGAVE_FILTRERING
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_OPPGAVE_FILTRERING"  MINVALUE 1 INCREMENT BY 200 START WITH 551 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_RESERVASJON
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_RESERVASJON"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_RESERVASJON_EVENT_LOGG
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_RESERVASJON_EVENT_LOGG"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_SAKSBEHANDLER
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_SAKSBEHANDLER"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE NOCYCLE ;
--------------------------------------------------------
--  DDL for Table AVDELING
--------------------------------------------------------

  CREATE TABLE "AVDELING"
   (	"ID" NUMBER(19,0),
	"AVDELING_ENHET" VARCHAR2(10 CHAR),
	"NAVN" VARCHAR2(255 CHAR),
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3),
	"KREVER_KODE_6" VARCHAR2(1 CHAR) DEFAULT 'N'
   ) ;

   COMMENT ON TABLE "AVDELING"  IS 'Tabell som inneholder avdelingene som kan utføre oppgaver';
   COMMENT ON COLUMN "AVDELING"."ID" IS 'PK';
   COMMENT ON COLUMN "AVDELING"."AVDELING_ENHET" IS 'Identifikasjon på enhet som har oppgaver.';
   COMMENT ON COLUMN "AVDELING"."KREVER_KODE_6" IS 'Kreves kode 6 for å kunne velge denne avdelingen';
--------------------------------------------------------
--  DDL for Table AVDELING_SAKSBEHANDLER
--------------------------------------------------------

  CREATE TABLE "AVDELING_SAKSBEHANDLER"
   (	"SAKSBEHANDLER_ID" NUMBER(19,0),
	"AVDELING_ID" NUMBER(19,0)
   ) ;

   COMMENT ON TABLE "AVDELING_SAKSBEHANDLER"  IS 'Tabell som er koblingen mellom avdeling og saksbehandler';
   COMMENT ON COLUMN "AVDELING_SAKSBEHANDLER"."SAKSBEHANDLER_ID" IS 'FK til saksbehandler';
   COMMENT ON COLUMN "AVDELING_SAKSBEHANDLER"."AVDELING_ID" IS 'FK til avdeling';
--------------------------------------------------------
--  DDL for Table EVENTMOTTAK_FEILLOGG
--------------------------------------------------------

  CREATE TABLE "EVENTMOTTAK_FEILLOGG"
   (	"ID" NUMBER(19,0),
	"MELDING" CLOB,
	"STATUS" VARCHAR2(100 CHAR),
	"ANTALL_FEILEDE_FORSOK" NUMBER(19,0) DEFAULT 0,
	"SISTE_KJORING_TS" TIMESTAMP (3),
	"FEILMELDING_SISTE_KJORING" CLOB,
	"KL_STATUS" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('EVENTMOTTAK_STATUS') VIRTUAL ,
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VLLOS',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "EVENTMOTTAK_FEILLOGG"  IS 'Feillogg for eventer som kommer fra fpsak';
   COMMENT ON COLUMN "EVENTMOTTAK_FEILLOGG"."ID" IS 'PK';
   COMMENT ON COLUMN "EVENTMOTTAK_FEILLOGG"."MELDING" IS 'Meldingen som kom fra eventkøen';
   COMMENT ON COLUMN "EVENTMOTTAK_FEILLOGG"."STATUS" IS 'Status for meldingen';
   COMMENT ON COLUMN "EVENTMOTTAK_FEILLOGG"."ANTALL_FEILEDE_FORSOK" IS 'Antallet feilede forsøk';
   COMMENT ON COLUMN "EVENTMOTTAK_FEILLOGG"."SISTE_KJORING_TS" IS 'Tidspunkt for siste kjøring';
   COMMENT ON COLUMN "EVENTMOTTAK_FEILLOGG"."FEILMELDING_SISTE_KJORING" IS 'Feilmelding for siste kjøring';
--------------------------------------------------------
--  DDL for Table FILTRERING_ANDRE_KRITERIER
--------------------------------------------------------

  CREATE TABLE "FILTRERING_ANDRE_KRITERIER"
   (	"ID" NUMBER(19,0),
	"OPPGAVE_FILTRERING_ID" NUMBER(19,0),
	"ANDRE_KRITERIER_TYPE" VARCHAR2(100 CHAR),
	"KL_ANDRE_KRITERIER_TYPE" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('ANDRE_KRITERIER') VIRTUAL ,
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3),
	"INKLUDER" VARCHAR2(1 CHAR) DEFAULT 'J'
   ) ;

   COMMENT ON TABLE "FILTRERING_ANDRE_KRITERIER"  IS 'Tabell inneholder filtreringer for andre kriterier';
   COMMENT ON COLUMN "FILTRERING_ANDRE_KRITERIER"."ID" IS 'PK';
   COMMENT ON COLUMN "FILTRERING_ANDRE_KRITERIER"."OPPGAVE_FILTRERING_ID" IS 'Oppgavefiltrering som filtreringen hører til';
   COMMENT ON COLUMN "FILTRERING_ANDRE_KRITERIER"."ANDRE_KRITERIER_TYPE" IS 'Kode for de andre kriteriene oppgavene skal filtreres på';
   COMMENT ON COLUMN "FILTRERING_ANDRE_KRITERIER"."KL_ANDRE_KRITERIER_TYPE" IS 'Kodeverk andre kriterier';
   COMMENT ON COLUMN "FILTRERING_ANDRE_KRITERIER"."INKLUDER" IS 'Verdi som sier om det skal filtreres inn eller filtreres vekk.';
--------------------------------------------------------
--  DDL for Table FILTRERING_BEHANDLING_TYPE
--------------------------------------------------------

  CREATE TABLE "FILTRERING_BEHANDLING_TYPE"
   (	"ID" NUMBER(19,0),
	"OPPGAVE_FILTRERING_ID" NUMBER(19,0),
	"BEHANDLING_TYPE" VARCHAR2(100 CHAR),
	"KL_BEHANDLING_TYPE" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('BEHANDLING_TYPE') VIRTUAL ,
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "FILTRERING_BEHANDLING_TYPE"  IS 'Tabell inneholder filtreringer på behandlingType';
   COMMENT ON COLUMN "FILTRERING_BEHANDLING_TYPE"."ID" IS 'PK';
   COMMENT ON COLUMN "FILTRERING_BEHANDLING_TYPE"."OPPGAVE_FILTRERING_ID" IS 'Listen filtreringen hører til';
   COMMENT ON COLUMN "FILTRERING_BEHANDLING_TYPE"."BEHANDLING_TYPE" IS 'Koden behandlingstypen skal filtreres på';
   COMMENT ON COLUMN "FILTRERING_BEHANDLING_TYPE"."KL_BEHANDLING_TYPE" IS 'Kodeverk behandlingstype';
--------------------------------------------------------
--  DDL for Table FILTRERING_SAKSBEHANDLER
--------------------------------------------------------

  CREATE TABLE "FILTRERING_SAKSBEHANDLER"
   (	"SAKSBEHANDLER_ID" NUMBER(19,0),
	"OPPGAVE_FILTRERING_ID" NUMBER(19,0)
   ) ;

   COMMENT ON TABLE "FILTRERING_SAKSBEHANDLER"  IS 'Tabell som er koblingen mellom avdeling og saksbehandler';
   COMMENT ON COLUMN "FILTRERING_SAKSBEHANDLER"."SAKSBEHANDLER_ID" IS 'FK til saksbehandler';
   COMMENT ON COLUMN "FILTRERING_SAKSBEHANDLER"."OPPGAVE_FILTRERING_ID" IS 'FK til oppgave filtrering';
--------------------------------------------------------
--  DDL for Table FILTRERING_YTELSE_TYPE
--------------------------------------------------------

  CREATE TABLE "FILTRERING_YTELSE_TYPE"
   (	"ID" NUMBER(19,0),
	"OPPGAVE_FILTRERING_ID" NUMBER(19,0),
	"FAGSAK_YTELSE_TYPE" VARCHAR2(100 CHAR),
	"KL_FAGSAK_YTELSE_TYPE" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('FAGSAK_YTELSE') VIRTUAL ,
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "FILTRERING_YTELSE_TYPE"  IS 'Tabell inneholder filtreringer på behandlingType';
   COMMENT ON COLUMN "FILTRERING_YTELSE_TYPE"."ID" IS 'PK';
   COMMENT ON COLUMN "FILTRERING_YTELSE_TYPE"."OPPGAVE_FILTRERING_ID" IS 'Oppgavefiltrering som filtreringen hører til';
   COMMENT ON COLUMN "FILTRERING_YTELSE_TYPE"."FAGSAK_YTELSE_TYPE" IS 'Koden ytelsetypen skal filtreres på';
   COMMENT ON COLUMN "FILTRERING_YTELSE_TYPE"."KL_FAGSAK_YTELSE_TYPE" IS 'Kodeverk ytelsetype';
--------------------------------------------------------
--  DDL for Table KODELISTE
--------------------------------------------------------

  CREATE TABLE "KODELISTE"
   (	"ID" NUMBER(19,0),
	"KODEVERK" VARCHAR2(100 CHAR),
	"KODE" VARCHAR2(100 CHAR),
	"OFFISIELL_KODE" VARCHAR2(1000 CHAR),
	"BESKRIVELSE" VARCHAR2(4000 CHAR),
	"GYLDIG_FOM" DATE DEFAULT sysdate,
	"GYLDIG_TOM" DATE DEFAULT to_date('31.12.9999', 'dd.mm.yyyy'),
	"OPPRETTET_AV" VARCHAR2(200 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(200 CHAR),
	"ENDRET_TID" TIMESTAMP (3),
	"EKSTRA_DATA" VARCHAR2(4000 CHAR)
   ) ;

   COMMENT ON COLUMN "KODELISTE"."ID" IS 'Primary Key';
   COMMENT ON COLUMN "KODELISTE"."KODEVERK" IS '(PK) og FK - kodeverk';
   COMMENT ON COLUMN "KODELISTE"."KODE" IS '(PK) Unik kode innenfor kodeverk. Denne koden er alltid brukt internt';
   COMMENT ON COLUMN "KODELISTE"."OFFISIELL_KODE" IS '(Optional) Offisiell kode hos kodeverkeier. Denne kan avvike fra kode der systemet har egne koder. Kan brukes til å veksle inn kode i offisiell kode når det trengs for integrasjon med andre systemer';
   COMMENT ON COLUMN "KODELISTE"."BESKRIVELSE" IS 'Beskrivelse av koden';
   COMMENT ON COLUMN "KODELISTE"."GYLDIG_FOM" IS 'Dato Kodeverket er gyldig fra og med';
   COMMENT ON COLUMN "KODELISTE"."GYLDIG_TOM" IS 'Dato Kodeverket er gyldig til og med';
   COMMENT ON COLUMN "KODELISTE"."EKSTRA_DATA" IS '(Optional) Tilleggsdata brukt av kodeverket.  Format er kodeverk spesifikt - eks. kan være tekst, json, key-value, etc.';
   COMMENT ON TABLE "KODELISTE"  IS 'Inneholder lister av koder for alle Kodeverk som benyttes i applikasjonen.  Både offisielle (synkronisert fra sentralt hold i Nav) såvel som interne Kodeverk.  Offisielle koder skiller seg ut ved at nav_offisiell_kode er populert. Følgelig vil gyldig_tom/fom, navn, språk og beskrivelse lastes ned fra Kodeverkklienten eller annen kilde sentralt';
--------------------------------------------------------
--  DDL for Table KODELISTE_NAVN_I18N
--------------------------------------------------------

  CREATE TABLE "KODELISTE_NAVN_I18N"
   (	"ID" NUMBER(19,0),
	"KL_KODEVERK" VARCHAR2(100 CHAR),
	"KL_KODE" VARCHAR2(100 CHAR),
	"SPRAK" VARCHAR2(3 CHAR),
	"NAVN" VARCHAR2(256 CHAR),
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "KODELISTE_NAVN_I18N"  IS 'Ny tabell som vil holde secondaryKodeliste navn verdi av all språk vi støtte';
   COMMENT ON COLUMN "KODELISTE_NAVN_I18N"."KL_KODEVERK" IS 'FK - Kodeverk fra secondaryKodeliste tabell';
   COMMENT ON COLUMN "KODELISTE_NAVN_I18N"."KL_KODE" IS 'FK - Kode fra secondaryKodeliste tabell';
   COMMENT ON COLUMN "KODELISTE_NAVN_I18N"."SPRAK" IS 'Respective språk';
--------------------------------------------------------
--  DDL for Table KODEVERK
--------------------------------------------------------

  CREATE TABLE "KODEVERK"
   (	"KODE" VARCHAR2(100 CHAR),
	"KODEVERK_EIER" VARCHAR2(100 CHAR) DEFAULT 'VL',
	"KODEVERK_EIER_REF" VARCHAR2(1000 CHAR),
	"KODEVERK_EIER_VER" VARCHAR2(20 CHAR),
	"KODEVERK_EIER_NAVN" VARCHAR2(100 CHAR),
	"KODEVERK_SYNK_NYE" CHAR(1 BYTE) DEFAULT 'J',
	"KODEVERK_SYNK_EKSISTERENDE" CHAR(1 BYTE) DEFAULT 'J',
	"NAVN" VARCHAR2(256 CHAR),
	"BESKRIVELSE" VARCHAR2(4000 CHAR),
	"OPPRETTET_AV" VARCHAR2(200 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(200 CHAR),
	"ENDRET_TID" TIMESTAMP (3),
	"SAMMENSATT" VARCHAR2(1 CHAR) DEFAULT 'N'
   ) ;

   COMMENT ON COLUMN "KODEVERK"."KODE" IS 'PK - definerer kodeverk';
   COMMENT ON COLUMN "KODEVERK"."KODEVERK_EIER" IS 'Offisielt kodeverk eier (kode)';
   COMMENT ON COLUMN "KODEVERK"."KODEVERK_EIER_REF" IS 'Offisielt kodeverk referanse (url)';
   COMMENT ON COLUMN "KODEVERK"."KODEVERK_EIER_VER" IS 'Offisielt kodeverk versjon';
   COMMENT ON COLUMN "KODEVERK"."KODEVERK_EIER_NAVN" IS 'Offisielt kodeverk navn';
   COMMENT ON COLUMN "KODEVERK"."KODEVERK_SYNK_NYE" IS 'Om nye koder fra kodeverkeier skal legges til ved oppdatering.';
   COMMENT ON COLUMN "KODEVERK"."KODEVERK_SYNK_EKSISTERENDE" IS 'Om eksisterende koder fra kodeverkeier skal endres ved oppdatering.';
   COMMENT ON COLUMN "KODEVERK"."NAVN" IS 'Navn på kodeverk';
   COMMENT ON COLUMN "KODEVERK"."BESKRIVELSE" IS 'Beskrivelse av kodeverk';
   COMMENT ON COLUMN "KODEVERK"."SAMMENSATT" IS 'Skiller mellom sammensatt kodeverk og enkel secondaryKodeliste';
   COMMENT ON TABLE "KODEVERK"  IS 'Registrerte kodeverk. Representerer grupperinger av koder';
--------------------------------------------------------
--  DDL for Table KONFIG_VERDI
--------------------------------------------------------

  CREATE TABLE "KONFIG_VERDI"
   (	"ID" NUMBER(19,0),
	"KONFIG_KODE" VARCHAR2(50 CHAR),
	"KONFIG_GRUPPE" VARCHAR2(50 CHAR),
	"KONFIG_VERDI" VARCHAR2(255 CHAR),
	"GYLDIG_FOM" DATE DEFAULT sysdate,
	"GYLDIG_TOM" DATE DEFAULT to_date('31.12.9999','dd.mm.yyyy'),
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "KONFIG_VERDI"  IS 'Angir konfigurerbare verdier med kode, eventuelt tilhørende gruppe.';
   COMMENT ON COLUMN "KONFIG_VERDI"."ID" IS 'Primary Key';
   COMMENT ON COLUMN "KONFIG_VERDI"."KONFIG_KODE" IS 'Angir kode som identifiserer en konfigurerbar verdi. ';
   COMMENT ON COLUMN "KONFIG_VERDI"."KONFIG_VERDI" IS 'Angir verdi';
   COMMENT ON COLUMN "KONFIG_VERDI"."GYLDIG_FOM" IS 'Gydlig fra-og-med dato';
   COMMENT ON COLUMN "KONFIG_VERDI"."GYLDIG_TOM" IS 'Gydlig til-og-med dato';
--------------------------------------------------------
--  DDL for Table KONFIG_VERDI_GRUPPE
--------------------------------------------------------

  CREATE TABLE "KONFIG_VERDI_GRUPPE"
   (	"KODE" VARCHAR2(50 CHAR),
	"NAVN" VARCHAR2(50 CHAR),
	"BESKRIVELSE" VARCHAR2(255 CHAR),
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "KONFIG_VERDI_GRUPPE"  IS 'Angir en gruppe konfigurerbare verdier tilhører. Det åpner for å kunne ha lister og Maps av konfigurerbare verdier';
   COMMENT ON COLUMN "KONFIG_VERDI_GRUPPE"."KODE" IS 'Primary Key for gruppen';
   COMMENT ON COLUMN "KONFIG_VERDI_GRUPPE"."NAVN" IS 'Angir et visningsnavn for gruppen';
   COMMENT ON COLUMN "KONFIG_VERDI_GRUPPE"."BESKRIVELSE" IS 'Beskrivelse av formålet med gruppen';
--------------------------------------------------------
--  DDL for Table KONFIG_VERDI_KODE
--------------------------------------------------------

  CREATE TABLE "KONFIG_VERDI_KODE"
   (	"KODE" VARCHAR2(50 CHAR),
	"KONFIG_GRUPPE" VARCHAR2(50 CHAR) DEFAULT 'INGEN',
	"NAVN" VARCHAR2(50 CHAR),
	"KONFIG_TYPE" VARCHAR2(50 CHAR),
	"BESKRIVELSE" VARCHAR2(255 CHAR),
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "KONFIG_VERDI_KODE"  IS 'Angir unik kode for en konfigurerbar verdi for validering og utlisting av tilgjengelige koder.';
   COMMENT ON COLUMN "KONFIG_VERDI_KODE"."KODE" IS 'Primary Key';
   COMMENT ON COLUMN "KONFIG_VERDI_KODE"."NAVN" IS 'Angir et visningsnavn';
   COMMENT ON COLUMN "KONFIG_VERDI_KODE"."BESKRIVELSE" IS 'Beskrivelse av formålet den konfigurerbare verdien';
   COMMENT ON COLUMN "KONFIG_VERDI_KODE"."KONFIG_TYPE" IS 'Type angivelse for koden';
   COMMENT ON COLUMN "KONFIG_VERDI_KODE"."KONFIG_GRUPPE" IS 'Angir gruppe en konfigurerbar verdi kode tilhører (hvis noen - kan også spesifiseres som INGEN).';
--------------------------------------------------------
--  DDL for Table KONFIG_VERDI_TYPE
--------------------------------------------------------

  CREATE TABLE "KONFIG_VERDI_TYPE"
   (	"KODE" VARCHAR2(50 CHAR),
	"NAVN" VARCHAR2(50 CHAR),
	"BESKRIVELSE" VARCHAR2(255 CHAR),
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "KONFIG_VERDI_TYPE"  IS 'Angir type den konfigurerbare verdien er av slik at dette kan brukes til validering og fremstilling.';
   COMMENT ON COLUMN "KONFIG_VERDI_TYPE"."KODE" IS 'Primary Key';
   COMMENT ON COLUMN "KONFIG_VERDI_TYPE"."NAVN" IS 'Angir et visningsnavn for typen';
   COMMENT ON COLUMN "KONFIG_VERDI_TYPE"."BESKRIVELSE" IS 'Beskrivelse av bruk av typen';
--------------------------------------------------------
--  DDL for Table OPPGAVE
--------------------------------------------------------

  CREATE TABLE "OPPGAVE"
   (	"ID" NUMBER(19,0),
	"BEHANDLING_ID" NUMBER(19,0),
	"FAGSAK_SAKSNR" NUMBER(19,0),
	"AKTOR_ID" NUMBER(19,0),
	"BEHANDLENDE_ENHET" VARCHAR2(10 CHAR),
	"AKTIV" VARCHAR2(1 CHAR) DEFAULT 'Y',
	"BEHANDLING_TYPE" VARCHAR2(100 CHAR),
	"FAGSAK_YTELSE_TYPE" VARCHAR2(100 CHAR),
	"SYSTEM" VARCHAR2(100 CHAR),
	"BEHANDLINGSFRIST" TIMESTAMP (3),
	"BEHANDLING_OPPRETTET" TIMESTAMP (3),
	"KL_BEHANDLING_TYPE" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('BEHANDLING_TYPE') VIRTUAL ,
	"KL_FAGSAK_YTELSE_TYPE" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('FAGSAK_YTELSE') VIRTUAL ,
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3),
	"FORSTE_STONADSDAG" DATE,
	"BEHANDLING_STATUS" VARCHAR2(100 CHAR),
	"UTFORT_FRA_ADMIN" VARCHAR2(1 CHAR) DEFAULT 'N',
	"OPPGAVE_AVSLUTTET" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "OPPGAVE"  IS 'Tabell som skal inneholde informasjon om behandlinger som trenger intervensjon av en saksbehandler';
   COMMENT ON COLUMN "OPPGAVE"."ID" IS 'PK';
   COMMENT ON COLUMN "OPPGAVE"."BEHANDLING_ID" IS 'ID for behandlingeng';
   COMMENT ON COLUMN "OPPGAVE"."FAGSAK_SAKSNR" IS 'Referansenr for fagsak';
   COMMENT ON COLUMN "OPPGAVE"."AKTOR_ID" IS 'Aktør id';
   COMMENT ON COLUMN "OPPGAVE"."BEHANDLENDE_ENHET" IS 'Enheten som har oppgaven satt til seg';
   COMMENT ON COLUMN "OPPGAVE"."AKTIV" IS 'Setter oppgaveegenskap til aktiv eller inaktiv etter egenskapen sin tilstand';
   COMMENT ON COLUMN "OPPGAVE"."BEHANDLING_TYPE" IS 'Hva slags behandlingstype behandlingen har';
   COMMENT ON COLUMN "OPPGAVE"."FAGSAK_YTELSE_TYPE" IS 'Hva slags ytelse type fagsaken har';
   COMMENT ON COLUMN "OPPGAVE"."SYSTEM" IS 'Hvilket system oppgaven kommer fra';
   COMMENT ON COLUMN "OPPGAVE"."BEHANDLINGSFRIST" IS 'Behandlingsfrist';
   COMMENT ON COLUMN "OPPGAVE"."BEHANDLING_OPPRETTET" IS 'Tidspunktet behandlingen ble opprettet';
   COMMENT ON COLUMN "OPPGAVE"."KL_BEHANDLING_TYPE" IS 'Kodeverk behandlingtype';
   COMMENT ON COLUMN "OPPGAVE"."KL_FAGSAK_YTELSE_TYPE" IS 'Kodeverk fagsakytelsetype';
   COMMENT ON COLUMN "OPPGAVE"."FORSTE_STONADSDAG" IS 'Første stønadsdag';
   COMMENT ON COLUMN "OPPGAVE"."BEHANDLING_STATUS" IS 'Status for behandlingen';
   COMMENT ON COLUMN "OPPGAVE"."UTFORT_FRA_ADMIN" IS 'Settes til true når Oppgave er endret fra admin-tjeneste';
   COMMENT ON COLUMN "OPPGAVE"."OPPGAVE_AVSLUTTET" IS 'Tidspunkt for avslutting av Oppgave';
--------------------------------------------------------
--  DDL for Table OPPGAVE_EGENSKAP
--------------------------------------------------------

  CREATE TABLE "OPPGAVE_EGENSKAP"
   (	"ID" NUMBER(19,0),
	"OPPGAVE_ID" NUMBER(19,0),
	"ANDRE_KRITERIER_TYPE" VARCHAR2(100 CHAR),
	"KL_ANDRE_KRITERIER_TYPE" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('ANDRE_KRITERIER') VIRTUAL ,
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3),
	"SISTE_SAKSBEHANDLER_FOR_TOTR" VARCHAR2(20 CHAR),
	"AKTIV" VARCHAR2(1 CHAR) DEFAULT 'J'
   ) ;

   COMMENT ON TABLE "OPPGAVE_EGENSKAP"  IS 'Tabell inneholder filtreringer for andre kriterier';
   COMMENT ON COLUMN "OPPGAVE_EGENSKAP"."ID" IS 'PK';
   COMMENT ON COLUMN "OPPGAVE_EGENSKAP"."OPPGAVE_ID" IS 'Oppgave egenskapen hører til';
   COMMENT ON COLUMN "OPPGAVE_EGENSKAP"."ANDRE_KRITERIER_TYPE" IS 'Kode for de andre kriteriene oppgavene skal filtreres på';
   COMMENT ON COLUMN "OPPGAVE_EGENSKAP"."KL_ANDRE_KRITERIER_TYPE" IS 'Kodeverk andre kriterier';
   COMMENT ON COLUMN "OPPGAVE_EGENSKAP"."SISTE_SAKSBEHANDLER_FOR_TOTR" IS 'Saksbehandleren som sendte behandlingen til totrinnskontroll';
--------------------------------------------------------
--  DDL for Table OPPGAVE_EVENT_LOGG
--------------------------------------------------------

  CREATE TABLE "OPPGAVE_EVENT_LOGG"
   (	"ID" NUMBER(19,0),
	"BEHANDLING_ID" NUMBER(19,0),
	"EVENT_TYPE" VARCHAR2(100 CHAR),
	"ANDRE_KRITERIER_TYPE" VARCHAR2(100 CHAR),
	"KL_ANDRE_KRITERIER_TYPE" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('ANDRE_KRITERIER') VIRTUAL ,
	"KL_EVENT_TYPE" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('OPPGAVE_EVENT_TYPE') VIRTUAL ,
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VLLOS',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3),
	"BEHANDLENDE_ENHET" VARCHAR2(10 CHAR),
	"FRIST_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "OPPGAVE_EVENT_LOGG"  IS 'Eventlogg for alt som gjøres med oppgaver';
   COMMENT ON COLUMN "OPPGAVE_EVENT_LOGG"."ID" IS 'PK';
   COMMENT ON COLUMN "OPPGAVE_EVENT_LOGG"."BEHANDLING_ID" IS 'Behandlingen det er koblet til';
   COMMENT ON COLUMN "OPPGAVE_EVENT_LOGG"."EVENT_TYPE" IS 'Typen event';
   COMMENT ON COLUMN "OPPGAVE_EVENT_LOGG"."ANDRE_KRITERIER_TYPE" IS 'Spesialkriterier som påvirker åpning og lukking av oppgaver';
   COMMENT ON COLUMN "OPPGAVE_EVENT_LOGG"."BEHANDLENDE_ENHET" IS 'Behandlende enhet for oppgaven';
   COMMENT ON COLUMN "OPPGAVE_EVENT_LOGG"."FRIST_TID" IS 'Behandling blir automatisk gjenopptatt etter dette tidspunktet';
--------------------------------------------------------
--  DDL for Table OPPGAVE_FILTRERING
--------------------------------------------------------

  CREATE TABLE "OPPGAVE_FILTRERING"
   (	"ID" NUMBER(19,0),
	"NAVN" VARCHAR2(100 CHAR),
	"SORTERING" VARCHAR2(100 CHAR),
	"KL_SORTERING" VARCHAR2(100 CHAR) GENERATED ALWAYS AS ('KO_SORTERING') VIRTUAL ,
	"AVDELING_ID" NUMBER(19,0),
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3),
	"ER_DYNAMISK_PERIODE" VARCHAR2(1 CHAR) DEFAULT 'N',
	"FOM_DATO" DATE,
	"TOM_DATO" DATE,
	"FOM_DAGER" NUMBER(19,0),
	"TOM_DAGER" NUMBER(19,0)
   ) ;

   COMMENT ON TABLE "OPPGAVE_FILTRERING"  IS 'Tabell som skal inneholde informasjon om lister som brukes til filtrering av oppgaver';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."ID" IS 'PK';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."NAVN" IS 'Navn på listen';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."SORTERING" IS 'Hva skal listen sorteres på';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."KL_SORTERING" IS 'Kodeverk sorteringstype';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."AVDELING_ID" IS 'Avdelingsenhet oppgavefiltreringa er koblet til';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."ER_DYNAMISK_PERIODE" IS 'Angir om intervallet for perioden er dynamisk satt';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."FOM_DATO" IS 'Datointervall fra og med dato';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."TOM_DATO" IS 'Datointervall til og med dato';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."FOM_DAGER" IS 'Datointervall fra og med dag';
   COMMENT ON COLUMN "OPPGAVE_FILTRERING"."TOM_DAGER" IS 'Datointervall til og med dag';
--------------------------------------------------------
--  DDL for Table RESERVASJON
--------------------------------------------------------

  CREATE TABLE "RESERVASJON"
   (	"ID" NUMBER(19,0),
	"OPPGAVE_ID" NUMBER(19,0),
	"RESERVERT_TIL" TIMESTAMP (3),
	"RESERVERT_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (6) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (6),
	"FLYTTET_AV" VARCHAR2(100 CHAR),
	"FLYTTET_TIDSPUNKT" TIMESTAMP (3),
	"BEGRUNNELSE" VARCHAR2(500 CHAR)
   ) ;

   COMMENT ON TABLE "RESERVASJON"  IS 'Tabell som skal inneholde reservasjoner av oppgaver';
   COMMENT ON COLUMN "RESERVASJON"."ID" IS 'PK';
   COMMENT ON COLUMN "RESERVASJON"."RESERVERT_TIL" IS 'Tidspunkt for når reservasjonen går ut';
   COMMENT ON COLUMN "RESERVASJON"."RESERVERT_AV" IS 'Saksbehandler ident til saksbehandler som har reservert oppgaven';
   COMMENT ON COLUMN "RESERVASJON"."FLYTTET_AV" IS 'Ident som har flyttet reservasjonen';
   COMMENT ON COLUMN "RESERVASJON"."FLYTTET_TIDSPUNKT" IS 'Tidspunkt for flytting av reservasjonen';
   COMMENT ON COLUMN "RESERVASJON"."BEGRUNNELSE" IS 'Begrunnelse for opphør/flytting av reservasjon';
--------------------------------------------------------
--  DDL for Table RESERVASJON_EVENT_LOGG
--------------------------------------------------------

  CREATE TABLE "RESERVASJON_EVENT_LOGG"
   (	"ID" NUMBER(19,0),
	"RESERVASJON_ID" NUMBER(19,0),
	"OPPGAVE_ID" NUMBER(19,0),
	"RESERVERT_TIL" TIMESTAMP (3),
	"RESERVERT_AV" VARCHAR2(20 CHAR),
	"FLYTTET_AV" VARCHAR2(100 CHAR),
	"FLYTTET_TIDSPUNKT" TIMESTAMP (3),
	"BEGRUNNELSE" VARCHAR2(500 CHAR),
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VLLOS',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "RESERVASJON_EVENT_LOGG"  IS 'Eventlogg for alt som gjøres med reservasjoner';
   COMMENT ON COLUMN "RESERVASJON_EVENT_LOGG"."ID" IS 'PK';
   COMMENT ON COLUMN "RESERVASJON_EVENT_LOGG"."RESERVASJON_ID" IS 'Id for reservasjonen';
   COMMENT ON COLUMN "RESERVASJON_EVENT_LOGG"."OPPGAVE_ID" IS 'Oppgaven det er koblet til';
   COMMENT ON COLUMN "RESERVASJON_EVENT_LOGG"."RESERVERT_TIL" IS 'Tidspunkt for hvor lenge reservasjonen er gyldig';
   COMMENT ON COLUMN "RESERVASJON_EVENT_LOGG"."RESERVERT_AV" IS 'Ident for hvem oppgaven er reservert på';
   COMMENT ON COLUMN "RESERVASJON_EVENT_LOGG"."FLYTTET_AV" IS 'Ident for hvem som evt. har flyttet reservasjonen';
   COMMENT ON COLUMN "RESERVASJON_EVENT_LOGG"."FLYTTET_TIDSPUNKT" IS 'Tidspunkt reservasjonen evt. er flyttet';
   COMMENT ON COLUMN "RESERVASJON_EVENT_LOGG"."BEGRUNNELSE" IS 'Begrunnelse for flytting/frigjøring av reservasjon';
--------------------------------------------------------
--  DDL for Table SAKSBEHANDLER
--------------------------------------------------------

  CREATE TABLE "SAKSBEHANDLER"
   (	"ID" NUMBER(19,0),
	"SAKSBEHANDLER_IDENT" VARCHAR2(20 CHAR),
	"VERSJON" NUMBER(19,0) DEFAULT 0,
	"OPPRETTET_AV" VARCHAR2(20 CHAR) DEFAULT 'VL',
	"OPPRETTET_TID" TIMESTAMP (3) DEFAULT systimestamp,
	"ENDRET_AV" VARCHAR2(20 CHAR),
	"ENDRET_TID" TIMESTAMP (3)
   ) ;

   COMMENT ON TABLE "SAKSBEHANDLER"  IS 'Tabell som inneholder saksbehandleren som kan løse oppgaver';
   COMMENT ON COLUMN "SAKSBEHANDLER"."ID" IS 'PK';
   COMMENT ON COLUMN "SAKSBEHANDLER"."SAKSBEHANDLER_IDENT" IS 'Saksbehandlerident';
-- INSERTING into AVDELING
Insert into AVDELING (ID,AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval,'4806','NAV Familie- og pensjonsytelser Drammen');
Insert into AVDELING (ID,AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval,'4842','NAV Familie- og pensjonsytelser Stord');
Insert into AVDELING (ID,AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval,'4833','NAV Familie- og pensjonsytelser Oslo 1');
Insert into AVDELING (ID,AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval,'4812','NAV Familie- og pensjonsytelser Bergen');
Insert into AVDELING (ID,AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval,'4847','NAV Familie- og pensjonsytelser Levanger');
Insert into AVDELING (ID,AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval,'4849','NAV Familie- og pensjonsytelser Tromsø');
Insert into AVDELING (ID,AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval,'4292','NAV Klageinstans Midt-Norge');
Insert into AVDELING (ID,AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval,'4802','NAV Familie- og pensjonsytelser Bærum');
Insert into AVDELING (ID,AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval,'2103','NAV Viken');

-- INSERTING into KODELISTE
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','PDF','PDF','Filtype PDF',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','PDFA','PDFA','Filtype PDFA',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','XML','XML','Filtype XML',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','AFP','AFP','Filtype AFP',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','AXML','AXML','Filtype AXML',to_date('06.07.2017','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','DLF','DLF','Filtype DLF',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','DOC','DOC','Filtype DOC',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','DOCX','DOCX','Filtype DOCX',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','JPEG','JPEG','Filtype JPEG',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','RTF','RTF','Filtype RTF',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','TIFF','TIFF','Filtype TIFF',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','XLS','XLS','Filtype XLS',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','XLSX','XLSX','Filtype XLSX',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ARKIV_FILTYPE','-',null,'Ikke definert',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'DOKUMENT_TYPE_ID','INNTEKTSMELDING','I000067',null,to_date('01.12.2017','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'DOKUMENT_TYPE_ID','-',null,'Ikke definert',to_date('01.12.2017','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'DOKUMENT_KATEGORI','KLGA','KA','Klage eller anke',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'DOKUMENT_KATEGORI','ITSKJ','IS','Ikke tolkbart skjema',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'DOKUMENT_KATEGORI','SOKN','SOK','Søknad',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'DOKUMENT_KATEGORI','ESKJ','ES','Elektronisk skjema',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'DOKUMENT_KATEGORI','-',null,'Ikke definert',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'MOTTAK_KANAL','ALTINN','ALTINN','Altinn',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'MOTTAK_KANAL','EIA','EIA','EIA',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'MOTTAK_KANAL','EKST_OPPS','EKST_OPPS','Eksternt oppslag',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'MOTTAK_KANAL','NAV_NO','NAV_NO','Ditt NAV',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'MOTTAK_KANAL','SKAN_NETS','SKAN_NETS','Skanning Nets',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'MOTTAK_KANAL','SKAN_PEN','SKAN_PEN','Skanning Pensjon',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'MOTTAK_KANAL','EESSI','EESSI','EESSI',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'MOTTAK_KANAL','-',null,'Ikke definert',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'VARIANT_FORMAT','PROD','PRODUKSJON','Produksjonsformat',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'VARIANT_FORMAT','ARKIV','ARKIV','Arkivformat',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'VARIANT_FORMAT','SKANM','SKANNING_META','Skanning metadata',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'VARIANT_FORMAT','BREVB','BREVBESTILLING','Brevbestilling data',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'VARIANT_FORMAT','ORIG','ORIGINAL','Originalformat',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'VARIANT_FORMAT','FULL','FULLVERSJON','Versjon med infotekster',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'VARIANT_FORMAT','SLADD','SLADDET','Sladdet format',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'VARIANT_FORMAT','PRDLF','PRODUKSJON_DLF','Produksjonsformat DLF',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'VARIANT_FORMAT','-',null,'Ikke definert',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KONFIG_VERDI_TYPE','BOOLEAN',null,'Støtter J(a) / N(ei) flagg',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KONFIG_VERDI_TYPE','PERIOD',null,'ISO 8601 Periode verdier.  Eks. P10M (10 måneder), P1D (1 dag) ',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KONFIG_VERDI_TYPE','DURATION',null,'ISO 8601 Duration (tid) verdier.  Eks. PT1H (1 time), PT1M (1 minutt) ',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KONFIG_VERDI_TYPE','INTEGER',null,'Heltallsverdier (positiv/negativ)',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KONFIG_VERDI_TYPE','STRING',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KONFIG_VERDI_TYPE','URI',null,'URI for å angi id til en ressurs',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KONFIG_VERDI_GRUPPE','INGEN',null,'Ingen gruppe definert (default).  Brukes istdf. NULL siden dette inngår i en Primary Key. Koder som ikke er del av en gruppe må alltid være unike.',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_TYPE','BT-006','ae0042','Dokumentinnsyn',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_TYPE','BT-002','ae0043','Førstegangsbehandling',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_TYPE','BT-003','ae0058','Klage',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_TYPE','BT-004','ae0028','Revurdering',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_TYPE','BT-005','ae0042','Søknad',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_TYPE','-',null,'Ukjent',to_date('01.07.2006','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KO_SORTERING','OPPRBEH',null,'Dato for opprettelse av behandling',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KO_SORTERING','BEHFRIST',null,'Dato for behandlingsfrist',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KO_SORTERING','-',null,'udefinert',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSAK_YTELSE','ES',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSAK_YTELSE','FP',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSAK_YTELSE','-',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ANDRE_KRITERIER','TIL_BESLUTTER',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ANDRE_KRITERIER','PAPIRSOKNAD',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'EVENTMOTTAK_STATUS','FEILET',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'EVENTMOTTAK_STATUS','FERDIG',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'OPPGAVE_EVENT_TYPE','OPPRETTET',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'OPPGAVE_EVENT_TYPE','LUKKET',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'OPPGAVE_EVENT_TYPE','VENT',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'OPPGAVE_EVENT_TYPE','GJENAPNET',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ANDRE_KRITERIER','-',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSAK_STATUS','OPPR',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSAK_STATUS','UBEH',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSAK_STATUS','LOP',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSAK_STATUS','AVSLU',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','ARENA','AO01',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','GRISEN','AO11',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','GOSYS','FS22',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','INFOTRYGD','IT01',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','HJE_HEL_ORT','OEBS',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','PESYS','PP01',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','VENTELONN','V2',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','UNNTAK','UFM',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','FPSAK','FS36',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','-',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','UTBETALINGSMELDING','OB36',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','MELOSYS','FS38',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','JOARK','AS36',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','TPS','FS03',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','MEDL','FS18',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','INNTEKT','FS28',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','GSAK','FS19',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','BISYS','BID',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','SKANNING','MOT',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','ØKONOMI','OKO',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','BIDRAGINNKREVING','BII',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','ØVRIG','OVR',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','ENHETSREGISTERET','ER01',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSYSTEM','AAREGISTERET','AR01',null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'KO_SORTERING','FORSTONAD',null,'Dato for første stønadsdag',to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'OPPGAVE_EVENT_TYPE','MANU_VENT',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_STATUS','AVSLU',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_STATUS','FVED',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_STATUS','IVED',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_STATUS','OPPRE',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_STATUS','UTRED',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'BEHANDLING_STATUS','-',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ANDRE_KRITERIER','UTBETALING_TIL_BRUKER',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ANDRE_KRITERIER','UTLANDSSAK',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'FAGSAK_YTELSE','SVP',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) values (seq_kodeliste.nextval,'ANDRE_KRITERIER','SOKT_GRADERING',null,null,to_date('01.01.2000','DD.MM.RRRR'),to_date('31.12.9999','DD.MM.RRRR'));
Insert into KODELISTE (ID,KODEVERK,KODE,OFFISIELL_KODE,BESKRIVELSE,GYLDIG_FOM,GYLDIG_TOM) VALUES (seq_kodeliste.nextval, 'BEHANDLING_TYPE', 'BT-008', 'ae0046', 'Anke', to_date('01.07.2019', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));


-- INSERTING into KODELISTE_NAVN_I18N
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','PDF','NB','PDF');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','PDFA','NB','PDFA');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','XML','NB','XML');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','AFP','NB','AFP');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','AXML','NB','AXML');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','DLF','NB','DLF');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','DOC','NB','DOC');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','DOCX','NB','DOCX');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','JPEG','NB','JPEG');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','RTF','NB','RTF');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','TIFF','NB','TIFF');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','XLS','NB','XLS');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','XLSX','NB','XLSX');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ARKIV_FILTYPE','-','NB','Ikke definert');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'DOKUMENT_TYPE_ID','INNTEKTSMELDING','NB','Opplysninger for å behandle krav om blant annet foreldrepenger');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'DOKUMENT_TYPE_ID','-','NB','Ikke definert');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'DOKUMENT_KATEGORI','KLGA','NB','Klage eller anke');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'DOKUMENT_KATEGORI','ITSKJ','NB','Ikke tolkbart skjema');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'DOKUMENT_KATEGORI','SOKN','NB','Søknad');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'DOKUMENT_KATEGORI','ESKJ','NB','Elektronisk skjema');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'DOKUMENT_KATEGORI','-','NB','Ikke definert');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'MOTTAK_KANAL','ALTINN','NB','Altinn');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'MOTTAK_KANAL','EIA','NB','EIA');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'MOTTAK_KANAL','EKST_OPPS','NB','Eksternt oppslag');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'MOTTAK_KANAL','NAV_NO','NB','Ditt NAV');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'MOTTAK_KANAL','SKAN_NETS','NB','Skanning Nets');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'MOTTAK_KANAL','SKAN_PEN','NB','Skanning Pensjon');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'MOTTAK_KANAL','EESSI','NB','EESSI');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'MOTTAK_KANAL','-','NB','Ikke definert');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'VARIANT_FORMAT','PROD','NB','Produksjonsformat');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'VARIANT_FORMAT','ARKIV','NB','Arkivformat');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'VARIANT_FORMAT','SKANM','NB','Skanning metadata');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'VARIANT_FORMAT','BREVB','NB','Brevbestilling data');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'VARIANT_FORMAT','ORIG','NB','Originalformat');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'VARIANT_FORMAT','FULL','NB','Versjon med infotekster');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'VARIANT_FORMAT','SLADD','NB','Sladdet format');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'VARIANT_FORMAT','PRDLF','NB','Produksjonsformat DLF');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'VARIANT_FORMAT','-','NB','Ikke definert');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KONFIG_VERDI_TYPE','BOOLEAN','NB','Boolske verdier');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KONFIG_VERDI_TYPE','PERIOD','NB','Periode verdier');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KONFIG_VERDI_TYPE','DURATION','NB','Periode verdier');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KONFIG_VERDI_TYPE','INTEGER','NB','Heltall');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KONFIG_VERDI_TYPE','STRING','NB','Streng verdier');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KONFIG_VERDI_TYPE','URI','NB','Uniform Resource Identifier');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KONFIG_VERDI_GRUPPE','INGEN','NB','-');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_TYPE','BT-006','NB','Dokumentinnsyn');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_TYPE','BT-002','NB','Førstegangsbehandling');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_TYPE','BT-003','NB','Klage');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_TYPE','BT-004','NB','Revurdering');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_TYPE','BT-005','NB','Søknad');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_TYPE','-','NB','Ukjent');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KO_SORTERING','OPPRBEH','NB','Dato for opprettelse av behandling');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KO_SORTERING','BEHFRIST','NB','Dato for behandlingsfrist');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KO_SORTERING','-','NB','Udefinert');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSAK_YTELSE','ES','NB','Engangsstønad');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSAK_YTELSE','FP','NB','Foreldrepenger');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSAK_YTELSE','-','NB','Ikke definert');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ANDRE_KRITERIER','TIL_BESLUTTER','NB','Til beslutter');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ANDRE_KRITERIER','PAPIRSOKNAD','NB','Registrer papirsøknad');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'EVENTMOTTAK_STATUS','FEILET','NB','Feilet');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'EVENTMOTTAK_STATUS','FERDIG','NB','Ferdig lagt inn.');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'OPPGAVE_EVENT_TYPE','OPPRETTET','NB','Opprettet oppgave');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'OPPGAVE_EVENT_TYPE','LUKKET','NB','Lukket oppgave');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'OPPGAVE_EVENT_TYPE','VENT','NB','Lukket på grunn av satt på vent');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'OPPGAVE_EVENT_TYPE','GJENAPNET','NB','Lukket på grunn av satt på vent');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ANDRE_KRITERIER','-','NB','Ukjent');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSAK_STATUS','OPPR','NB','Opprettet');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSAK_STATUS','UBEH','NB','Under behandling');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSAK_STATUS','LOP','NB','Løpende');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSAK_STATUS','AVSLU','NB','Avsluttet');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','ARENA','NB','Arena');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','GRISEN','NB','Grisen');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','GOSYS','NB','Gosys');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','INFOTRYGD','NB','Infotrygd');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','HJE_HEL_ORT','NB','Hjelpemidler, Helsetjenester og Ort. Hjelpemidler');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','PESYS','NB','Pesys');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','VENTELONN','NB','Ventelønn');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','UNNTAK','NB','Unntak');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','FPSAK','NB','Vedtaksløsning Foreldrepenger');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','-','NB','Ikke definert');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','UTBETALINGSMELDING','NB','Utbetalingsmelding');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','MELOSYS','NB','Melosys');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','JOARK','NB','Joark');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','TPS','NB','TPS');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','MEDL','NB','MEDL');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','INNTEKT','NB','INNTEKT');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','GSAK','NB','GSAK');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','BISYS','NB','BID');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','SKANNING','NB','MOT');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','ØKONOMI','NB','OKO');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','BIDRAGINNKREVING','NB','BII');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','ØVRIG','NB','ØVRIG');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','ENHETSREGISTERET','NB','Enhetsregisteret');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSYSTEM','AAREGISTERET','NB','AAregisteret');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'KO_SORTERING','FORSTONAD','NB','Dato for første stønadsdag');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'OPPGAVE_EVENT_TYPE','MANU_VENT','NB','Lukket på grunn av manuelt satt på vent');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_STATUS','AVSLU','NB','Avsluttet');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_STATUS','FVED','NB','Fatter vedtak');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_STATUS','IVED','NB','Iverksetter vedtak');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_STATUS','OPPRE','NB','Opprettet');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_STATUS','UTRED','NB','Behandling utredes');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'BEHANDLING_STATUS','-','NB','Ikke definert');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ANDRE_KRITERIER','UTBETALING_TIL_BRUKER','NB','Utbetaling til bruker');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ANDRE_KRITERIER','UTLANDSSAK','NB','Utland');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'FAGSAK_YTELSE','SVP','NB','Svangerskapspenger');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) values (SEQ_KODELISTE_NAVN_I18N.nextval,'ANDRE_KRITERIER','SOKT_GRADERING','NB','Søkt gradering');
Insert into KODELISTE_NAVN_I18N (ID,KL_KODEVERK,KL_KODE,SPRAK,NAVN) VALUES (seq_kodeliste.nextval, 'BEHANDLING_TYPE', 'BT-008', 'NB', 'Anke');
-- INSERTING into KODEVERK
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('BEHANDLING_TEMA','Kodeverkforvaltning','http://nav.no/kodeverk/Kodeverk/Behandlingstema','9','Behandlingstema','N','N','Behandlingstema','NAV Behandlingstema', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('TEMA','Kodeverkforvaltning','http://nav.no/kodeverk/Kodeverk/Tema','2','Tema','N','N','Tema','NAV Tema', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('DOKUMENT_TYPE_ID','Kodeverkforvaltning','http://nav.no/kodeverk/Kodeverk/DokumentTypeId-er','2','DokumentTypeId-er','J','N','DokumentTypeId-er','Typen til et mottatt dokument. Dette er et subset av DokumentTyper; inngÃ¥ende dokumenter, for eksempel sÃ¸knad, terminbekreftelse o.l', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('ARKIV_FILTYPE','Kodeverkforvaltning','http://nav.no/kodeverk/Kodeverk/Arkivfiltyper','3','Arkivfiltyper','N','N','Arkivfiltyper','NAV Arkivfiltyper', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('KONFIG_VERDI_GRUPPE','VL',null,null,null,'N','N','KonfigVerdiGruppe','Angir en gruppe konfigurerbare verdier tilhører. Det åpner for å kunne ha lister og Maps av konfigurerbare verdier', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('KONFIG_VERDI_TYPE','VL',null,null,null,'N','N','KonfigVerdiType','Angir type den konfigurerbare verdien er av slik at dette kan brukes til validering og fremstilling.', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('DOKUMENT_KATEGORI','Kodeverkforvaltning','http://nav.no/kodeverk/Kodeverk/Dokumentkategorier','1','Dokumentkategorier','N','N','Dokumentkategorier','NAV Dokumentkategorier', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('FAGSYSTEM','VL',null,null,'Fagsystemer','N','N','Fagsystemer','NAV Fagsystemer', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('MOTTAK_KANAL','Kodeverkforvaltning','http://nav.no/kodeverk/Kodeverk/Mottakskanaler','1','Mottakskanaler','N','N','Mottakskanaler','NAV Mottakskanaler', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('VARIANT_FORMAT','Kodeverkforvaltning','http://nav.no/kodeverk/Kodeverk/Variantformater','1','Variantformater','N','N','Variantformater','NAV Variantformater', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('RELATERT_YTELSE_TEMA','Arena',null,null,null,'N','N','RelatertYtelseTema','Kodeverk for tema på relaterte ytelser.', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('RELATERT_YTELSE_BEH_TEMA','Arena',null,null,null,'N','N','RelatertYtelseBehandlingTema','Kodeverk for behandlingstema på relaterte ytelser', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('BEHANDLING_TYPE','Kodeverkforvaltning','http://nav.no/kodeverk/Kodeverk/Arkivfiltyper','5','Behandlingstyper','N','N','Behandlingstyper','Internt kodeverk for behandlingstyper.', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('KO_SORTERING','FPLOS',null,null,null,'J','J','Valgt sortering for kø','Mulige sorteringer for saksbehandleres køer', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('FAGSAK_YTELSE','VL',null,null,null,'J','J','FagsakYtelseType','Internt kodeverk for ytelsestype.', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('ANDRE_KRITERIER','VLLOS',null,null,null,'J','J','ANDRE_KRITERIER','Internt kodeverk for andre kriterier for filtrering.','N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('EVENTMOTTAK_STATUS','VLLOS',null,null,null,'J','J','EVENTMOTTAK_STATUS','Internt kodeverk for status på eventmottak.','N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('OPPGAVE_EVENT_TYPE','VLLOS',null,null,null,'J','J','OPPGAVE_EVENT_TYPE','Internt kodeverk for hva som har skjedd med oppgaver.','N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('FAGSAK_STATUS','VL',null,null,null,'J','J','FagsakStatus','Internt kodeverk for statuser på fagsaker.', 'N');
Insert into KODEVERK (KODE,KODEVERK_EIER,KODEVERK_EIER_REF,KODEVERK_EIER_VER,KODEVERK_EIER_NAVN,KODEVERK_SYNK_NYE,KODEVERK_SYNK_EKSISTERENDE,NAVN,BESKRIVELSE,SAMMENSATT) values ('BEHANDLING_STATUS','VL',null,null,null,'J','J','BehandlingStatus','Internt kodeverk for behandling status.', 'N');
-- INSERTING into KONFIG_VERDI
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'behandling.venter.frist.lengde','INGEN','P2W',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'virtuell.saksbehandler.navn','INGEN','Vedtaksløsning Prosess',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'relaterte.ytelser.periode.start','INGEN','P10M',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'vedtak.klagefrist.uker','INGEN','6',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'rettskilde.url','INGEN','https://www.nav.no/rettskildene-intern/',to_date('01.01.2017','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'bruker.gruppenavn.saksbehandler','INGEN','0000-GA-fpsak-saksbehandler',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'bruker.gruppenavn.veileder','INGEN','0000-GA-fpsak-veileder',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'bruker.gruppenavn.beslutter','INGEN','0000-GA-fpsak-beslutter',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'bruker.gruppenavn.overstyrer','INGEN','0000-GA-fpsak-manuelt-overstyrer',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'bruker.gruppenavn.egenansatt','INGEN','0000-GA-GOSYS_UTVIDET',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'bruker.gruppenavn.kode6','INGEN','0000-GA-GOSYS_KODE6',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'bruker.gruppenavn.kode7','INGEN','0000-GA-GOSYS_KODE7',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'systemrutine.url','INGEN','https://navet.adeo.no/ansatt/Fag/Regelverk+og+rutiner/Saksbehandling',to_date('01.01.2016','DD.MM.RRRR'));
Insert into KONFIG_VERDI (ID,KONFIG_KODE,KONFIG_GRUPPE,KONFIG_VERDI,GYLDIG_FOM) values (SEQ_KONFIG_VERDI.nextval,'bruker.gruppenavn.oppgavestyrer','INGEN','0000-GA-fpsak-Oppgavestyrer',to_date('01.01.2016','DD.MM.RRRR'));
-- INSERTING into KONFIG_VERDI_GRUPPE
Insert into KONFIG_VERDI_GRUPPE (KODE,NAVN,BESKRIVELSE) values ('INGEN','-','Ingen gruppe definert (default).  Brukes istdf. NULL siden dette inngår i en Primary Key. Koder som ikke er del av en gruppe må alltid være unike.');
-- INSERTING into KONFIG_VERDI_KODE
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('behandling.venter.frist.lengde','INGEN','Frist - Behandling venter','PERIOD','Sett behandling på vent  (i en angitt periode, eks. P2W = 2 uker');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('virtuell.saksbehandler.navn','INGEN','Virtuell saksbehandler navn','STRING','Setter saksbehandler navn når prosessen er gått automatisk. Kun for visning internt i løsning');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('relaterte.ytelser.periode.start','INGEN','Relaterte ytelser periode start','PERIOD','Periode bakover i tid fra dagens dato det skal søkes etter relaterte ytelser i Infotrygd og Arena. Default P10M (10 måneder) før dagens dato');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('vedtak.klagefrist.uker','INGEN','Vedtak klagefrist','INTEGER',' Klagefrist i uker (positivt heltall), sendes i vedtaksbrev til brukeren');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('rettskilde.url','INGEN','Rettskilde URL','URI','Url som peker på rettkildene som brukes som beslutningsstøtte');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('bruker.gruppenavn.saksbehandler','INGEN','Gruppenavn for rolle saksbehandler','STRING','Gruppenavn for rolle saksbehandler');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('bruker.gruppenavn.veileder','INGEN','Gruppenavn for rolle veileder','STRING','Gruppenavn for rolle veileder');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('bruker.gruppenavn.beslutter','INGEN','Gruppenavn for rolle beslutter','STRING','Gruppenavn for rolle beslutter');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('bruker.gruppenavn.overstyrer','INGEN','Gruppenavn for rolle overstyrer','STRING','Gruppenavn for rolle overstyrer');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('bruker.gruppenavn.egenansatt','INGEN','Gruppenavn for rolle egen ansatt','STRING','Gruppenavn for rolle egen ansatt');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('bruker.gruppenavn.kode6','INGEN','Gruppenavn for rolle kode 6','STRING','Gruppenavn for rolle kode 6');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('bruker.gruppenavn.kode7','INGEN','Gruppenavn for rolle kode 7','STRING','Gruppenavn for rolle kode 7');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('systemrutine.url','INGEN','Systemrutiner URL','URI','Lenke til systemrutiner');
Insert into KONFIG_VERDI_KODE (KODE,KONFIG_GRUPPE,NAVN,KONFIG_TYPE,BESKRIVELSE) values ('bruker.gruppenavn.oppgavestyrer','INGEN','Gruppenavn for rolle oppgavestyrer','STRING','Gruppenavn for rolle egen oppgavestyrer');
-- INSERTING into KONFIG_VERDI_TYPE
Insert into KONFIG_VERDI_TYPE (KODE,NAVN,BESKRIVELSE) values ('BOOLEAN','Boolske verdier','Støtter J(a) / N(ei) flagg');
Insert into KONFIG_VERDI_TYPE (KODE,NAVN,BESKRIVELSE) values ('PERIOD','Periode verdier','ISO 8601 Periode verdier.  Eks. P10M (10 måneder), P1D (1 dag) ');
Insert into KONFIG_VERDI_TYPE (KODE,NAVN,BESKRIVELSE) values ('DURATION','Periode verdier','ISO 8601 Duration (tid) verdier.  Eks. PT1H (1 time), PT1M (1 minutt) ');
Insert into KONFIG_VERDI_TYPE (KODE,NAVN,BESKRIVELSE) values ('INTEGER','Heltall','Heltallsverdier (positiv/negativ)');
Insert into KONFIG_VERDI_TYPE (KODE,NAVN,BESKRIVELSE) values ('STRING','Streng verdier',null);
Insert into KONFIG_VERDI_TYPE (KODE,NAVN,BESKRIVELSE) values ('URI','Uniform Resource Identifier','URI for å angi id til en ressurs');

--------------------------------------------------------
--  DDL for Index IDX_AVDELING_SAKSBEHANDLER_1
--------------------------------------------------------

  CREATE INDEX "IDX_AVDELING_SAKSBEHANDLER_1" ON "AVDELING_SAKSBEHANDLER" ("SAKSBEHANDLER_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_AVDELING_SAKSBEHANDLER_2
--------------------------------------------------------

  CREATE INDEX "IDX_AVDELING_SAKSBEHANDLER_2" ON "AVDELING_SAKSBEHANDLER" ("AVDELING_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_AVDELING_1
--------------------------------------------------------

  CREATE INDEX "IDX_AVDELING_1" ON "AVDELING" ("AVDELING_ENHET")
  ;
--------------------------------------------------------
--  DDL for Index IDX_EVENTMOTTAK_FEILLOGG_1
--------------------------------------------------------

  CREATE INDEX "IDX_EVENTMOTTAK_FEILLOGG_1" ON "EVENTMOTTAK_FEILLOGG" ("STATUS")
  ;
--------------------------------------------------------
--  DDL for Index IDX_EVENTMOTTAK_FEILLOGG_2
--------------------------------------------------------

  CREATE INDEX "IDX_EVENTMOTTAK_FEILLOGG_2" ON "EVENTMOTTAK_FEILLOGG" ("SISTE_KJORING_TS")
  ;
--------------------------------------------------------
--  DDL for Index IDX_FILTR_BEHANDLING_TYPE_1
--------------------------------------------------------

  CREATE INDEX "IDX_FILTR_BEHANDLING_TYPE_1" ON "FILTRERING_BEHANDLING_TYPE" ("OPPGAVE_FILTRERING_ID", "BEHANDLING_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_FILTR_BEHANDLING_TYPE_2
--------------------------------------------------------

  CREATE INDEX "IDX_FILTR_BEHANDLING_TYPE_2" ON "FILTRERING_BEHANDLING_TYPE" ("BEHANDLING_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_FILTRERING_ANDRE_KRIT_1
--------------------------------------------------------

  CREATE INDEX "IDX_FILTRERING_ANDRE_KRIT_1" ON "FILTRERING_ANDRE_KRITERIER" ("OPPGAVE_FILTRERING_ID", "ANDRE_KRITERIER_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_FILTRERING_ANDRE_KRIT_2
--------------------------------------------------------

  CREATE INDEX "IDX_FILTRERING_ANDRE_KRIT_2" ON "FILTRERING_ANDRE_KRITERIER" ("ANDRE_KRITERIER_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_FILTRERING_SAKSBEHANDLER_1
--------------------------------------------------------

  CREATE INDEX "IDX_FILTRERING_SAKSBEHANDLER_1" ON "FILTRERING_SAKSBEHANDLER" ("SAKSBEHANDLER_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_FILTRERING_SAKSBEHANDLER_2
--------------------------------------------------------

  CREATE INDEX "IDX_FILTRERING_SAKSBEHANDLER_2" ON "FILTRERING_SAKSBEHANDLER" ("OPPGAVE_FILTRERING_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_FILTR_YTELSE_TYPE_1
--------------------------------------------------------

  CREATE INDEX "IDX_FILTR_YTELSE_TYPE_1" ON "FILTRERING_YTELSE_TYPE" ("OPPGAVE_FILTRERING_ID", "FAGSAK_YTELSE_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_FILTR_YTELSE_TYPE_2
--------------------------------------------------------

  CREATE INDEX "IDX_FILTR_YTELSE_TYPE_2" ON "FILTRERING_YTELSE_TYPE" ("FAGSAK_YTELSE_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_KODELISTE_1
--------------------------------------------------------

  CREATE INDEX "IDX_KODELISTE_1" ON "KODELISTE" ("KODE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_KODELISTE_2
--------------------------------------------------------

  CREATE INDEX "IDX_KODELISTE_2" ON "KODELISTE" ("OFFISIELL_KODE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_KODELISTE_3
--------------------------------------------------------

  CREATE INDEX "IDX_KODELISTE_3" ON "KODELISTE" ("GYLDIG_FOM")
  ;
--------------------------------------------------------
--  DDL for Index IDX_KODELISTE_6
--------------------------------------------------------

  CREATE INDEX "IDX_KODELISTE_6" ON "KODELISTE" ("KODEVERK")
  ;
--------------------------------------------------------
--  DDL for Index IDX_KONFIG_VERDI_KODE_6
--------------------------------------------------------

  CREATE INDEX "IDX_KONFIG_VERDI_KODE_6" ON "KONFIG_VERDI_KODE" ("KONFIG_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_KONFIG_VERDI_KODE_7
--------------------------------------------------------

  CREATE INDEX "IDX_KONFIG_VERDI_KODE_7" ON "KONFIG_VERDI_KODE" ("KONFIG_GRUPPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_KONFIG_VERDI_1
--------------------------------------------------------

  CREATE INDEX "IDX_KONFIG_VERDI_1" ON "KONFIG_VERDI" ("GYLDIG_FOM", "GYLDIG_TOM")
  ;
--------------------------------------------------------
--  DDL for Index IDX_KONFIG_VERDI_2
--------------------------------------------------------

  CREATE INDEX "IDX_KONFIG_VERDI_2" ON "KONFIG_VERDI" ("KONFIG_GRUPPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_KONFIG_VERDI_3
--------------------------------------------------------

  CREATE INDEX "IDX_KONFIG_VERDI_3" ON "KONFIG_VERDI" ("KONFIG_KODE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_EGENSKAP_1
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_EGENSKAP_1" ON "OPPGAVE_EGENSKAP" ("OPPGAVE_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_EGENSKAP_2
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_EGENSKAP_2" ON "OPPGAVE_EGENSKAP" ("ANDRE_KRITERIER_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_EGENSKAP_3
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_EGENSKAP_3" ON "OPPGAVE_EGENSKAP" ("ANDRE_KRITERIER_TYPE", UPPER("SISTE_SAKSBEHANDLER_FOR_TOTR"))
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_EVENT_LOGG_1
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_EVENT_LOGG_1" ON "OPPGAVE_EVENT_LOGG" ("BEHANDLING_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_EVENT_LOGG_2
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_EVENT_LOGG_2" ON "OPPGAVE_EVENT_LOGG" ("EVENT_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_EVENT_LOGG_3
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_EVENT_LOGG_3" ON "OPPGAVE_EVENT_LOGG" ("ANDRE_KRITERIER_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_FILTRERING_1
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_FILTRERING_1" ON "OPPGAVE_FILTRERING" ("AVDELING_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_1
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_1" ON "OPPGAVE" ("BEHANDLING_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_2
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_2" ON "OPPGAVE" ("FAGSAK_YTELSE_TYPE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_3
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_3" ON "OPPGAVE" ("AKTIV", "BEHANDLENDE_ENHET")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_5
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_5" ON "OPPGAVE" ("BEHANDLINGSFRIST")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_6
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_6" ON "OPPGAVE" ("BEHANDLING_OPPRETTET")
  ;
--------------------------------------------------------
--  DDL for Index IDX_OPPGAVE_7
--------------------------------------------------------

  CREATE INDEX "IDX_OPPGAVE_7" ON "OPPGAVE" ("FORSTE_STONADSDAG")
  ;
--------------------------------------------------------
--  DDL for Index IDX_RESERVASJON_EVENT_LOGG_1
--------------------------------------------------------

  CREATE INDEX "IDX_RESERVASJON_EVENT_LOGG_1" ON "RESERVASJON_EVENT_LOGG" ("RESERVASJON_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_RESERVASJON_EVENT_LOGG_2
--------------------------------------------------------

  CREATE INDEX "IDX_RESERVASJON_EVENT_LOGG_2" ON "RESERVASJON_EVENT_LOGG" ("OPPGAVE_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_RESERVASJON_1
--------------------------------------------------------

  CREATE INDEX "IDX_RESERVASJON_1" ON "RESERVASJON" ("OPPGAVE_ID")
  ;
--------------------------------------------------------
--  DDL for Index IDX_SAKSBEHANDLER_1
--------------------------------------------------------

  CREATE INDEX "IDX_SAKSBEHANDLER_1" ON "SAKSBEHANDLER" ("SAKSBEHANDLER_IDENT")
  ;
--------------------------------------------------------
--  DDL for Index PK_AVDELING
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_AVDELING" ON "AVDELING" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_AVDELING_SAKSBEHANDLER
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_AVDELING_SAKSBEHANDLER" ON "AVDELING_SAKSBEHANDLER" ("SAKSBEHANDLER_ID", "AVDELING_ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_EVENTMOTTAK_FEILLOGG
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_EVENTMOTTAK_FEILLOGG" ON "EVENTMOTTAK_FEILLOGG" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_FILTR_BEHANDLING_TYPE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_FILTR_BEHANDLING_TYPE" ON "FILTRERING_BEHANDLING_TYPE" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_FILTRERING_ANDRE_KRITERIER
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_FILTRERING_ANDRE_KRITERIER" ON "FILTRERING_ANDRE_KRITERIER" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_FILTRERING_SAKSBEHANDLER
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_FILTRERING_SAKSBEHANDLER" ON "FILTRERING_SAKSBEHANDLER" ("SAKSBEHANDLER_ID", "OPPGAVE_FILTRERING_ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_FILTRERING_YTELSE_TYPE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_FILTRERING_YTELSE_TYPE" ON "FILTRERING_YTELSE_TYPE" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_KODELISTE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_KODELISTE" ON "KODELISTE" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_KODELISTE_NAVN_I18N
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_KODELISTE_NAVN_I18N" ON "KODELISTE_NAVN_I18N" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_KODEVERK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_KODEVERK" ON "KODEVERK" ("KODE")
  ;
--------------------------------------------------------
--  DDL for Index PK_KONFIG_VERDI
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_KONFIG_VERDI" ON "KONFIG_VERDI" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_KONFIG_VERDI_GRUPPE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_KONFIG_VERDI_GRUPPE" ON "KONFIG_VERDI_GRUPPE" ("KODE")
  ;
--------------------------------------------------------
--  DDL for Index PK_KONFIG_VERDI_KODE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_KONFIG_VERDI_KODE" ON "KONFIG_VERDI_KODE" ("KODE", "KONFIG_GRUPPE")
  ;
--------------------------------------------------------
--  DDL for Index PK_KONFIG_VERDI_TYPE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_KONFIG_VERDI_TYPE" ON "KONFIG_VERDI_TYPE" ("KODE")
  ;
--------------------------------------------------------
--  DDL for Index PK_OPPGAVE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_OPPGAVE" ON "OPPGAVE" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_OPPGAVE_EGENSKAP
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_OPPGAVE_EGENSKAP" ON "OPPGAVE_EGENSKAP" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_OPPGAVE_EVENT_TYPE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_OPPGAVE_EVENT_TYPE" ON "OPPGAVE_EVENT_LOGG" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_OPPGAVE_FILTRERING
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_OPPGAVE_FILTRERING" ON "OPPGAVE_FILTRERING" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_RESERVASJON
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_RESERVASJON" ON "RESERVASJON" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_RESERVASJON_EVENT_LOGG
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_RESERVASJON_EVENT_LOGG" ON "RESERVASJON_EVENT_LOGG" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index PK_SAKSBEHANDLER
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_SAKSBEHANDLER" ON "SAKSBEHANDLER" ("ID")
  ;
--------------------------------------------------------
--  DDL for Index UIDX_KODELISTE_1
--------------------------------------------------------

--  CREATE UNIQUE INDEX "UIDX_KODELISTE_1" ON "KODELISTE" ("KODE", "KODEVERK")
--  ;
--------------------------------------------------------
--  DDL for Index UIDX_KONFIG_VERDI_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "UIDX_KONFIG_VERDI_1" ON "KONFIG_VERDI" ("KONFIG_GRUPPE", "GYLDIG_TOM", "KONFIG_KODE")
  ;
--------------------------------------------------------
--  DDL for Index UIDX_KONFIG_VERDI_2
--------------------------------------------------------

  CREATE UNIQUE INDEX "UIDX_KONFIG_VERDI_2" ON "KONFIG_VERDI" ("KONFIG_KODE", "KONFIG_GRUPPE")
  ;
--------------------------------------------------------
--  Constraints for Table AVDELING
--------------------------------------------------------

  ALTER TABLE "AVDELING" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "AVDELING" MODIFY ("AVDELING_ENHET" NOT NULL ENABLE);
  ALTER TABLE "AVDELING" MODIFY ("NAVN" NOT NULL ENABLE);
  ALTER TABLE "AVDELING" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "AVDELING" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "AVDELING" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "AVDELING" ADD CONSTRAINT "PK_AVDELING" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table AVDELING_SAKSBEHANDLER
--------------------------------------------------------

  ALTER TABLE "AVDELING_SAKSBEHANDLER" MODIFY ("SAKSBEHANDLER_ID" NOT NULL ENABLE);
  ALTER TABLE "AVDELING_SAKSBEHANDLER" MODIFY ("AVDELING_ID" NOT NULL ENABLE);
  ALTER TABLE "AVDELING_SAKSBEHANDLER" ADD CONSTRAINT "PK_AVDELING_SAKSBEHANDLER" PRIMARY KEY ("SAKSBEHANDLER_ID", "AVDELING_ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table EVENTMOTTAK_FEILLOGG
--------------------------------------------------------

  ALTER TABLE "EVENTMOTTAK_FEILLOGG" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "EVENTMOTTAK_FEILLOGG" MODIFY ("MELDING" NOT NULL ENABLE);
  ALTER TABLE "EVENTMOTTAK_FEILLOGG" MODIFY ("STATUS" NOT NULL ENABLE);
  ALTER TABLE "EVENTMOTTAK_FEILLOGG" MODIFY ("KL_STATUS" NOT NULL ENABLE);
  ALTER TABLE "EVENTMOTTAK_FEILLOGG" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "EVENTMOTTAK_FEILLOGG" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "EVENTMOTTAK_FEILLOGG" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "EVENTMOTTAK_FEILLOGG" ADD CONSTRAINT "PK_EVENTMOTTAK_FEILLOGG" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table FILTRERING_ANDRE_KRITERIER
--------------------------------------------------------

  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" MODIFY ("OPPGAVE_FILTRERING_ID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" MODIFY ("ANDRE_KRITERIER_TYPE" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" MODIFY ("KL_ANDRE_KRITERIER_TYPE" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" ADD CONSTRAINT "PK_FILTRERING_ANDRE_KRITERIER" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" MODIFY ("INKLUDER" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table FILTRERING_BEHANDLING_TYPE
--------------------------------------------------------

  ALTER TABLE "FILTRERING_BEHANDLING_TYPE" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_BEHANDLING_TYPE" MODIFY ("OPPGAVE_FILTRERING_ID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_BEHANDLING_TYPE" MODIFY ("BEHANDLING_TYPE" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_BEHANDLING_TYPE" MODIFY ("KL_BEHANDLING_TYPE" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_BEHANDLING_TYPE" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_BEHANDLING_TYPE" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_BEHANDLING_TYPE" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_BEHANDLING_TYPE" ADD CONSTRAINT "PK_FILTR_BEHANDLING_TYPE" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table FILTRERING_SAKSBEHANDLER
--------------------------------------------------------

  ALTER TABLE "FILTRERING_SAKSBEHANDLER" MODIFY ("SAKSBEHANDLER_ID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_SAKSBEHANDLER" MODIFY ("OPPGAVE_FILTRERING_ID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_SAKSBEHANDLER" ADD CONSTRAINT "PK_FILTRERING_SAKSBEHANDLER" PRIMARY KEY ("SAKSBEHANDLER_ID", "OPPGAVE_FILTRERING_ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table FILTRERING_YTELSE_TYPE
--------------------------------------------------------

  ALTER TABLE "FILTRERING_YTELSE_TYPE" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_YTELSE_TYPE" MODIFY ("OPPGAVE_FILTRERING_ID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_YTELSE_TYPE" MODIFY ("FAGSAK_YTELSE_TYPE" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_YTELSE_TYPE" MODIFY ("KL_FAGSAK_YTELSE_TYPE" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_YTELSE_TYPE" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_YTELSE_TYPE" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_YTELSE_TYPE" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "FILTRERING_YTELSE_TYPE" ADD CONSTRAINT "PK_FILTRERING_YTELSE_TYPE" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;

--------------------------------------------------------
--  Constraints for Table KODELISTE
--------------------------------------------------------

  ALTER TABLE "KODELISTE" ADD CONSTRAINT "CHK_UNIQUE_KODELISTE" UNIQUE ("KODE", "KODEVERK")
  USING INDEX (CREATE UNIQUE INDEX "UIDX_KODELISTE_1" ON "KODELISTE" ("KODE", "KODEVERK")
  )  ENABLE;
  ALTER TABLE "KODELISTE" ADD CONSTRAINT "PK_KODELISTE" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
  ALTER TABLE "KODELISTE" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE" MODIFY ("GYLDIG_TOM" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE" MODIFY ("GYLDIG_FOM" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE" MODIFY ("KODE" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE" MODIFY ("KODEVERK" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE" MODIFY ("ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table KODELISTE_NAVN_I18N
--------------------------------------------------------

  ALTER TABLE "KODELISTE_NAVN_I18N" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE_NAVN_I18N" MODIFY ("KL_KODEVERK" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE_NAVN_I18N" MODIFY ("KL_KODE" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE_NAVN_I18N" MODIFY ("SPRAK" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE_NAVN_I18N" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE_NAVN_I18N" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "KODELISTE_NAVN_I18N" ADD CONSTRAINT "PK_KODELISTE_NAVN_I18N" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table KODEVERK
--------------------------------------------------------

  ALTER TABLE "KODEVERK" MODIFY ("NAVN" NOT NULL ENABLE);
  ALTER TABLE "KODEVERK" MODIFY ("KODEVERK_SYNK_EKSISTERENDE" NOT NULL ENABLE);
  ALTER TABLE "KODEVERK" MODIFY ("KODEVERK_SYNK_NYE" NOT NULL ENABLE);
  ALTER TABLE "KODEVERK" MODIFY ("KODEVERK_EIER" NOT NULL ENABLE);
  ALTER TABLE "KODEVERK" MODIFY ("KODE" NOT NULL ENABLE);
  ALTER TABLE "KODEVERK" ADD CONSTRAINT "PK_KODEVERK" PRIMARY KEY ("KODE")
  USING INDEX  ENABLE;
  ALTER TABLE "KODEVERK" ADD CHECK (kodeverk_synk_eksisterende IN ('J', 'N')) ENABLE;
  ALTER TABLE "KODEVERK" ADD CHECK (kodeverk_synk_nye IN ('J', 'N')) ENABLE;
  ALTER TABLE "KODEVERK" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "KODEVERK" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table KONFIG_VERDI
--------------------------------------------------------

  ALTER TABLE "KONFIG_VERDI" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI" MODIFY ("KONFIG_KODE" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI" MODIFY ("KONFIG_GRUPPE" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI" MODIFY ("GYLDIG_FOM" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI" MODIFY ("GYLDIG_TOM" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI" ADD CONSTRAINT "PK_KONFIG_VERDI" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table KONFIG_VERDI_GRUPPE
--------------------------------------------------------

  ALTER TABLE "KONFIG_VERDI_GRUPPE" MODIFY ("KODE" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_GRUPPE" MODIFY ("NAVN" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_GRUPPE" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_GRUPPE" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_GRUPPE" ADD CONSTRAINT "PK_KONFIG_VERDI_GRUPPE" PRIMARY KEY ("KODE")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table KONFIG_VERDI_KODE
--------------------------------------------------------

  ALTER TABLE "KONFIG_VERDI_KODE" MODIFY ("KODE" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_KODE" MODIFY ("KONFIG_GRUPPE" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_KODE" MODIFY ("NAVN" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_KODE" MODIFY ("KONFIG_TYPE" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_KODE" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_KODE" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_KODE" ADD CONSTRAINT "PK_KONFIG_VERDI_KODE" PRIMARY KEY ("KODE", "KONFIG_GRUPPE")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table KONFIG_VERDI_TYPE
--------------------------------------------------------

  ALTER TABLE "KONFIG_VERDI_TYPE" MODIFY ("KODE" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_TYPE" MODIFY ("NAVN" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_TYPE" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_TYPE" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "KONFIG_VERDI_TYPE" ADD CONSTRAINT "PK_KONFIG_VERDI_TYPE" PRIMARY KEY ("KODE")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table OPPGAVE
--------------------------------------------------------

  ALTER TABLE "OPPGAVE" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("BEHANDLING_ID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("UTFORT_FRA_ADMIN" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("FAGSAK_SAKSNR" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("BEHANDLENDE_ENHET" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("AKTIV" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("BEHANDLING_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("FAGSAK_YTELSE_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("KL_BEHANDLING_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("KL_FAGSAK_YTELSE_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE" ADD CONSTRAINT "PK_OPPGAVE" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table OPPGAVE_EGENSKAP
--------------------------------------------------------

  ALTER TABLE "OPPGAVE_EGENSKAP" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EGENSKAP" MODIFY ("OPPGAVE_ID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EGENSKAP" MODIFY ("ANDRE_KRITERIER_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EGENSKAP" MODIFY ("KL_ANDRE_KRITERIER_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EGENSKAP" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EGENSKAP" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EGENSKAP" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EGENSKAP" ADD CONSTRAINT "PK_OPPGAVE_EGENSKAP" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
  ALTER TABLE "OPPGAVE_EGENSKAP" MODIFY ("AKTIV" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table OPPGAVE_EVENT_LOGG
--------------------------------------------------------

  ALTER TABLE "OPPGAVE_EVENT_LOGG" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EVENT_LOGG" MODIFY ("BEHANDLING_ID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EVENT_LOGG" MODIFY ("EVENT_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EVENT_LOGG" MODIFY ("KL_ANDRE_KRITERIER_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EVENT_LOGG" MODIFY ("KL_EVENT_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EVENT_LOGG" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EVENT_LOGG" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EVENT_LOGG" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_EVENT_LOGG" ADD CONSTRAINT "PK_OPPGAVE_EVENT_TYPE" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table OPPGAVE_FILTRERING
--------------------------------------------------------

  ALTER TABLE "OPPGAVE_FILTRERING" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_FILTRERING" MODIFY ("NAVN" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_FILTRERING" MODIFY ("SORTERING" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_FILTRERING" MODIFY ("KL_SORTERING" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_FILTRERING" MODIFY ("AVDELING_ID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_FILTRERING" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_FILTRERING" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_FILTRERING" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "OPPGAVE_FILTRERING" ADD CONSTRAINT "PK_OPPGAVE_FILTRERING" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
  ALTER TABLE "OPPGAVE_FILTRERING" MODIFY ("ER_DYNAMISK_PERIODE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table RESERVASJON
--------------------------------------------------------

  ALTER TABLE "RESERVASJON" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON" MODIFY ("OPPGAVE_ID" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON" MODIFY ("RESERVERT_AV" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON" ADD CONSTRAINT "PK_RESERVASJON" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table RESERVASJON_EVENT_LOGG
--------------------------------------------------------

  ALTER TABLE "RESERVASJON_EVENT_LOGG" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON_EVENT_LOGG" MODIFY ("RESERVASJON_ID" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON_EVENT_LOGG" MODIFY ("OPPGAVE_ID" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON_EVENT_LOGG" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON_EVENT_LOGG" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON_EVENT_LOGG" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "RESERVASJON_EVENT_LOGG" ADD CONSTRAINT "PK_RESERVASJON_EVENT_LOGG" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table SAKSBEHANDLER
--------------------------------------------------------

  ALTER TABLE "SAKSBEHANDLER" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "SAKSBEHANDLER" MODIFY ("SAKSBEHANDLER_IDENT" NOT NULL ENABLE);
  ALTER TABLE "SAKSBEHANDLER" MODIFY ("VERSJON" NOT NULL ENABLE);
  ALTER TABLE "SAKSBEHANDLER" MODIFY ("OPPRETTET_AV" NOT NULL ENABLE);
  ALTER TABLE "SAKSBEHANDLER" MODIFY ("OPPRETTET_TID" NOT NULL ENABLE);
  ALTER TABLE "SAKSBEHANDLER" ADD CONSTRAINT "PK_SAKSBEHANDLER" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table AVDELING_SAKSBEHANDLER
--------------------------------------------------------

  ALTER TABLE "AVDELING_SAKSBEHANDLER" ADD CONSTRAINT "FK_AVDELING_SAKSBEHANDLER_1" FOREIGN KEY ("SAKSBEHANDLER_ID")
	  REFERENCES "SAKSBEHANDLER" ("ID") ENABLE;
  ALTER TABLE "AVDELING_SAKSBEHANDLER" ADD CONSTRAINT "FK_AVDELING_SAKSBEHANDLER_2" FOREIGN KEY ("AVDELING_ID")
	  REFERENCES "AVDELING" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EVENTMOTTAK_FEILLOGG
--------------------------------------------------------

  ALTER TABLE "EVENTMOTTAK_FEILLOGG" ADD CONSTRAINT "FK_EVENTMOTTAK_FEILLOGG_1" FOREIGN KEY ("STATUS", "KL_STATUS")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FILTRERING_ANDRE_KRITERIER
--------------------------------------------------------

  ALTER TABLE "FILTRERING_ANDRE_KRITERIER" ADD CONSTRAINT "FK_FILTRERING_ANDRE_KRIT_1" FOREIGN KEY ("ANDRE_KRITERIER_TYPE", "KL_ANDRE_KRITERIER_TYPE")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FILTRERING_BEHANDLING_TYPE
--------------------------------------------------------

  ALTER TABLE "FILTRERING_BEHANDLING_TYPE" ADD CONSTRAINT "FK_FILTR_BEHANDLING_TYPE_1" FOREIGN KEY ("BEHANDLING_TYPE", "KL_BEHANDLING_TYPE")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FILTRERING_SAKSBEHANDLER
--------------------------------------------------------

  ALTER TABLE "FILTRERING_SAKSBEHANDLER" ADD CONSTRAINT "FK_FILTRERING_SAKSBEHANDLER_1" FOREIGN KEY ("SAKSBEHANDLER_ID")
	  REFERENCES "SAKSBEHANDLER" ("ID") ENABLE;
  ALTER TABLE "FILTRERING_SAKSBEHANDLER" ADD CONSTRAINT "FK_FILTRERING_SAKSBEHANDLER_2" FOREIGN KEY ("OPPGAVE_FILTRERING_ID")
	  REFERENCES "OPPGAVE_FILTRERING" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FILTRERING_YTELSE_TYPE
--------------------------------------------------------

  ALTER TABLE "FILTRERING_YTELSE_TYPE" ADD CONSTRAINT "FK_FILTRERING_YTELSE_TYPE_1" FOREIGN KEY ("FAGSAK_YTELSE_TYPE", "KL_FAGSAK_YTELSE_TYPE")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table KODELISTE
--------------------------------------------------------

  ALTER TABLE "KODELISTE" ADD CONSTRAINT "FK_KODELISTE_01" FOREIGN KEY ("KODEVERK")
	  REFERENCES "KODEVERK" ("KODE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table KODELISTE_NAVN_I18N
--------------------------------------------------------

  ALTER TABLE "KODELISTE_NAVN_I18N" ADD CONSTRAINT "FK_KODELISTE_02" FOREIGN KEY ("KL_KODE", "KL_KODEVERK")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table KONFIG_VERDI
--------------------------------------------------------

  ALTER TABLE "KONFIG_VERDI" ADD CONSTRAINT "FK_KONFIG_VERDI_1" FOREIGN KEY ("KONFIG_KODE", "KONFIG_GRUPPE")
	  REFERENCES "KONFIG_VERDI_KODE" ("KODE", "KONFIG_GRUPPE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table KONFIG_VERDI_KODE
--------------------------------------------------------

  ALTER TABLE "KONFIG_VERDI_KODE" ADD CONSTRAINT "FK_KONFIG_VERDI_KODE_1" FOREIGN KEY ("KONFIG_TYPE")
	  REFERENCES "KONFIG_VERDI_TYPE" ("KODE") ENABLE;
  ALTER TABLE "KONFIG_VERDI_KODE" ADD CONSTRAINT "FK_KONFIG_VERDI_KODE_2" FOREIGN KEY ("KONFIG_GRUPPE")
	  REFERENCES "KONFIG_VERDI_GRUPPE" ("KODE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table OPPGAVE
--------------------------------------------------------

  ALTER TABLE "OPPGAVE" ADD CONSTRAINT "FK_OPPGAVE_1" FOREIGN KEY ("BEHANDLING_TYPE", "KL_BEHANDLING_TYPE")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
  ALTER TABLE "OPPGAVE" ADD CONSTRAINT "FK_OPPGAVE_2" FOREIGN KEY ("FAGSAK_YTELSE_TYPE", "KL_FAGSAK_YTELSE_TYPE")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table OPPGAVE_EGENSKAP
--------------------------------------------------------

  ALTER TABLE "OPPGAVE_EGENSKAP" ADD CONSTRAINT "FK_OPPGAVE_EGENSKAP_1" FOREIGN KEY ("ANDRE_KRITERIER_TYPE", "KL_ANDRE_KRITERIER_TYPE")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
  ALTER TABLE "OPPGAVE_EGENSKAP" ADD CONSTRAINT "FK_OPPGAVE_EGENSKAP_2" FOREIGN KEY ("OPPGAVE_ID")
	  REFERENCES "OPPGAVE" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table OPPGAVE_EVENT_LOGG
--------------------------------------------------------

  ALTER TABLE "OPPGAVE_EVENT_LOGG" ADD CONSTRAINT "FK_OPPGAVE_EVENT_LOGG_1" FOREIGN KEY ("EVENT_TYPE", "KL_EVENT_TYPE")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
  ALTER TABLE "OPPGAVE_EVENT_LOGG" ADD CONSTRAINT "FK_OPPGAVE_EVENT_LOGG_2" FOREIGN KEY ("ANDRE_KRITERIER_TYPE", "KL_ANDRE_KRITERIER_TYPE")
	  REFERENCES "KODELISTE" ("KODE", "KODEVERK") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table OPPGAVE_FILTRERING
--------------------------------------------------------

  ALTER TABLE "OPPGAVE_FILTRERING" ADD CONSTRAINT "FK_LISTE_1" FOREIGN KEY ("AVDELING_ID")
	  REFERENCES "AVDELING" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RESERVASJON
--------------------------------------------------------

  ALTER TABLE "RESERVASJON" ADD CONSTRAINT "FK_RESERVASJON_1" FOREIGN KEY ("OPPGAVE_ID")
	  REFERENCES "OPPGAVE" ("ID") ENABLE;


/* Justeringer for baseline før migrering til Postgress.
-- 1.

declare
   inst_rank number;
begin
    select max("installed_rank")+1 into inst_rank from "schema_version";
    Insert into "schema_version" ("installed_rank","version","description","type","script","checksum","installed_by","execution_time","success") values (inst_rank,'2','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',null,'FPLOS',0,'1');
end;
/

 -- 2.
create table "schema_version_history"  as SELECT * FROM "schema_version" where "version" < to_char('2');

-- 3.
delete from  "schema_version" where "version" < to_char('2');
commit;
*/
