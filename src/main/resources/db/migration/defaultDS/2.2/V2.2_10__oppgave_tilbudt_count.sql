alter table OPPGAVE add TILBUDT_COUNT integer;

comment on column oppgave.TILBUDT_COUNT is 'Antall ganger oppgaven er blitt tilbudt saksbehandler';
