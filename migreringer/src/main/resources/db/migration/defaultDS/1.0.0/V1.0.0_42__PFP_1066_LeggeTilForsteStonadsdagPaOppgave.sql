ALTER TABLE OPPGAVE ADD FORSTE_STONADSDAG date;
COMMENT ON COLUMN OPPGAVE.FORSTE_STONADSDAG IS 'Første stønadsdag';

INSERT INTO KODELISTE (id, kodeverk, kode, beskrivelse, gyldig_fom)
VALUES(seq_kodeliste.nextval, 'KO_SORTERING', 'FORSTONAD', 'Dato for første stønadsdag', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE_NAVN_I18N (ID, KL_KODEVERK, KL_KODE, SPRAK, NAVN)
VALUES (SEQ_KODELISTE_NAVN_I18N.NEXTVAL, 'KO_SORTERING', 'FORSTONAD', 'NB', 'Dato for første stønadsdag');