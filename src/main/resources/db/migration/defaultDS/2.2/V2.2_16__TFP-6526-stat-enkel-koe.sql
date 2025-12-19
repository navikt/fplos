create table STAT_KOE
(
    OPPGAVE_FILTER_ID    NUMBER(19)         not null,
    TIDSSTEMPEL          NUMBER(19)         not null,
    STAT_DATO            DATE               not null,
    ANTALL_AKTIVE        NUMBER(10)         not null,
    ANTALL_TILGJENGELIGE NUMBER(10)         not null,
    CONSTRAINT PK_STAT_KOE primary key (OPPGAVE_FILTER_ID, TIDSSTEMPEL)
);

comment on table STAT_KOE is 'Daglig statistikk over antall aktive, opprettede og avsluttede oppgaver i en kø';

comment on column STAT_KOE.OPPGAVE_FILTER_ID is 'Id for oppgavefilteret som køen er basert på';
comment on column STAT_KOE.TIDSSTEMPEL is 'Tidsstempel for når statistikken er tatt';
comment on column STAT_KOE.STAT_DATO is 'Telling for dato';
comment on column STAT_KOE.ANTALL_AKTIVE is 'Antall aktive oppgaver';
comment on column STAT_KOE.ANTALL_TILGJENGELIGE is 'Antall aktive oppgaver som ikke er reservert';
