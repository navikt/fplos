alter table STAT_OPPGAVE_FILTER add ANTALL_OPPRETTET NUMBER(10);

comment on column STAT_OPPGAVE_FILTER.ANTALL_OPPRETTET is 'Antall opprettet oppaver siden sist innslag';

alter table STAT_OPPGAVE_FILTER add ANTALL_AVSLUTTET NUMBER(10);

comment on column STAT_OPPGAVE_FILTER.ANTALL_AVSLUTTET is 'Antall avsluttet oppaver siden sist innslag';
