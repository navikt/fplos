
CREATE SEQUENCE  "SEQ_TILBAKEKREVING_EGENSKAPER"  MINVALUE 1 INCREMENT BY 50 START WITH 1 NOCACHE  ORDER  NOCYCLE ;

CREATE TABLE "TILBAKEKREVING_EGENSKAPER"
(
    "OPPGAVE_ID"        NUMBER(19, 0),
    "BELOP"             NUMBER(19, 2),
    "FEILUTBETALINGSTART"  TIMESTAMP (3)

);

COMMENT ON TABLE "TILBAKEKREVING_EGENSKAPER"  IS 'Tabell inneholder egenskaper for tilbakekrevingoppgaver';
COMMENT ON COLUMN "TILBAKEKREVING_EGENSKAPER"."OPPGAVE_ID" IS 'Oppgave egenskapene hører til';
COMMENT ON COLUMN "TILBAKEKREVING_EGENSKAPER"."BELOP" IS 'Beløpet som henvendelsen gjelder';
COMMENT ON COLUMN "TILBAKEKREVING_EGENSKAPER"."FEILUTBETALINGSTART" IS 'Tidspunkt når kravet ikke lenger er gyldig';

CREATE UNIQUE INDEX "PK_TILBAKEKREVING_EGENSKAPER" ON "TILBAKEKREVING_EGENSKAPER" ("OPPGAVE_ID")
;

ALTER TABLE "TILBAKEKREVING_EGENSKAPER" MODIFY ("OPPGAVE_ID" NOT NULL ENABLE);
ALTER TABLE "TILBAKEKREVING_EGENSKAPER" ADD CONSTRAINT "PK_TILBAKEKREVING_EGENSKAPER" PRIMARY KEY ("OPPGAVE_ID")
    USING INDEX  ENABLE;



ALTER TABLE "TILBAKEKREVING_EGENSKAPER" ADD CONSTRAINT "FK_TILBAKEKREVING_EGENSKAPER" FOREIGN KEY ("OPPGAVE_ID")
    REFERENCES "OPPGAVE" ("ID") ENABLE;

CREATE VIEW "TILBAKEKREVING_OPPGAVE" AS
SELECT
    o.ID,
    o.BEHANDLING_ID,
    o.FAGSAK_SAKSNR,
    o.AKTOR_ID,
    o.BEHANDLENDE_ENHET,
    o.AKTIV,
    o.BEHANDLING_TYPE,
    o.FAGSAK_YTELSE_TYPE,
    o.SYSTEM,
    o.BEHANDLINGSFRIST,
    o.BEHANDLING_OPPRETTET,
    o.VERSJON,
    o.OPPRETTET_AV,
    o.OPPRETTET_TID,
    o.ENDRET_AV,
    o.ENDRET_TID,
    o.FORSTE_STONADSDAG,
    o.BEHANDLING_STATUS,
    o.UTFORT_FRA_ADMIN,
    o.OPPGAVE_AVSLUTTET,
    o.EKSTERN_ID,
    toe.BELOP,
    toe.FEILUTBETALINGSTART
FROM OPPGAVE o INNER JOIN TILBAKEKREVING_EGENSKAPER toe
                          ON o.id = toe.OPPGAVE_ID;

COMMENT ON MATERIALIZED VIEW "TILBAKEKREVING_OPPGAVE"  IS 'View som inneholder informasjon om behandlinger for tilbakebetaling som trenger intervensjon av en saksbehandler';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."ID" IS 'PK';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."BEHANDLING_ID" IS 'ID for behandlingeng';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."FAGSAK_SAKSNR" IS 'Referansenr for fagsak';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."AKTOR_ID" IS 'Aktør id';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."BEHANDLENDE_ENHET" IS 'Enheten som har oppgaven satt til seg';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."AKTIV" IS 'Setter oppgaveegenskap til aktiv eller inaktiv etter egenskapen sin tilstand';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."BEHANDLING_TYPE" IS 'Hva slags behandlingstype behandlingen har';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."FAGSAK_YTELSE_TYPE" IS 'Hva slags ytelse type fagsaken har';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."SYSTEM" IS 'Hvilket system oppgaven kommer fra';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."BEHANDLINGSFRIST" IS 'Behandlingsfrist';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."BEHANDLING_OPPRETTET" IS 'Tidspunktet behandlingen ble opprettet';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."FORSTE_STONADSDAG" IS 'Første stønadsdag';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."BEHANDLING_STATUS" IS 'Status for behandlingen';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."UTFORT_FRA_ADMIN" IS 'Settes til true når Oppgave er endret fra admin-tjeneste';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."OPPGAVE_AVSLUTTET" IS 'Tidspunkt for avslutting av Oppgave';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."EKSTERN_ID" IS 'Ekstern ID som identifiserer behandling i fagsystem.';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."BELOP" IS 'Beløpet som henvendelsen gjelder';
COMMENT ON COLUMN "TILBAKEKREVING_OPPGAVE"."FEILUTBETALINGSTART" IS 'Dato for første feilutbetaling';
