CREATE TABLE OPPGAVE (
  ID NUMBER(19,0) NOT NULL
  ,BEHANDLING_ID NUMBER(19,0) NOT NULL
  ,FAGSAK_ID NUMBER(19,0) NOT NULL
  ,FAGSAK_SAKSNR NUMBER(19,0) NOT NULL
  ,AKTOR_ID NUMBER(19,0)
  ,SAKSBEHANDLER_ID VARCHAR2(20 CHAR)
  ,BEHANDLENDE_ENHET VARCHAR2(10 CHAR) NOT NULL
  ,BEHANDLENDE_ENHET_NAVN VARCHAR2(320 CHAR) NOT NULL
  ,RESERVERT_TIL TIMESTAMP(3)
  ,AKTIV VARCHAR2(1 CHAR) DEFAULT 'Y' NOT NULL
  ,BEHANDLING_TYPE VARCHAR2(100 CHAR) NOT NULL
  ,FAGSAK_YTELSE_TYPE VARCHAR2(100 CHAR) NOT NULL
  ,SYSTEM VARCHAR2(100 CHAR)
  ,BEHANDLINGSFRIST TIMESTAMP(3)
  ,BEHANDLING_OPPRETTET TIMESTAMP(3)
  ,KL_BEHANDLING_TYPE VARCHAR(100 CHAR) AS ('BEHANDLING_TYPE') NOT NULL
  ,KL_FAGSAK_YTELSE_TYPE VARCHAR(100 CHAR) AS ('FAGSAK_YTELSE') NOT NULL,
   versjon            NUMBER(19, 0) DEFAULT 0           NOT NULL,
   opprettet_av       VARCHAR2(20 CHAR) DEFAULT 'VL'    NOT NULL,
   opprettet_tid      TIMESTAMP(3) DEFAULT systimestamp NOT NULL,
   endret_av          VARCHAR2(20 CHAR),
   endret_tid         TIMESTAMP(3)
  ,CONSTRAINT PK_OPPGAVE PRIMARY KEY (ID)
);

CREATE SEQUENCE SEQ_OPPGAVE MINVALUE 1 START WITH 1 INCREMENT BY 50 NOCACHE NOCYCLE;

create index IDX_OPPGAVE_1 on OPPGAVE(BEHANDLING_TYPE);

create index IDX_OPPGAVE_2 on OPPGAVE(FAGSAK_YTELSE_TYPE);

ALTER TABLE OPPGAVE ADD CONSTRAINT FK_OPPGAVE_1
FOREIGN KEY (KL_BEHANDLING_TYPE, BEHANDLING_TYPE) REFERENCES KODELISTE (kodeverk, kode);

ALTER TABLE OPPGAVE ADD CONSTRAINT FK_OPPGAVE_2
FOREIGN KEY (KL_FAGSAK_YTELSE_TYPE, FAGSAK_YTELSE_TYPE) REFERENCES KODELISTE (kodeverk, kode);

COMMENT ON TABLE OPPGAVE is 'Tabell som skal inneholde informasjon om behandlinger som trenger intervensjon av en saksbehandler';

COMMENT ON COLUMN OPPGAVE.ID IS 'PK';
COMMENT ON COLUMN OPPGAVE.BEHANDLING_ID IS 'ID for behandlingeng';
COMMENT ON COLUMN OPPGAVE.FAGSAK_ID IS 'ID for saken';
COMMENT ON COLUMN OPPGAVE.FAGSAK_SAKSNR IS 'Referansenr for fagsak';
COMMENT ON COLUMN OPPGAVE.AKTOR_ID IS 'Aktør id';
COMMENT ON COLUMN OPPGAVE.SAKSBEHANDLER_ID IS 'Saksbehandlerident for saksbehandler som har reservert oppgaven';
COMMENT ON COLUMN OPPGAVE.BEHANDLENDE_ENHET IS 'Enheten som har oppgaven satt til seg';
COMMENT ON COLUMN OPPGAVE.BEHANDLENDE_ENHET_NAVN IS 'Navnet på enheten som har oppgaven satt til seg';
COMMENT ON COLUMN OPPGAVE.RESERVERT_TIL IS 'Tidspunkt for når reservasjonen går ut';
COMMENT ON COLUMN OPPGAVE.AKTIV IS 'Hvis behandlingen er ferdig så er ikke oppgaveinnslaget lenger aktivt';
COMMENT ON COLUMN OPPGAVE.AKTIV IS 'Hvilket system som har oppgaven feks fpsak fptilbake eller fpklage';
COMMENT ON COLUMN OPPGAVE.BEHANDLING_TYPE IS 'Hva slags behandlingstype behandlingen har';
COMMENT ON COLUMN OPPGAVE.FAGSAK_YTELSE_TYPE  IS 'Hva slags ytelse type fagsaken har';
COMMENT ON COLUMN OPPGAVE.SYSTEM IS 'Hvilket system oppgaven kommer fra';
COMMENT ON COLUMN OPPGAVE.BEHANDLINGSFRIST IS 'Behandlingsfrist';
COMMENT ON COLUMN OPPGAVE.BEHANDLING_OPPRETTET IS 'Tidspunktet behandlingen ble opprettet';
COMMENT ON COLUMN OPPGAVE.KL_BEHANDLING_TYPE IS 'Kodeverk behandlingtype';
COMMENT ON COLUMN OPPGAVE.KL_FAGSAK_YTELSE_TYPE IS 'Kodeverk fagsakytelsetype';


CREATE TABLE RESERVASJON (
  ID NUMBER(19,0) NOT NULL
  ,OPPGAVE_ID NUMBER(19,0) NOT NULL
  ,RESERVERT_TIL TIMESTAMP(3)
  ,RESERVERT_AV VARCHAR2(20 CHAR) DEFAULT 'VL'    NOT NULL,
   versjon            NUMBER(19, 0) DEFAULT 0           NOT NULL,
   opprettet_av       VARCHAR2(20 CHAR) DEFAULT 'VL'    NOT NULL,
   opprettet_tid      TIMESTAMP(3) DEFAULT systimestamp NOT NULL,
   endret_av          VARCHAR2(20 CHAR),
   endret_tid         TIMESTAMP(3)
  ,CONSTRAINT FK_RESERVASJON_1 FOREIGN KEY (OPPGAVE_ID) REFERENCES OPPGAVE
  ,CONSTRAINT PK_RESERVASJON PRIMARY KEY (ID)
);

CREATE SEQUENCE SEQ_RESERVASJON MINVALUE 1 START WITH 1 INCREMENT BY 50 NOCACHE NOCYCLE;

create index IDX_RESERVASJON_1 on RESERVASJON(OPPGAVE_ID);

COMMENT ON TABLE RESERVASJON is 'Tabell som skal inneholde reservasjoner av oppgaver';
COMMENT ON COLUMN RESERVASJON.ID IS 'PK';
COMMENT ON COLUMN RESERVASJON.RESERVERT_TIL IS 'Tidspunkt for når reservasjonen går ut';
COMMENT ON COLUMN RESERVASJON.RESERVERT_AV IS 'Saksbehandler ident til saksbehandler som har reservert oppgaven';
