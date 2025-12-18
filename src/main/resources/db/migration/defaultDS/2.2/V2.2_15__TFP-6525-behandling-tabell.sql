create table BEHANDLING
(
    ID        RAW(16)                         not null
        constraint PK_BEHANDLING
        primary key,
    AKTOR_ID            VARCHAR2(50 char)    not null,
    SAKSNUMMER           VARCHAR2(19 char)    not null,
    FAGSAK_YTELSE_TYPE   VARCHAR2(100 char)   not null,
    KILDESYSTEM          VARCHAR2(100 char)   not null,
    BEHANDLENDE_ENHET    VARCHAR2(10 char)    not null,
    BEHANDLING_TYPE      VARCHAR2(100 char)   not null,
    BEHANDLING_TILSTAND  VARCHAR2(100 char)   not null,
    AKTIVE_AKSJONSPUNKT  VARCHAR2(200 char),
    VENTEFRIST           TIMESTAMP(3),
    OPPRETTET            TIMESTAMP(3)         not null,
    AVSLUTTET            TIMESTAMP(3),
    BEHANDLINGSFRIST     DATE,
    FORSTE_STONADSDAG    DATE,
    FEILUTBETALING_BELOP NUMBER(19, 2),
    FEILUTBETALING_START DATE,
    VERSJON              NUMBER(19)           default 0            not null,
    OPPRETTET_AV         VARCHAR2(20 char)    default 'VL'         not null,
    OPPRETTET_TID        TIMESTAMP(3)         default systimestamp not null,
    ENDRET_AV            VARCHAR2(20 char),
    ENDRET_TID           TIMESTAMP(3)
);


comment on table BEHANDLING is 'Tabell som speiler behandling og status fra kildesystem';

comment on column BEHANDLING.ID is 'PK - behandling sin uuid';
comment on column BEHANDLING.AKTOR_ID is 'AktørId for saken';
comment on column BEHANDLING.SAKSNUMMER is 'Saksnummer for fagsaken';
comment on column BEHANDLING.BEHANDLING_TYPE is 'Hva slags behandlingstype behandlingen har';
comment on column BEHANDLING.FAGSAK_YTELSE_TYPE is 'Hva slags ytelse type fagsaken har';
comment on column BEHANDLING.KILDESYSTEM is 'Hvilket system oppgaven kommer fra';
comment on column BEHANDLING.BEHANDLENDE_ENHET is 'Hvilken enhet';
comment on column BEHANDLING.BEHANDLING_TILSTAND is 'Behandlingstilstand';
comment on column BEHANDLING.AKTIVE_AKSJONSPUNKT is 'Åpne aksjonspnkt, sorterte koder';
comment on column BEHANDLING.VENTEFRIST is 'Tidligste ventefrist for behandlinger på vent';
comment on column BEHANDLING.OPPRETTET is 'Tidspunktet behandlingen ble opprettet';
comment on column BEHANDLING.AVSLUTTET is 'Tidspunktet behandlingen ble avsluttet';
comment on column BEHANDLING.BEHANDLINGSFRIST is 'Behandlingsfrist';
comment on column BEHANDLING.FORSTE_STONADSDAG is 'Første stønadsdag';
comment on column BEHANDLING.FEILUTBETALING_BELOP is 'Feilutbetalt beløp fra fptilbake';
comment on column BEHANDLING.FEILUTBETALING_START is 'Startdato for feilutbetaling fra fptilbake';

create index IDX_BEHANDLING_1
    on BEHANDLING (BEHANDLING_TILSTAND);

create index IDX_BEHANDLING_2
    on BEHANDLING (FAGSAK_YTELSE_TYPE);

create index IDX_BEHANDLING_3
    on BEHANDLING (BEHANDLING_TYPE);

create index IDX_BEHANDLING_4
    on BEHANDLING (BEHANDLENDE_ENHET);

create index IDX_BEHANDLING_5
    on BEHANDLING (BEHANDLINGSFRIST);

create index IDX_BEHANDLING_6
    on BEHANDLING (OPPRETTET);

create index IDX_BEHANDLING_7
    on BEHANDLING (FORSTE_STONADSDAG);

create index IDX_BEHANDLING_8
    on BEHANDLING (FEILUTBETALING_BELOP);

create index IDX_BEHANDLING_9
    on BEHANDLING (FEILUTBETALING_START);

create index IDX_BEHANDLING_10
    on BEHANDLING (VENTEFRIST);

create index IDX_BEHANDLING_11
    on BEHANDLING (AVSLUTTET);
