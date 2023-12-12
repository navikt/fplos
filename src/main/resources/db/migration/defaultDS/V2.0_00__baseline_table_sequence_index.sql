create sequence SEQ_AVDELING
    increment by 50
    nocache;

create sequence SEQ_EVENTMOTTAK_FEILLOGG
    increment by 50
    nocache;

create sequence SEQ_FILTR_BEHANDLING_TYPE
    increment by 50
    nocache;

create sequence SEQ_FILTRERING_ANDRE_KRIT
    increment by 50
    nocache;

create sequence SEQ_FILTRERING_YTELSE_TYPE
    increment by 50
    nocache;

create sequence SEQ_KODELISTE
    increment by 50
    nocache;

create sequence SEQ_KODELISTE_NAVN_I18N
    increment by 50
    nocache;

create sequence SEQ_OPPGAVE
    increment by 50
    order
    nocache;

create sequence SEQ_OPPGAVE_EGENSKAP
    increment by 50
    order
    nocache;

create sequence SEQ_OPPGAVE_EVENT_LOGG
    increment by 50
    order
    nocache;

create sequence SEQ_OPPGAVE_FILTRERING
    increment by 200
    nocache;

create sequence SEQ_RESERVASJON
    increment by 50
    nocache;

create sequence SEQ_RESERVASJON_EVENT_LOGG
    increment by 50
    nocache;

create sequence SEQ_SAKSBEHANDLER
    increment by 50
    nocache;

create sequence SEQ_TILBAKEKREVING_EGENSKAPER
    increment by 50
    order
    nocache;

create sequence SEQ_PROSESS_TASK
    minvalue 1000000
    increment by 50
    nocache;

create sequence SEQ_PROSESS_TASK_GRUPPE
    minvalue 10000000
    increment by 50
    nocache;

create sequence SEQ_HENDELSE
    minvalue 10000
    increment by 50
    order
    nocache;

create sequence SEQ_HENDELSE_TK_AKSJONSPUNKT
    minvalue 10000
    increment by 50
    order
    nocache;

create sequence SEQ_DRIFTSMELDING
    minvalue 1000
    increment by 50
    nocache;

create sequence SEQ_STATISTIKK_KO
    minvalue 1000
    increment by 50
    nocache;

create table AVDELING
(
    ID             NUMBER(19)                             not null
        constraint PK_AVDELING
        primary key,
    AVDELING_ENHET VARCHAR2(10 char)                      not null,
    NAVN           VARCHAR2(255 char)                     not null,
    VERSJON        NUMBER(19)        default 0            not null,
    OPPRETTET_AV   VARCHAR2(20 char) default 'VL'         not null,
    OPPRETTET_TID  TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV      VARCHAR2(20 char),
    ENDRET_TID     TIMESTAMP(3),
    KREVER_KODE_6  VARCHAR2(1 char)  default 'N'
);

comment on table AVDELING is 'Tabell som inneholder avdelingene som kan utføre oppgaver';

comment on column AVDELING.ID is 'PK';

comment on column AVDELING.AVDELING_ENHET is 'Identifikasjon på enhet som har oppgaver.';

comment on column AVDELING.KREVER_KODE_6 is 'Kreves kode 6 for å kunne velge denne avdelingen';

create index IDX_AVDELING_1
    on AVDELING (AVDELING_ENHET);

create table EVENTMOTTAK_FEILLOGG
(
    ID                        NUMBER(19)                             not null
        constraint PK_EVENTMOTTAK_FEILLOGG
        primary key,
    MELDING                   CLOB                                   not null,
    STATUS                    VARCHAR2(100 char)                     not null,
    FEILMELDING_SISTE_KJORING CLOB,
    VERSJON                   NUMBER(19)        default 0            not null,
    OPPRETTET_AV              VARCHAR2(20 char) default 'VLLOS'      not null,
    OPPRETTET_TID             TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV                 VARCHAR2(20 char),
    ENDRET_TID                TIMESTAMP(3)
);

comment on table EVENTMOTTAK_FEILLOGG is 'Feillogg for eventer som kommer fra fpsak';

comment on column EVENTMOTTAK_FEILLOGG.ID is 'PK';

comment on column EVENTMOTTAK_FEILLOGG.MELDING is 'Meldingen som kom fra eventkøen';

comment on column EVENTMOTTAK_FEILLOGG.STATUS is 'Status for meldingen';

comment on column EVENTMOTTAK_FEILLOGG.FEILMELDING_SISTE_KJORING is 'Feilmelding for siste kjøring';

create index IDX_EVENTMOTTAK_FEILLOGG_1
    on EVENTMOTTAK_FEILLOGG (STATUS);

create table FILTRERING_ANDRE_KRITERIER
(
    ID                    NUMBER(19)                             not null
        constraint PK_FILTRERING_ANDRE_KRITERIER
        primary key,
    OPPGAVE_FILTRERING_ID NUMBER(19)                             not null,
    ANDRE_KRITERIER_TYPE  VARCHAR2(100 char)                     not null,
    VERSJON               NUMBER(19)        default 0            not null,
    OPPRETTET_AV          VARCHAR2(20 char) default 'VL'         not null,
    OPPRETTET_TID         TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV             VARCHAR2(20 char),
    ENDRET_TID            TIMESTAMP(3),
    INKLUDER              VARCHAR2(1 char)  default 'J'          not null
);

comment on table FILTRERING_ANDRE_KRITERIER is 'Tabell inneholder filtreringer for andre kriterier';

comment on column FILTRERING_ANDRE_KRITERIER.ID is 'PK';

comment on column FILTRERING_ANDRE_KRITERIER.OPPGAVE_FILTRERING_ID is 'Oppgavefiltrering som filtreringen hører til';

