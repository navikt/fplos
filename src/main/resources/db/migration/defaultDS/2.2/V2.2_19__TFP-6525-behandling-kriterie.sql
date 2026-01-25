create table BEHANDLING_EGENSKAP
(
    BEHANDLING_ID           RAW(16)               not null
        constraint FK_BEHANDLING_EGENSKAP_01 references BEHANDLING(ID),
    ANDRE_KRITERIER_TYPE    VARCHAR2(100 char)    not null,
    constraint PK_BEHANDLING_EGENSKAP primary key (BEHANDLING_ID, ANDRE_KRITERIER_TYPE)
);

comment on table BEHANDLING_EGENSKAP is 'Tabell som inneholder egenskaper for en behandling';
comment on column BEHANDLING_EGENSKAP.BEHANDLING_ID is 'Referanse til behandling';
comment on column BEHANDLING_EGENSKAP.ANDRE_KRITERIER_TYPE is 'Kode for de andre kriteriene oppgavene skal filtreres på';

create index IDX_BEHANDLING_EGENSKAP_1 on BEHANDLING_EGENSKAP (BEHANDLING_ID);

create index IDX_BEHANDLING_EGENSKAP_2 on BEHANDLING_EGENSKAP (ANDRE_KRITERIER_TYPE);
