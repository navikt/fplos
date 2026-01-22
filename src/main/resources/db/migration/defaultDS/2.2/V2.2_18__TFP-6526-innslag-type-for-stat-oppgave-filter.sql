alter table STAT_OPPGAVE_FILTER add INNSLAG_TYPE VARCHAR2(20 char) default 'REGELMESSIG' not null;
update STAT_OPPGAVE_FILTER set INNSLAG_TYPE = 'REGELMESSIG' where INNSLAG_TYPE is null;

comment on column STAT_OPPGAVE_FILTER.INNSLAG_TYPE is 'REGELMESSIG for målinger gjort regelmessig hver time, SNAPSHOT for øyeblikksbilder.';
