ALTER TABLE "OPPGAVE" ADD HREF VARCHAR2(500 CHAR);

COMMENT ON COLUMN "OPPGAVE"."HREF" IS 'URL som benyttes til eksternt system';