comment on column FILTRERING_ANDRE_KRITERIER.ANDRE_KRITERIER_TYPE is 'Kode for de andre kriteriene oppgavene skal filtreres på';

comment on column FILTRERING_ANDRE_KRITERIER.INKLUDER is 'Verdi som sier om det skal filtreres inn eller filtreres vekk.';

create index IDX_FILTRERING_ANDRE_KRIT_1
    on FILTRERING_ANDRE_KRITERIER (OPPGAVE_FILTRERING_ID, ANDRE_KRITERIER_TYPE);

create index IDX_FILTRERING_ANDRE_KRIT_2
    on FILTRERING_ANDRE_KRITERIER (ANDRE_KRITERIER_TYPE);

create table FILTRERING_BEHANDLING_TYPE
(
    ID                    NUMBER(19)                                   not null
        constraint PK_FILTR_BEHANDLING_TYPE
        primary key,
    OPPGAVE_FILTRERING_ID NUMBER(19)                                   not null,
    BEHANDLING_TYPE       VARCHAR2(100 char)                           not null,
    KL_BEHANDLING_TYPE    VARCHAR2(100 char) default 'BEHANDLING_TYPE' not null,
    VERSJON               NUMBER(19)         default 0                 not null,
    OPPRETTET_AV          VARCHAR2(20 char)  default 'VL'              not null,
    OPPRETTET_TID         TIMESTAMP(3)       default systimestamp      not null,
    ENDRET_AV             VARCHAR2(20 char),
    ENDRET_TID            TIMESTAMP(3)
);

comment on table FILTRERING_BEHANDLING_TYPE is 'Tabell inneholder filtreringer på behandlingType';

comment on column FILTRERING_BEHANDLING_TYPE.ID is 'PK';

comment on column FILTRERING_BEHANDLING_TYPE.OPPGAVE_FILTRERING_ID is 'Listen filtreringen hører til';

comment on column FILTRERING_BEHANDLING_TYPE.BEHANDLING_TYPE is 'Koden behandlingstypen skal filtreres på';

comment on column FILTRERING_BEHANDLING_TYPE.KL_BEHANDLING_TYPE is 'Kodeverk behandlingstype';

create index IDX_FILTR_BEHANDLING_TYPE_1
    on FILTRERING_BEHANDLING_TYPE (OPPGAVE_FILTRERING_ID, BEHANDLING_TYPE);

create index IDX_FILTR_BEHANDLING_TYPE_2
    on FILTRERING_BEHANDLING_TYPE (BEHANDLING_TYPE);

create table FILTRERING_YTELSE_TYPE
(
    ID                    NUMBER(19)                             not null
        constraint PK_FILTRERING_YTELSE_TYPE
        primary key,
    OPPGAVE_FILTRERING_ID NUMBER(19)                             not null,
    FAGSAK_YTELSE_TYPE    VARCHAR2(100 char)                     not null,
    VERSJON               NUMBER(19)        default 0            not null,
    OPPRETTET_AV          VARCHAR2(20 char) default 'VL'         not null,
    OPPRETTET_TID         TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV             VARCHAR2(20 char),
    ENDRET_TID            TIMESTAMP(3)
);

comment on table FILTRERING_YTELSE_TYPE is 'Tabell inneholder filtreringer på behandlingType';

comment on column FILTRERING_YTELSE_TYPE.ID is 'PK';

comment on column FILTRERING_YTELSE_TYPE.OPPGAVE_FILTRERING_ID is 'Oppgavefiltrering som filtreringen hører til';

comment on column FILTRERING_YTELSE_TYPE.FAGSAK_YTELSE_TYPE is 'Koden ytelsetypen skal filtreres på';

create index IDX_FILTR_YTELSE_TYPE_1
    on FILTRERING_YTELSE_TYPE (OPPGAVE_FILTRERING_ID, FAGSAK_YTELSE_TYPE);

create index IDX_FILTR_YTELSE_TYPE_2
    on FILTRERING_YTELSE_TYPE (FAGSAK_YTELSE_TYPE);

create table OPPGAVE
(
    ID                   NUMBER(19)                             not null
        constraint PK_OPPGAVE
        primary key,
    FAGSAK_SAKSNR        NUMBER(19)                             not null,
    AKTOR_ID             NUMBER(19),
    BEHANDLENDE_ENHET    VARCHAR2(10 char)                      not null,
    AKTIV                VARCHAR2(1 char)  default 'Y'          not null,
    BEHANDLING_TYPE      VARCHAR2(100 char)                     not null,
    FAGSAK_YTELSE_TYPE   VARCHAR2(100 char)                     not null,
    SYSTEM               VARCHAR2(100 char),
    BEHANDLINGSFRIST     TIMESTAMP(3),
    BEHANDLING_OPPRETTET TIMESTAMP(3),
    VERSJON              NUMBER(19)        default 0            not null,
    OPPRETTET_AV         VARCHAR2(20 char) default 'VL'         not null,
    OPPRETTET_TID        TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV            VARCHAR2(20 char),
    ENDRET_TID           TIMESTAMP(3),
    FORSTE_STONADSDAG    DATE,
    BEHANDLING_STATUS    VARCHAR2(100 char),
    UTFORT_FRA_ADMIN     VARCHAR2(1 char)  default 'N'          not null,
    OPPGAVE_AVSLUTTET    TIMESTAMP(3),
    HREF                 VARCHAR2(500 char),
    BEHANDLING_ID        RAW(16)                                not null,
    ENDRET_TID_2         TIMESTAMP(3)
);

comment on table OPPGAVE is 'Tabell som skal inneholde informasjon om behandlinger som trenger intervensjon av en saksbehandler';

comment on column OPPGAVE.ID is 'PK';

comment on column OPPGAVE.FAGSAK_SAKSNR is 'Referansenr for fagsak';

