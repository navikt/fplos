alter table STAT_OPPGAVE_FILTER add INNSLAG_TYPE VARCHAR2(20 char);
update STAT_OPPGAVE_FILTER set INNSLAG_TYPE = 'REGELMESSIG' where INNSLAG_TYPE is null;
alter table STAT_OPPGAVE_FILTER modify INNSLAG_TYPE not null;
comment on column STAT_OPPGAVE_FILTER.INNSLAG_TYPE is 'REGELMESSIG for målinger gjort regelmessig hver time, SNAPSHOT for øyeblikksbilder.';

create index idx_statistikk_oppgave_filter_søk on STAT_OPPGAVE_FILTER (OPPGAVE_FILTER_ID, INNSLAG_TYPE);
