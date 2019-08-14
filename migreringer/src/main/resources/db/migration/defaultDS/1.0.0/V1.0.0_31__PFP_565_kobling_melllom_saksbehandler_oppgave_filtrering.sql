CREATE TABLE FILTRERING_SAKSBEHANDLER (
  SAKSBEHANDLER_ID NUMBER(19, 0) NOT NULL,
  OPPGAVE_FILTRERING_ID NUMBER(19, 0) NOT NULL,
  CONSTRAINT PK_FILTRERING_SAKSBEHANDLER PRIMARY KEY (SAKSBEHANDLER_ID, OPPGAVE_FILTRERING_ID),
  CONSTRAINT FK_FILTRERING_SAKSBEHANDLER_1 FOREIGN KEY (SAKSBEHANDLER_ID) REFERENCES SAKSBEHANDLER(ID),
  CONSTRAINT FK_FILTRERING_SAKSBEHANDLER_2 FOREIGN KEY (OPPGAVE_FILTRERING_ID) REFERENCES OPPGAVE_FILTRERING(ID)
);

CREATE INDEX IDX_FILTRERING_SAKSBEHANDLER_1 on FILTRERING_SAKSBEHANDLER(SAKSBEHANDLER_ID);
CREATE INDEX IDX_FILTRERING_SAKSBEHANDLER_2 on FILTRERING_SAKSBEHANDLER(OPPGAVE_FILTRERING_ID);

COMMENT ON TABLE FILTRERING_SAKSBEHANDLER is 'Tabell som er koblingen mellom avdeling og saksbehandler';

COMMENT ON COLUMN FILTRERING_SAKSBEHANDLER.SAKSBEHANDLER_ID IS 'FK til saksbehandler';
COMMENT ON COLUMN FILTRERING_SAKSBEHANDLER.OPPGAVE_FILTRERING_ID IS 'FK til oppgave filtrering';