comment on column OPPGAVE.AKTOR_ID is 'Aktør id';

comment on column OPPGAVE.BEHANDLENDE_ENHET is 'Enheten som har oppgaven satt til seg';

comment on column OPPGAVE.AKTIV is 'Setter oppgaveegenskap til aktiv eller inaktiv etter egenskapen sin tilstand';

comment on column OPPGAVE.BEHANDLING_TYPE is 'Hva slags behandlingstype behandlingen har';

comment on column OPPGAVE.FAGSAK_YTELSE_TYPE is 'Hva slags ytelse type fagsaken har';

comment on column OPPGAVE.SYSTEM is 'Hvilket system oppgaven kommer fra';

comment on column OPPGAVE.BEHANDLINGSFRIST is 'Behandlingsfrist';

comment on column OPPGAVE.BEHANDLING_OPPRETTET is 'Tidspunktet behandlingen ble opprettet';

comment on column OPPGAVE.FORSTE_STONADSDAG is 'Første stønadsdag';

comment on column OPPGAVE.BEHANDLING_STATUS is 'Status for behandlingen';

comment on column OPPGAVE.UTFORT_FRA_ADMIN is 'Settes til true når Oppgave er endret fra admin-tjeneste';

comment on column OPPGAVE.OPPGAVE_AVSLUTTET is 'Tidspunkt for avslutting av Oppgave';

comment on column OPPGAVE.HREF is 'URL som benyttes til eksternt system';

comment on column OPPGAVE.BEHANDLING_ID is 'Behandling id for behandling i fagsystem.';

comment on column OPPGAVE.ENDRET_TID_2 is 'Temp kolonne for opprydding etter kafkaproblemer 5.10.2020';

create index IDX_OPPGAVE_1
    on OPPGAVE (BEHANDLING_TYPE);

create index IDX_OPPGAVE_2
    on OPPGAVE (FAGSAK_YTELSE_TYPE);

create index IDX_OPPGAVE_3
    on OPPGAVE (AKTIV, BEHANDLENDE_ENHET);

create index IDX_OPPGAVE_5
    on OPPGAVE (BEHANDLINGSFRIST);

create index IDX_OPPGAVE_6
    on OPPGAVE (BEHANDLING_OPPRETTET);

create index IDX_OPPGAVE_7
    on OPPGAVE (FORSTE_STONADSDAG);

create index IDX_OPPGAVE_8
    on OPPGAVE (BEHANDLING_ID);

create table OPPGAVE_EGENSKAP
(
    ID                           NUMBER(19)                             not null
        constraint PK_OPPGAVE_EGENSKAP
        primary key,
    OPPGAVE_ID                   NUMBER(19)                             not null
        constraint FK_OPPGAVE_EGENSKAP_2
        references OPPGAVE,
    ANDRE_KRITERIER_TYPE         VARCHAR2(100 char)                     not null,
    VERSJON                      NUMBER(19)        default 0            not null,
    OPPRETTET_AV                 VARCHAR2(20 char) default 'VL'         not null,
    OPPRETTET_TID                TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV                    VARCHAR2(20 char),
    ENDRET_TID                   TIMESTAMP(3),
    SISTE_SAKSBEHANDLER_FOR_TOTR VARCHAR2(20 char),
    AKTIV                        VARCHAR2(1 char)  default 'J'          not null
);

comment on table OPPGAVE_EGENSKAP is 'Tabell inneholder filtreringer for andre kriterier';

comment on column OPPGAVE_EGENSKAP.ID is 'PK';

comment on column OPPGAVE_EGENSKAP.OPPGAVE_ID is 'Oppgave egenskapen hører til';

comment on column OPPGAVE_EGENSKAP.ANDRE_KRITERIER_TYPE is 'Kode for de andre kriteriene oppgavene skal filtreres på';

comment on column OPPGAVE_EGENSKAP.SISTE_SAKSBEHANDLER_FOR_TOTR is 'Saksbehandleren som sendte behandlingen til totrinnskontroll';

create index IDX_OPPGAVE_EGENSKAP_1
    on OPPGAVE_EGENSKAP (OPPGAVE_ID);

create index IDX_OPPGAVE_EGENSKAP_2
    on OPPGAVE_EGENSKAP (ANDRE_KRITERIER_TYPE);

create index IDX_OPPGAVE_EGENSKAP_3
    on OPPGAVE_EGENSKAP (ANDRE_KRITERIER_TYPE, UPPER("SISTE_SAKSBEHANDLER_FOR_TOTR"));

create table OPPGAVE_EVENT_LOGG
(
    ID                   NUMBER(19)                             not null
        constraint PK_OPPGAVE_EVENT_TYPE
        primary key,
    EVENT_TYPE           VARCHAR2(100 char)                     not null,
    ANDRE_KRITERIER_TYPE VARCHAR2(100 char),
    VERSJON              NUMBER(19)        default 0            not null,
    OPPRETTET_AV         VARCHAR2(20 char) default 'VLLOS'      not null,
    OPPRETTET_TID        TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV            VARCHAR2(20 char),
    ENDRET_TID           TIMESTAMP(3),
    BEHANDLENDE_ENHET    VARCHAR2(10 char),
    FRIST_TID            TIMESTAMP(3),
    BEHANDLING_ID        RAW(16)                                not null
);

comment on table OPPGAVE_EVENT_LOGG is 'Eventlogg for alt som gjøres med oppgaver';

comment on column OPPGAVE_EVENT_LOGG.ID is 'PK';

comment on column OPPGAVE_EVENT_LOGG.EVENT_TYPE is 'Typen event';

comment on column OPPGAVE_EVENT_LOGG.ANDRE_KRITERIER_TYPE is 'Spesialkriterier som påvirker åpning og lukking av oppgaver';

