create table STAT_ENHET_YTELSE_BEHANDLING
(
    BEHANDLENDE_ENHET    VARCHAR2(10 char)  not null,
    TIDSSTEMPEL          NUMBER(19)         not null,
    FAGSAK_YTELSE_TYPE   VARCHAR2(100 char) not null,
    BEHANDLING_TYPE      VARCHAR2(100 char) not null,
    STAT_DATO            DATE               not null,
    ANTALL_AKTIVE        NUMBER(10)         not null,
    ANTALL_OPPRETTET     NUMBER(10)         not null,
    ANTALL_AVSLUTTET     NUMBER(10)         not null,
    CONSTRAINT PK_STAT_ENHET_YTELSE_BEHANDLING
        primary key (BEHANDLENDE_ENHET, TIDSSTEMPEL, FAGSAK_YTELSE_TYPE, BEHANDLING_TYPE)
);

comment on table STAT_ENHET_YTELSE_BEHANDLING is 'Daglig statistikk over antall aktive, opprettede og avsluttede oppgaver';

comment on column STAT_ENHET_YTELSE_BEHANDLING.BEHANDLENDE_ENHET is 'Hvilken enhet';
comment on column STAT_ENHET_YTELSE_BEHANDLING.TIDSSTEMPEL is 'Tidsstempel for når statistikken er tatt';
comment on column STAT_ENHET_YTELSE_BEHANDLING.FAGSAK_YTELSE_TYPE is 'Telling for ytelse';
comment on column STAT_ENHET_YTELSE_BEHANDLING.BEHANDLING_TYPE is 'Telling for behandlingstype';
comment on column STAT_ENHET_YTELSE_BEHANDLING.STAT_DATO is 'Telling for dato';
comment on column STAT_ENHET_YTELSE_BEHANDLING.ANTALL_AKTIVE is 'Antall aktive oppgaver';
comment on column STAT_ENHET_YTELSE_BEHANDLING.ANTALL_OPPRETTET is 'Antall oppgaver opprettet siden forrige telling';
comment on column STAT_ENHET_YTELSE_BEHANDLING.ANTALL_AVSLUTTET is 'Antall oppgaver avsluttet siden forrige telling';
