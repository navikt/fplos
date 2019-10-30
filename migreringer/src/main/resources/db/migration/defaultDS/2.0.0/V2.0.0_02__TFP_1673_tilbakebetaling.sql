UPDATE "OPPGAVE" SET SYSTEM = 'FPSAK' where SYSTEM is null;

ALTER TABLE "OPPGAVE" ADD EKSTERN_ID VARCHAR(36 CHAR);

COMMENT ON COLUMN "OPPGAVE"."EKSTERN_ID" IS 'Ekstern ID som identifiserer behandling i fagsystem. Benyttes til oppslag.';