comment on column OPPGAVE_EVENT_LOGG.BEHANDLENDE_ENHET is 'Behandlende enhet for oppgaven';

comment on column OPPGAVE_EVENT_LOGG.FRIST_TID is 'Behandling blir automatisk gjenopptatt etter dette tidspunktet';

comment on column OPPGAVE_EVENT_LOGG.BEHANDLING_ID is 'Behandling id for behandling i fagsystem.';

create index IDX_OPPGAVE_EVENT_LOGG_2
    on OPPGAVE_EVENT_LOGG (EVENT_TYPE);

create index IDX_OPPGAVE_EVENT_LOGG_3
    on OPPGAVE_EVENT_LOGG (ANDRE_KRITERIER_TYPE);

create table OPPGAVE_FILTRERING
(
    ID                  NUMBER(19)                             not null
        constraint PK_OPPGAVE_FILTRERING
        primary key,
    NAVN                VARCHAR2(100 char)                     not null,
    SORTERING           VARCHAR2(100 char)                     not null,
    AVDELING_ID         NUMBER(19)                             not null
        constraint FK_LISTE_1
        references AVDELING,
    VERSJON             NUMBER(19)        default 0            not null,
    OPPRETTET_AV        VARCHAR2(20 char) default 'VL'         not null,
    OPPRETTET_TID       TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV           VARCHAR2(20 char),
    ENDRET_TID          TIMESTAMP(3),
    ER_DYNAMISK_PERIODE VARCHAR2(1 char)  default 'N'          not null,
    FOM_DATO            DATE,
    TOM_DATO            DATE,
    FOM_DAGER           NUMBER(19),
    TOM_DAGER           NUMBER(19)
);

comment on table OPPGAVE_FILTRERING is 'Tabell som skal inneholde informasjon om lister som brukes til filtrering av oppgaver';

comment on column OPPGAVE_FILTRERING.ID is 'PK';

comment on column OPPGAVE_FILTRERING.NAVN is 'Navn på listen';

comment on column OPPGAVE_FILTRERING.SORTERING is 'Hva skal listen sorteres på';

comment on column OPPGAVE_FILTRERING.AVDELING_ID is 'Avdelingsenhet oppgavefiltreringa er koblet til';

comment on column OPPGAVE_FILTRERING.ER_DYNAMISK_PERIODE is 'Angir om intervallet for perioden er dynamisk satt';

comment on column OPPGAVE_FILTRERING.FOM_DATO is 'Datointervall fra og med dato';

comment on column OPPGAVE_FILTRERING.TOM_DATO is 'Datointervall til og med dato';

comment on column OPPGAVE_FILTRERING.FOM_DAGER is 'Datointervall fra og med dag';

comment on column OPPGAVE_FILTRERING.TOM_DAGER is 'Datointervall til og med dag';

create index IDX_OPPGAVE_FILTRERING_1
    on OPPGAVE_FILTRERING (AVDELING_ID);

create table RESERVASJON
(
    ID                NUMBER(19)                             not null
        constraint PK_RESERVASJON
        primary key,
    OPPGAVE_ID        NUMBER(19)                             not null
        constraint FK_RESERVASJON_1
        references OPPGAVE,
    RESERVERT_TIL     TIMESTAMP(3),
    RESERVERT_AV      VARCHAR2(20 char) default 'VL'         not null,
    VERSJON           NUMBER(19)        default 0            not null,
    OPPRETTET_AV      VARCHAR2(20 char) default 'VL'         not null,
    OPPRETTET_TID     TIMESTAMP(6)      default systimestamp not null,
    ENDRET_AV         VARCHAR2(20 char),
    ENDRET_TID        TIMESTAMP(6),
    FLYTTET_AV        VARCHAR2(100 char),
    FLYTTET_TIDSPUNKT TIMESTAMP(3),
    BEGRUNNELSE       VARCHAR2(500 char)
);

comment on table RESERVASJON is 'Tabell som skal inneholde reservasjoner av oppgaver';

comment on column RESERVASJON.ID is 'PK';

comment on column RESERVASJON.RESERVERT_TIL is 'Tidspunkt for når reservasjonen går ut';

comment on column RESERVASJON.RESERVERT_AV is 'Saksbehandler ident til saksbehandler som har reservert oppgaven';

comment on column RESERVASJON.FLYTTET_AV is 'Ident som har flyttet reservasjonen';

comment on column RESERVASJON.FLYTTET_TIDSPUNKT is 'Tidspunkt for flytting av reservasjonen';

comment on column RESERVASJON.BEGRUNNELSE is 'Begrunnelse for opphør/flytting av reservasjon';

create index IDX_RESERVASJON_1
    on RESERVASJON (OPPGAVE_ID);

alter table RESERVASJON
    add constraint UNIQUE_OPPGAVE
        unique (OPPGAVE_ID);

create table RESERVASJON_EVENT_LOGG
(
    ID                NUMBER(19)                             not null
        constraint PK_RESERVASJON_EVENT_LOGG
        primary key,
    RESERVASJON_ID    NUMBER(19)                             not null,
    OPPGAVE_ID        NUMBER(19)                             not null,
    RESERVERT_TIL     TIMESTAMP(3),
    RESERVERT_AV      VARCHAR2(20 char),
    FLYTTET_AV        VARCHAR2(100 char),
    FLYTTET_TIDSPUNKT TIMESTAMP(3),
    BEGRUNNELSE       VARCHAR2(500 char),
    VERSJON           NUMBER(19)        default 0            not null,
    OPPRETTET_AV      VARCHAR2(20 char) default 'VLLOS'      not null,
    OPPRETTET_TID     TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV         VARCHAR2(20 char),
    ENDRET_TID        TIMESTAMP(3)
);

