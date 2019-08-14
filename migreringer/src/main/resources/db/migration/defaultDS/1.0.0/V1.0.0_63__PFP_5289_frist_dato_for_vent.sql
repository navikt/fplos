ALTER TABLE OPPGAVE_EVENT_LOGG ADD FRIST_TID TIMESTAMP(3);
COMMENT ON COLUMN OPPGAVE_EVENT_LOGG.FRIST_TID is 'Behandling blir automatisk gjenopptatt etter dette tidspunktet';