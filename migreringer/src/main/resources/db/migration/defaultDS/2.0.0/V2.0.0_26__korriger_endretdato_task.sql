INSERT INTO PROSESS_TASK_TYPE (KODE, NAVN, FEIL_MAKS_FORSOEK, FEIL_SEK_MELLOM_FORSOEK, FEILHANDTERING_ALGORITME, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID, CRON_EXPRESSION)
VALUES ('oppgaveendretdato.oppdaterer', 'Korrigerer endretdato', 3, 30, 'DEFAULT', 'Korrigerer endretdato etter kafkaproblemer', 'VL', CURRENT_TIMESTAMP, null, null, null);