comment on table RESERVASJON_EVENT_LOGG is 'Eventlogg for alt som gjøres med reservasjoner';

comment on column RESERVASJON_EVENT_LOGG.ID is 'PK';

comment on column RESERVASJON_EVENT_LOGG.RESERVASJON_ID is 'Id for reservasjonen';

comment on column RESERVASJON_EVENT_LOGG.OPPGAVE_ID is 'Oppgaven det er koblet til';

comment on column RESERVASJON_EVENT_LOGG.RESERVERT_TIL is 'Tidspunkt for hvor lenge reservasjonen er gyldig';

comment on column RESERVASJON_EVENT_LOGG.RESERVERT_AV is 'Ident for hvem oppgaven er reservert på';

comment on column RESERVASJON_EVENT_LOGG.FLYTTET_AV is 'Ident for hvem som evt. har flyttet reservasjonen';

comment on column RESERVASJON_EVENT_LOGG.FLYTTET_TIDSPUNKT is 'Tidspunkt reservasjonen evt. er flyttet';

comment on column RESERVASJON_EVENT_LOGG.BEGRUNNELSE is 'Begrunnelse for flytting/frigjøring av reservasjon';

create index IDX_RESERVASJON_EVENT_LOGG_1
    on RESERVASJON_EVENT_LOGG (RESERVASJON_ID);

create index IDX_RESERVASJON_EVENT_LOGG_2
    on RESERVASJON_EVENT_LOGG (OPPGAVE_ID);

create table SAKSBEHANDLER
(
    ID                  NUMBER(19)                             not null
        constraint PK_SAKSBEHANDLER
        primary key,
    SAKSBEHANDLER_IDENT VARCHAR2(20 char)                      not null,
    VERSJON             NUMBER(19)        default 0            not null,
    OPPRETTET_AV        VARCHAR2(20 char) default 'VL'         not null,
    OPPRETTET_TID       TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV           VARCHAR2(20 char),
    ENDRET_TID          TIMESTAMP(3)
);

comment on table SAKSBEHANDLER is 'Tabell som inneholder saksbehandleren som kan løse oppgaver';

comment on column SAKSBEHANDLER.ID is 'PK';

comment on column SAKSBEHANDLER.SAKSBEHANDLER_IDENT is 'Saksbehandlerident';

create table AVDELING_SAKSBEHANDLER
(
    SAKSBEHANDLER_ID NUMBER(19) not null
        constraint FK_AVDELING_SAKSBEHANDLER_1
        references SAKSBEHANDLER,
    AVDELING_ID      NUMBER(19) not null
        constraint FK_AVDELING_SAKSBEHANDLER_2
        references AVDELING,
    constraint PK_AVDELING_SAKSBEHANDLER
        primary key (SAKSBEHANDLER_ID, AVDELING_ID)
);

comment on table AVDELING_SAKSBEHANDLER is 'Tabell som er koblingen mellom avdeling og saksbehandler';

comment on column AVDELING_SAKSBEHANDLER.SAKSBEHANDLER_ID is 'FK til saksbehandler';

comment on column AVDELING_SAKSBEHANDLER.AVDELING_ID is 'FK til avdeling';

create index IDX_AVDELING_SAKSBEHANDLER_1
    on AVDELING_SAKSBEHANDLER (SAKSBEHANDLER_ID);

create index IDX_AVDELING_SAKSBEHANDLER_2
    on AVDELING_SAKSBEHANDLER (AVDELING_ID);

create table FILTRERING_SAKSBEHANDLER
(
    SAKSBEHANDLER_ID      NUMBER(19) not null
        constraint FK_FILTRERING_SAKSBEHANDLER_1
        references SAKSBEHANDLER,
    OPPGAVE_FILTRERING_ID NUMBER(19) not null
        constraint FK_FILTRERING_SAKSBEHANDLER_2
        references OPPGAVE_FILTRERING,
    constraint PK_FILTRERING_SAKSBEHANDLER
        primary key (SAKSBEHANDLER_ID, OPPGAVE_FILTRERING_ID)
);

comment on table FILTRERING_SAKSBEHANDLER is 'Tabell som er koblingen mellom avdeling og saksbehandler';

comment on column FILTRERING_SAKSBEHANDLER.SAKSBEHANDLER_ID is 'FK til saksbehandler';

comment on column FILTRERING_SAKSBEHANDLER.OPPGAVE_FILTRERING_ID is 'FK til oppgave filtrering';

create index IDX_FILTRERING_SAKSBEHANDLER_1
    on FILTRERING_SAKSBEHANDLER (SAKSBEHANDLER_ID);

create index IDX_FILTRERING_SAKSBEHANDLER_2
    on FILTRERING_SAKSBEHANDLER (OPPGAVE_FILTRERING_ID);

create index IDX_SAKSBEHANDLER_1
    on SAKSBEHANDLER (SAKSBEHANDLER_IDENT);

create table TILBAKEKREVING_EGENSKAPER
(
    OPPGAVE_ID          NUMBER(19) not null
        constraint PK_TILBAKEKREVING_EGENSKAPER
        primary key
        constraint FK_TILBAKEKREVING_EGENSKAPER
        references OPPGAVE,
    BELOP               NUMBER(19, 2),
    FEILUTBETALINGSTART TIMESTAMP(3)
);

comment on table TILBAKEKREVING_EGENSKAPER is 'Tabell inneholder egenskaper for tilbakekrevingoppgaver';

comment on column TILBAKEKREVING_EGENSKAPER.OPPGAVE_ID is 'Oppgave egenskapene hører til';

comment on column TILBAKEKREVING_EGENSKAPER.BELOP is 'Beløpet som henvendelsen gjelder';

comment on column TILBAKEKREVING_EGENSKAPER.FEILUTBETALINGSTART is 'Tidspunkt når kravet ikke lenger er gyldig';

