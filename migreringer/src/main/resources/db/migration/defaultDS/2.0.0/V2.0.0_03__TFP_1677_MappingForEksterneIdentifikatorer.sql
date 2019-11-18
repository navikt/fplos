
ALTER TABLE "OPPGAVE" MODIFY (
    EKSTERN_ID RAW(16)
);

COMMENT ON COLUMN "OPPGAVE"."EKSTERN_ID" IS 'Ekstern ID som identifiserer behandling i fagsystem.';

ALTER TABLE "OPPGAVE_EVENT_LOGG" ADD "EKSTERN_ID" RAW(16);

COMMENT ON COLUMN "OPPGAVE_EVENT_LOGG"."EKSTERN_ID" IS 'Ekstern ID som identifiserer behandling i fagsystem.';


