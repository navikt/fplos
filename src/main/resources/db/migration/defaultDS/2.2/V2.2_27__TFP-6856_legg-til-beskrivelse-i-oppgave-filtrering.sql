alter table OPPGAVE_FILTRERING add BESKRIVELSE VARCHAR2(500 char);

comment on column OPPGAVE_FILTRERING.BESKRIVELSE is 'Beskrivelse av oppgavefiltrering.';