create table PROSESS_TASK
(
    ID                        NUMBER(19)                              not null
        constraint PK_PROSESS_TASK
        primary key,
    TASK_TYPE                 VARCHAR2(50 char)                       not null,
    PRIORITET                 NUMBER(3)          default 0            not null,
    STATUS                    VARCHAR2(20 char)  default 'KLAR'       not null
        constraint CHK_PROSESS_TASK_STATUS
        check (status in ('KLAR', 'FEILET', 'VENTER_SVAR', 'SUSPENDERT', 'VETO', 'FERDIG', 'KJOERT')),
    TASK_PARAMETERE           VARCHAR2(4000 char),
    TASK_PAYLOAD              CLOB,
    TASK_GRUPPE               VARCHAR2(250 char),
    TASK_SEKVENS              VARCHAR2(100 char) default '1'          not null,
    NESTE_KJOERING_ETTER      TIMESTAMP(0)       default current_timestamp,
    FEILEDE_FORSOEK           NUMBER(5)          default 0,
    SISTE_KJOERING_TS         TIMESTAMP(6),
    SISTE_KJOERING_FEIL_KODE  VARCHAR2(50 char),
    SISTE_KJOERING_FEIL_TEKST CLOB,
    SISTE_KJOERING_SERVER     VARCHAR2(50 char),
    VERSJON                   NUMBER(19)         default 0            not null,
    OPPRETTET_AV              VARCHAR2(20 char)  default 'VL'         not null,
    OPPRETTET_TID             TIMESTAMP(6)       default systimestamp not null,
    BLOKKERT_AV               NUMBER(19),
    SISTE_KJOERING_SLUTT_TS   TIMESTAMP(6),
    SISTE_KJOERING_PLUKK_TS   TIMESTAMP(6)
);

comment on table PROSESS_TASK is 'Inneholder tasks som skal kjøres i bakgrunnen';

comment on column PROSESS_TASK.ID is 'Primary Key';

comment on column PROSESS_TASK.TASK_TYPE is 'navn på task. Brukes til å matche riktig implementasjon';

comment on column PROSESS_TASK.PRIORITET is 'prioritet på task.  Høyere tall har høyere prioritet';

comment on column PROSESS_TASK.STATUS is 'status på task: KLAR, NYTT_FORSOEK, FEILET, VENTER_SVAR, FERDIG';

comment on column PROSESS_TASK.TASK_PARAMETERE is 'parametere angitt for en task';

comment on column PROSESS_TASK.TASK_PAYLOAD is 'inputdata for en task';

comment on column PROSESS_TASK.TASK_GRUPPE is 'angir en unik id som grupperer flere ';

comment on column PROSESS_TASK.TASK_SEKVENS is 'angir rekkefølge på task innenfor en gruppe ';

comment on column PROSESS_TASK.NESTE_KJOERING_ETTER is 'tasken skal ikke kjøeres før tidspunkt er passert';

comment on column PROSESS_TASK.FEILEDE_FORSOEK is 'antall feilede forsøk';

comment on column PROSESS_TASK.SISTE_KJOERING_TS is 'siste gang tasken ble forsøkt kjørt (før kjøring)';

comment on column PROSESS_TASK.SISTE_KJOERING_FEIL_KODE is 'siste feilkode tasken fikk';

comment on column PROSESS_TASK.SISTE_KJOERING_FEIL_TEKST is 'siste feil tasken fikk';

comment on column PROSESS_TASK.SISTE_KJOERING_SERVER is 'navn på node som sist kjørte en task (server@pid)';

comment on column PROSESS_TASK.VERSJON is 'angir versjon for optimistisk låsing';

comment on column PROSESS_TASK.BLOKKERT_AV is 'Id til ProsessTask som blokkerer kjøring av denne (når status=VETO)';

comment on column PROSESS_TASK.SISTE_KJOERING_SLUTT_TS is 'tidsstempel siste gang tasken ble kjørt (etter kjøring)';

comment on column PROSESS_TASK.SISTE_KJOERING_PLUKK_TS is 'siste gang tasken ble forsøkt plukket (fra db til in-memory, før kjøring)';

create table HENDELSE
(
    ID                             NUMBER(19)         not null
        constraint PK_HENDELSE
        primary key,
    FAGSYSTEM                      VARCHAR2(100 char) not null,
    BEHANDLING_ID                  RAW(16)            not null,
    BEHANDLENDE_ENHET              VARCHAR2(100 char) not null,
    SAKSNUMMER                     VARCHAR2(100 char) not null,
    AKTØR_ID                       VARCHAR2(100 char) not null,
    BEHANDLING_OPPRETTET_TIDSPUNKT TIMESTAMP(3)       not null,
    BEHANDLING_TYPE                VARCHAR2(10 char)  not null,
    YTELSE_TYPE                    VARCHAR2(10 char)  not null,
    VERSJON                        NUMBER(19)        default 0,
    OPPRETTET_AV                   VARCHAR2(20 char) default 'VL',
    OPPRETTET_TID                  TIMESTAMP(3)      default systimestamp,
    ENDRET_AV                      VARCHAR2(20 char),
    ENDRET_TID                     TIMESTAMP(3)
);

comment on table HENDELSE is 'Mellomlagring av hendelse fra kafka, tas og ryddes av prosesstasks';

comment on column HENDELSE.ID is 'Primary Key';

comment on column HENDELSE.FAGSYSTEM is 'Hvilket fagsystem hendelsen kommer fra';

comment on column HENDELSE.BEHANDLING_ID is 'Behandling id til behandlingen i fagsystemet';

comment on column HENDELSE.BEHANDLENDE_ENHET is 'Enhet som behandler';

comment on column HENDELSE.SAKSNUMMER is 'Saksnummer i fagsystemet';

