create sequence seq_prosess_task
    minvalue 1000000
    increment by 50;

create sequence seq_prosess_task_gruppe
    minvalue 10000000
    increment by 1000000;

CREATE TABLE prosess_task
(
    id                        bigint                DEFAULT nextval('SEQ_GLOBAL_PK'),
    task_type                 varchar(50)  NOT NULL,
    prioritet                 smallint     NOT NULL DEFAULT 0,
    status                    varchar(20)  NOT NULL DEFAULT 'KLAR'
        CONSTRAINT chk_prosess_task_status CHECK (status IN ('KLAR', 'FEILET', 'VENTER_SVAR', 'SUSPENDERT', 'VETO', 'FERDIG', 'KJOERT')),
    task_parametere           varchar(4000),
    task_payload              text,
    task_gruppe               varchar(250),
    task_sekvens              varchar(100) NOT NULL DEFAULT '1',
    neste_kjoering_etter      TIMESTAMP(0)          DEFAULT current_timestamp,
    feilede_forsoek           integer               DEFAULT 0,
    siste_kjoering_ts         TIMESTAMP(6),
    siste_kjoering_feil_kode  varchar(50),
    siste_kjoering_feil_tekst text,
    siste_kjoering_server     varchar(50),
    versjon                   bigint       NOT NULL DEFAULT 0,
    opprettet_av              varchar(20)  NOT NULL DEFAULT 'VL',
    opprettet_tid             TIMESTAMP(6) NOT NULL DEFAULT statement_timestamp(),
    blokkert_av               bigint,
    siste_kjoering_slutt_ts   TIMESTAMP(6),
    siste_kjoering_plukk_ts   TIMESTAMP(6),
    CONSTRAINT pk_prosess_task PRIMARY KEY (id)
);
COMMENT
ON TABLE prosess_task IS 'Inneholder tasks som skal kjøres i bakgrunnen';
COMMENT
ON COLUMN prosess_task.blokkert_av IS 'Id til ProsessTask som blokkerer kjøring av denne (når status=VETO)';
COMMENT
ON COLUMN prosess_task.feilede_forsoek IS 'antall feilede forsøk';
COMMENT
ON COLUMN prosess_task.id IS 'Primary Key';
COMMENT
ON COLUMN prosess_task.neste_kjoering_etter IS 'tasken skal ikke kjøeres før tidspunkt er passert';
COMMENT
ON COLUMN prosess_task.prioritet IS 'prioritet på task.  Høyere tall har høyere prioritet';
COMMENT
ON COLUMN prosess_task.siste_kjoering_feil_kode IS 'siste feilkode tasken fikk';
COMMENT
ON COLUMN prosess_task.siste_kjoering_feil_tekst IS 'siste feil tasken fikk';
COMMENT
ON COLUMN prosess_task.siste_kjoering_plukk_ts IS 'siste gang tasken ble forsøkt plukket (fra db til in-memory, før kjøring)';
COMMENT
ON COLUMN prosess_task.siste_kjoering_server IS 'navn på node som sist kjørte en task (server@pid)';
COMMENT
ON COLUMN prosess_task.siste_kjoering_slutt_ts IS 'tidsstempel siste gang tasken ble kjørt (etter kjøring)';
COMMENT
ON COLUMN prosess_task.siste_kjoering_ts IS 'siste gang tasken ble forsøkt kjørt (før kjøring)';
COMMENT
ON COLUMN prosess_task.status IS 'status på task: KLAR, NYTT_FORSOEK, FEILET, VENTER_SVAR, FERDIG';
COMMENT
ON COLUMN prosess_task.task_gruppe IS 'angir en unik id som grupperer flere ';
COMMENT
ON COLUMN prosess_task.task_parametere IS 'parametere angitt for en task';
COMMENT
ON COLUMN prosess_task.task_payload IS 'inputdata for en task';
COMMENT
ON COLUMN prosess_task.task_sekvens IS 'angir rekkefølge på task innenfor en gruppe ';
COMMENT
ON COLUMN prosess_task.task_type IS 'navn på task. Brukes til å matche riktig implementasjon';
COMMENT
ON COLUMN prosess_task.versjon IS 'angir versjon for optimistisk låsing';
