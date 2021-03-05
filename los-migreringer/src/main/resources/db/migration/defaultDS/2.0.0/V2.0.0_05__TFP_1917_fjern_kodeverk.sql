ALTER TABLE "OPPGAVE" DROP CONSTRAINT "FK_OPPGAVE_1";
ALTER TABLE "OPPGAVE" DROP CONSTRAINT "FK_OPPGAVE_2";
ALTER TABLE "FILTRERING_YTELSE_TYPE" DROP CONSTRAINT "FK_FILTRERING_YTELSE_TYPE_1";
ALTER TABLE "FILTRERING_ANDRE_KRITERIER" DROP CONSTRAINT "FK_FILTRERING_ANDRE_KRIT_1";
ALTER TABLE "OPPGAVE_EGENSKAP" DROP CONSTRAINT "FK_OPPGAVE_EGENSKAP_1";
ALTER TABLE "OPPGAVE_EVENT_LOGG" DROP CONSTRAINT "FK_OPPGAVE_EVENT_LOGG_2";
ALTER TABLE "OPPGAVE_EVENT_LOGG" DROP CONSTRAINT "FK_OPPGAVE_EVENT_LOGG_1";

ALTER TABLE "OPPGAVE" DROP COLUMN "KL_BEHANDLING_TYPE";
ALTER TABLE "OPPGAVE" DROP COLUMN "KL_FAGSAK_YTELSE_TYPE";
ALTER TABLE "FILTRERING_YTELSE_TYPE" DROP COLUMN "KL_FAGSAK_YTELSE_TYPE";
ALTER TABLE "FILTRERING_ANDRE_KRITERIER" DROP COLUMN "KL_ANDRE_KRITERIER_TYPE";
ALTER TABLE "OPPGAVE_EGENSKAP" DROP COLUMN "KL_ANDRE_KRITERIER_TYPE";
ALTER TABLE "OPPGAVE_EVENT_LOGG" DROP COLUMN "KL_ANDRE_KRITERIER_TYPE";
ALTER TABLE "OPPGAVE_EVENT_LOGG" DROP COLUMN KL_EVENT_TYPE;
ALTER TABLE "OPPGAVE_FILTRERING" DROP COLUMN KL_SORTERING;