comment on column HENDELSE.AKTØR_ID is 'Aktør id som omhandler hendelsen';

comment on column HENDELSE.BEHANDLING_OPPRETTET_TIDSPUNKT is 'Tidspunkt da behandlingen ble opprettet';

comment on column HENDELSE.BEHANDLING_TYPE is 'Type behandling';

comment on column HENDELSE.YTELSE_TYPE is 'Type ytelse';

create table HENDELSE_TILBAKEKREVING
(
    HENDELSE_ID                NUMBER(19) not null
        constraint PK_HENDELSE_TILBAKEKREVING
        primary key
        constraint FK_HENDELSE_TILBAKEKREVING
        references HENDELSE,
    FEILUTBETALT_BELØP         NUMBER(19, 2),
    HREF                       VARCHAR2(500 char),
    FØRSTE_FEILUTBETALING_DATO TIMESTAMP(3),
    ANSVARLIG_SAKSBEHANDLER    VARCHAR2(100 char),
    VERSJON                    NUMBER(19)        default 0,
    OPPRETTET_AV               VARCHAR2(20 char) default 'VL',
    OPPRETTET_TID              TIMESTAMP(3)      default systimestamp,
    ENDRET_AV                  VARCHAR2(20 char),
    ENDRET_TID                 TIMESTAMP(3)
);

comment on table HENDELSE_TILBAKEKREVING is 'Mellomlagring av tilbakekrevinghendelse fra kafka, tas og ryddes av prosesstasks';

comment on column HENDELSE_TILBAKEKREVING.HENDELSE_ID is 'Foreign key til HENDELSE';

comment on column HENDELSE_TILBAKEKREVING.FEILUTBETALT_BELØP is 'Beløp som er feilutbetalt';

comment on column HENDELSE_TILBAKEKREVING.HREF is 'Referanse til behandling';

comment on column HENDELSE_TILBAKEKREVING.FØRSTE_FEILUTBETALING_DATO is 'Dato fra første feilutbetaling';

comment on column HENDELSE_TILBAKEKREVING.ANSVARLIG_SAKSBEHANDLER is 'Ansvarlig saksbehandler';

create table HENDELSE_TK_AKSJONSPUNKT
(
    ID                         NUMBER(19)        not null
        constraint PK_HENDELSE_TK_AKSJONSPUNKT
        primary key,
    TILBAKEKREVING_HENDELSE_ID NUMBER(19)        not null
        constraint FK_TILBAKEKREVING_HENDELSE_ID
        references HENDELSE_TILBAKEKREVING,
    KODE                       VARCHAR2(20 char) not null,
    STATUS                     VARCHAR2(20 char) not null,
    VERSJON                    NUMBER(19)        default 0,
    OPPRETTET_AV               VARCHAR2(20 char) default 'VL',
    OPPRETTET_TID              TIMESTAMP(3)      default systimestamp,
    ENDRET_AV                  VARCHAR2(20 char),
    ENDRET_TID                 TIMESTAMP(3)
);

comment on table HENDELSE_TK_AKSJONSPUNKT is 'Mellomlagring av aksjonspunkter i tilbakekrevinghendelse fra kafka, tas og ryddes av prosesstasks';

comment on column HENDELSE_TK_AKSJONSPUNKT.ID is 'PK';

comment on column HENDELSE_TK_AKSJONSPUNKT.TILBAKEKREVING_HENDELSE_ID is 'FK til hendelse';

comment on column HENDELSE_TK_AKSJONSPUNKT.KODE is 'Aksjonspunkt kode';

comment on column HENDELSE_TK_AKSJONSPUNKT.STATUS is 'Aksjonspunkt status';

create index IDX_HENDELSE_TK_AKSJONSPUNKT_1
    on HENDELSE_TK_AKSJONSPUNKT (TILBAKEKREVING_HENDELSE_ID);

create table DRIFTSMELDING
(
    ID            NUMBER(19) not null
        constraint PK_DRIFTSMELDING
        primary key,
    MELDING       VARCHAR2(1500 char),
    AKTIV_FRA     TIMESTAMP(3),
    AKTIV_TIL     TIMESTAMP(3),
    VERSJON       NUMBER(19)        default 0,
    OPPRETTET_AV  VARCHAR2(20 char) default 'VL',
    OPPRETTET_TID TIMESTAMP(3)      default systimestamp,
    ENDRET_AV     VARCHAR2(20 char),
    ENDRET_TID    TIMESTAMP(3)
);

comment on table DRIFTSMELDING is 'Driftsmeldinger til saksbehandlere';

comment on column DRIFTSMELDING.ID is 'Primary Key';

comment on column DRIFTSMELDING.MELDING is 'Body tekst driftsmelding';

comment on column DRIFTSMELDING.AKTIV_FRA is 'Driftsmelding er aktiv fra';

comment on column DRIFTSMELDING.AKTIV_TIL is 'Driftsmelding er aktiv til';

comment on column DRIFTSMELDING.VERSJON is 'angir versjon for optimistisk låsing';

comment on column DRIFTSMELDING.OPPRETTET_AV is 'Driftsmelding opprettet av';

comment on column DRIFTSMELDING.OPPRETTET_TID is 'Tidspunkt da driftsmeldingen ble opprettet';

comment on column DRIFTSMELDING.ENDRET_AV is 'Driftsmelding endret av';

comment on column DRIFTSMELDING.ENDRET_TID is 'Timestamp endring';

create index IDX_DRIFTSMELDING_AKTIVTIL
    on DRIFTSMELDING (AKTIV_TIL);

create global temporary table HT_HENDELSE
(
    ID NUMBER(19) not null
)
    on commit delete rows;

create global temporary table HT_OPPGAVE
(
    ID NUMBER(19) not null
)
    on commit delete rows;

