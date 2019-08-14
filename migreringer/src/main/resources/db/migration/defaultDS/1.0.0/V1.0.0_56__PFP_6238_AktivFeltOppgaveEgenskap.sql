ALTER TABLE OPPGAVE_EGENSKAP ADD AKTIV VARCHAR2(1 CHAR) DEFAULT 'J' NOT NULL;
COMMENT ON COLUMN OPPGAVE.AKTIV IS 'Setter oppgaveegenskap til aktiv eller inaktiv etter egenskapen sin tilstand';