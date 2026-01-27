alter table STAT_OPPGAVE_FILTER add ANTALL_VENTENDE NUMBER(10);

comment on column STAT_OPPGAVE_FILTER.ANTALL_VENTENDE is 'Antall ventende behandlinger';