create global temporary table HT_TILBAKEKREVING_EGENSKAPER
(
    OPPGAVE_ID NUMBER(19) not null
)
    on commit delete rows;

create global temporary table HT_HENDELSE_TILBAKEKREVING
(
    HENDELSE_ID NUMBER(19) not null
)
    on commit delete rows;

create table STATISTIKK_KO
(
    ID                    NUMBER(19)                             not null
        constraint PK_STATISTIKK_KO
        primary key,
    OPPGAVE_ID            NUMBER(19)                             not null,
    OPPGAVE_FILTRERING_ID NUMBER(19)                             not null,
    BEHANDLING_TYPE       VARCHAR2(100 char)                     not null,
    HENDELSE              VARCHAR2(20 char)                      not null,
    VERSJON               NUMBER(19)        default 0,
    OPPRETTET_AV          VARCHAR2(20 char) default 'VL'         not null,
    OPPRETTET_TID         TIMESTAMP(3)      default systimestamp not null,
    ENDRET_AV             VARCHAR2(20 char),
    ENDRET_TID            TIMESTAMP(3)
);

comment on table STATISTIKK_KO is 'Data om endringer i oppgavebeholdning';

comment on column STATISTIKK_KO.ID is 'Primary Key';

comment on column STATISTIKK_KO.OPPGAVE_ID is 'Refererer til oppgave';

comment on column STATISTIKK_KO.OPPGAVE_FILTRERING_ID is 'Refererer til oppgavekø';

comment on column STATISTIKK_KO.BEHANDLING_TYPE is 'Behandlingtype';

comment on column STATISTIKK_KO.HENDELSE is 'Hendelsetype';

comment on column STATISTIKK_KO.VERSJON is 'angir versjon for optimistisk låsing hvor aktuelt';

comment on column STATISTIKK_KO.OPPRETTET_AV is 'Opprettet av';

comment on column STATISTIKK_KO.OPPRETTET_TID is 'Tidspunkt opprettet';

comment on column STATISTIKK_KO.ENDRET_AV is 'Endret av';

comment on column STATISTIKK_KO.ENDRET_TID is 'Timestamp endring';

create index IDX_KOSTAT_OPPR
    on STATISTIKK_KO (OPPRETTET_TID);

create view TILBAKEKREVING_OPPGAVE as
SELECT o.ID,
       o.BEHANDLING_ID,
       o.FAGSAK_SAKSNR,
       o.AKTOR_ID,
       o.BEHANDLENDE_ENHET,
       o.AKTIV,
       o.BEHANDLING_TYPE,
       o.FAGSAK_YTELSE_TYPE,
       o.SYSTEM,
       o.BEHANDLINGSFRIST,
       o.BEHANDLING_OPPRETTET,
       o.VERSJON,
       o.OPPRETTET_AV,
       o.OPPRETTET_TID,
       o.ENDRET_AV,
       o.ENDRET_TID,
       o.FORSTE_STONADSDAG,
       o.BEHANDLING_STATUS,
       o.UTFORT_FRA_ADMIN,
       o.OPPGAVE_AVSLUTTET,
       toe.BELOP,
       toe.FEILUTBETALINGSTART
FROM OPPGAVE o
         INNER JOIN TILBAKEKREVING_EGENSKAPER toe
                    ON o.id = toe.OPPGAVE_ID;

comment on table TILBAKEKREVING_OPPGAVE is 'View som inneholder informasjon om behandlinger for tilbakebetaling som trenger intervensjon av en saksbehandler';

comment on column TILBAKEKREVING_OPPGAVE.ID is 'PK';

comment on column TILBAKEKREVING_OPPGAVE.BEHANDLING_ID is 'ID for behandlingeng';

comment on column TILBAKEKREVING_OPPGAVE.FAGSAK_SAKSNR is 'Referansenr for fagsak';

comment on column TILBAKEKREVING_OPPGAVE.AKTOR_ID is 'Aktør id';

comment on column TILBAKEKREVING_OPPGAVE.BEHANDLENDE_ENHET is 'Enheten som har oppgaven satt til seg';

comment on column TILBAKEKREVING_OPPGAVE.AKTIV is 'Setter oppgaveegenskap til aktiv eller inaktiv etter egenskapen sin tilstand';

comment on column TILBAKEKREVING_OPPGAVE.BEHANDLING_TYPE is 'Hva slags behandlingstype behandlingen har';

comment on column TILBAKEKREVING_OPPGAVE.FAGSAK_YTELSE_TYPE is 'Hva slags ytelse type fagsaken har';

comment on column TILBAKEKREVING_OPPGAVE.SYSTEM is 'Hvilket system oppgaven kommer fra';

comment on column TILBAKEKREVING_OPPGAVE.BEHANDLINGSFRIST is 'Behandlingsfrist';

comment on column TILBAKEKREVING_OPPGAVE.BEHANDLING_OPPRETTET is 'Tidspunktet behandlingen ble opprettet';

comment on column TILBAKEKREVING_OPPGAVE.FORSTE_STONADSDAG is 'Første stønadsdag';

comment on column TILBAKEKREVING_OPPGAVE.BEHANDLING_STATUS is 'Status for behandlingen';

comment on column TILBAKEKREVING_OPPGAVE.UTFORT_FRA_ADMIN is 'Settes til true når Oppgave er endret fra admin-tjeneste';

comment on column TILBAKEKREVING_OPPGAVE.OPPGAVE_AVSLUTTET is 'Tidspunkt for avslutting av Oppgave';

comment on column TILBAKEKREVING_OPPGAVE.BELOP is 'Beløpet som henvendelsen gjelder';

comment on column TILBAKEKREVING_OPPGAVE.FEILUTBETALINGSTART is 'Dato for første feilutbetaling';

