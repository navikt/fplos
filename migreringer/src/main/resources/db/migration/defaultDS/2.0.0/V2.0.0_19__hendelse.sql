create table "HENDELSE" (
    ID number(19) not null,
    FAGSYSTEM varchar2(100 CHAR) not null,
    BEHANDLING_ID RAW(16) not null,
    BEHANDLENDE_ENHET varchar2(100 CHAR) not null,
    SAKSNUMMER varchar2(100 CHAR) not null,
    AKTØR_ID varchar2(100 CHAR) not null,
    BEHANDLING_OPPRETTET_TIDSPUNKT TIMESTAMP(3) not null,
    BEHANDLING_TYPE varchar2(10 CHAR) not null,
    YTELSE_TYPE varchar2(10 CHAR) not null,
    VERSJON NUMBER(19,0) DEFAULT 0,
	OPPRETTET_AV VARCHAR2(20 CHAR) DEFAULT 'VL',
	OPPRETTET_TID TIMESTAMP (3) DEFAULT systimestamp,
	ENDRET_AV VARCHAR2(20 CHAR),
	ENDRET_TID TIMESTAMP (3)
);
CREATE SEQUENCE SEQ_HENDELSE MINVALUE 10000 INCREMENT BY 50 START WITH 10000 NOCACHE ORDER NOCYCLE;

alter table HENDELSE add constraint PK_HENDELSE primary key (ID);

COMMENT ON COLUMN HENDELSE.ID IS 'Primary Key';
COMMENT ON COLUMN HENDELSE.FAGSYSTEM IS 'Hvilket fagsystem hendelsen kommer fra';
COMMENT ON COLUMN HENDELSE.BEHANDLING_ID IS 'Behandling id til behandlingen i fagsystemet';
COMMENT ON COLUMN HENDELSE.BEHANDLENDE_ENHET IS 'Enhet som behandler';
COMMENT ON COLUMN HENDELSE.SAKSNUMMER IS 'Saksnummer i fagsystemet';
COMMENT ON COLUMN HENDELSE.AKTØR_ID IS 'Aktør id som omhandler hendelsen';
COMMENT ON COLUMN HENDELSE.BEHANDLING_OPPRETTET_TIDSPUNKT IS 'Tidspunkt da behandlingen ble opprettet';
COMMENT ON COLUMN HENDELSE.BEHANDLING_TYPE IS 'Type behandling';
COMMENT ON COLUMN HENDELSE.YTELSE_TYPE IS 'Type ytelse';
COMMENT ON TABLE HENDELSE IS 'Mellomlagring av hendelse fra kafka, tas og ryddes av prosesstasks';

create table "HENDELSE_TILBAKEKREVING" (
    HENDELSE_ID number(19) not null,
    FEILUTBETALT_BELØP number(19, 2),
    HREF varchar2(500 CHAR),
    FØRSTE_FEILUTBETALING_DATO TIMESTAMP(3),
    ANSVARLIG_SAKSBEHANDLER varchar2(100 CHAR),
    VERSJON NUMBER(19,0) DEFAULT 0,
	OPPRETTET_AV VARCHAR2(20 CHAR) DEFAULT 'VL',
	OPPRETTET_TID TIMESTAMP (3) DEFAULT systimestamp,
	ENDRET_AV VARCHAR2(20 CHAR),
	ENDRET_TID TIMESTAMP (3)
);

alter table HENDELSE_TILBAKEKREVING add constraint PK_HENDELSE_TILBAKEKREVING primary key (HENDELSE_ID);
alter table HENDELSE_TILBAKEKREVING add constraint FK_HENDELSE_TILBAKEKREVING foreign key (HENDELSE_ID) references HENDELSE (ID);

COMMENT ON COLUMN HENDELSE_TILBAKEKREVING.HENDELSE_ID IS 'Foreign key til HENDELSE';
COMMENT ON COLUMN HENDELSE_TILBAKEKREVING.FEILUTBETALT_BELØP IS 'Beløp som er feilutbetalt';
COMMENT ON COLUMN HENDELSE_TILBAKEKREVING.HREF IS 'Referanse til behandling';
COMMENT ON COLUMN HENDELSE_TILBAKEKREVING.FØRSTE_FEILUTBETALING_DATO IS 'Dato fra første feilutbetaling';
COMMENT ON COLUMN HENDELSE_TILBAKEKREVING.ANSVARLIG_SAKSBEHANDLER IS 'Ansvarlig saksbehandler';
COMMENT ON TABLE HENDELSE_TILBAKEKREVING IS 'Mellomlagring av tilbakekrevinghendelse fra kafka, tas og ryddes av prosesstasks';

