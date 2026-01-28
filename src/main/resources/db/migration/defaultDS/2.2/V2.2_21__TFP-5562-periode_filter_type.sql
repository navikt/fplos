alter table OPPGAVE_FILTRERING add PERIODEFILTER_TYPE VARCHAR2(40 char) default 'FAST_PERIODE' not null;

comment on column OPPGAVE_FILTRERING.PERIODEFILTER_TYPE is 'Type periodefilter som skal brukes for oppgavefiltrering.';

update OPPGAVE_FILTRERING set PERIODEFILTER_TYPE = 'RELATIV_PERIODE_DAGER' where ER_DYNAMISK_PERIODE = 'J';
