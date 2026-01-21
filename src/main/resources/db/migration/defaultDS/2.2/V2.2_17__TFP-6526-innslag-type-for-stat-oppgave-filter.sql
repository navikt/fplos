alter table STAT_OPPGAVE_FILTER add INNSLAG_TYPE VARCHAR2(20) default 'REGELMESSIG' not null;
update STAT_OPPGAVE_FILTER set INNSLAG_TYPE = 'REGELMESSIG' where INNSLAG_TYPE is null;
comment on column STAT_OPPGAVE_FILTER.INNSLAG_TYPE is 'REGELMESSIG for målinger gjort regelmessing hver time, SNAPSHOT for øyeblikksbilder.';