create table "HENDELSE_TK_AKSJONSPUNKT" (
    ID number(19) not null,
    TILBAKEKREVING_HENDELSE_ID number(19) not null,
    KODE varchar2(20 CHAR) not null,
    STATUS varchar2(20 CHAR) not null,
    VERSJON NUMBER(19,0) DEFAULT 0,
	OPPRETTET_AV VARCHAR2(20 CHAR) DEFAULT 'VL',
	OPPRETTET_TID TIMESTAMP (3) DEFAULT systimestamp,
	ENDRET_AV VARCHAR2(20 CHAR),
	ENDRET_TID TIMESTAMP (3)
);

alter table HENDELSE_TK_AKSJONSPUNKT add constraint PK_HENDELSE_TK_AKSJONSPUNKT primary key (ID);
alter table HENDELSE_TK_AKSJONSPUNKT add constraint FK_TILBAKEKREVING_HENDELSE_ID foreign key (TILBAKEKREVING_HENDELSE_ID) references HENDELSE_TILBAKEKREVING (HENDELSE_ID);

CREATE INDEX IDX_HENDELSE_TK_AKSJONSPUNKT_1 ON HENDELSE_TK_AKSJONSPUNKT (TILBAKEKREVING_HENDELSE_ID);
CREATE SEQUENCE SEQ_HENDELSE_TK_AKSJONSPUNKT MINVALUE 10000 INCREMENT BY 50 START WITH 10000 NOCACHE ORDER NOCYCLE;

COMMENT ON COLUMN HENDELSE_TK_AKSJONSPUNKT.ID IS 'PK';
COMMENT ON COLUMN HENDELSE_TK_AKSJONSPUNKT.TILBAKEKREVING_HENDELSE_ID IS 'FK til hendelse';
COMMENT ON COLUMN HENDELSE_TK_AKSJONSPUNKT.KODE IS 'Aksjonspunkt kode';
COMMENT ON COLUMN HENDELSE_TK_AKSJONSPUNKT.STATUS IS 'Aksjonspunkt status';
COMMENT ON TABLE HENDELSE_TK_AKSJONSPUNKT IS 'Mellomlagring av aksjonspunkter i tilbakekrevinghendelse fra kafka, tas og ryddes av prosesstasks';

COMMENT ON COLUMN PROSESS_TASK.ID IS 'Primary Key';
COMMENT ON COLUMN PROSESS_TASK.TASK_TYPE IS 'navn på task. Brukes til å matche riktig implementasjon';
COMMENT ON COLUMN PROSESS_TASK.PRIORITET IS 'prioritet på task.  Høyere tall har høyere prioritet';
COMMENT ON COLUMN PROSESS_TASK.STATUS IS 'status på task: KLAR, NYTT_FORSOEK, FEILET, VENTER_SVAR, FERDIG';
COMMENT ON COLUMN PROSESS_TASK.TASK_PARAMETERE IS 'parametere angitt for en task';
COMMENT ON COLUMN PROSESS_TASK.TASK_PAYLOAD IS 'inputdata for en task';
COMMENT ON COLUMN PROSESS_TASK.TASK_GRUPPE IS 'angir en unik id som grupperer flere ';
COMMENT ON COLUMN PROSESS_TASK.TASK_SEKVENS IS 'angir rekkefølge på task innenfor en gruppe ';
COMMENT ON COLUMN PROSESS_TASK.NESTE_KJOERING_ETTER IS 'tasken skal ikke kjøeres før tidspunkt er passert';
COMMENT ON COLUMN PROSESS_TASK.FEILEDE_FORSOEK IS 'antall feilede forsøk';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_TS IS 'siste gang tasken ble forsøkt kjørt (før kjøring)';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_FEIL_KODE IS 'siste feilkode tasken fikk';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_FEIL_TEKST IS 'siste feil tasken fikk';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_SERVER IS 'navn på node som sist kjørte en task (server@pid)';
COMMENT ON COLUMN PROSESS_TASK.VERSJON IS 'angir versjon for optimistisk låsing';
COMMENT ON COLUMN PROSESS_TASK.BLOKKERT_AV IS 'Id til ProsessTask som blokkerer kjøring av denne (når status=VETO)';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_SLUTT_TS IS 'tidsstempel siste gang tasken ble kjørt (etter kjøring)';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_PLUKK_TS IS 'siste gang tasken ble forsøkt plukket (fra db til in-memory, før kjøring)';
COMMENT ON TABLE PROSESS_TASK IS 'Inneholder tasks som skal kjøres i bakgrunnen';
