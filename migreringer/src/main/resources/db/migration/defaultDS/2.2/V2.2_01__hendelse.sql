create table MOTTATT_HENDELSE
(
    HENDELSE_UID VARCHAR2(100 char) not null
        constraint PK_MOTTATT_HENDELSE
        primary key,
    OPPRETTET_AV VARCHAR2(20 char) default 'VL' not null,
    OPPRETTET_TID TIMESTAMP(3) default systimestamp not null,
    ENDRET_AV VARCHAR2(20 char),
    ENDRET_TID TIMESTAMP(3)
);

comment on table MOTTATT_HENDELSE is 'Holder unik identifikator for alle mottatte hendelser.';
comment on column MOTTATT_HENDELSE.HENDELSE_UID is 'Unik identifikator for hendelse mottatt';