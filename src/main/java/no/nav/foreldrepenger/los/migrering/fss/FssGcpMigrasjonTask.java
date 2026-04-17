package no.nav.foreldrepenger.los.migrering.fss;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.migrering.dto.BulkDataWrapper;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@Dependent
@ProsessTask(value = "vedlikehold.migrerfssgcp", maxFailedRuns = 1)
public class FssGcpMigrasjonTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FssGcpMigrasjonTask.class);

    private static final int ITERASJON_BULK_STØRRELSE = 100;
    private static final int PROSESSTASK_MAKS_ANTALL = 2_000;

    public static final String START_POSISJON = "START_POSISJON";
    public static final String STEG = "CURRENT_MIGRASJONSTEG";


    private final FssEksportRepository fssEksportRepository;
    private final GcpLosKlient gcpLosKlient;
    private final ProsessTaskTjeneste prosessTaskTjeneste;


    @Inject
    public FssGcpMigrasjonTask(FssEksportRepository fssEksportRepository, GcpLosKlient gcpLosKlient, ProsessTaskTjeneste prosessTaskTjeneste) {
        this.fssEksportRepository = fssEksportRepository;
        this.gcpLosKlient = gcpLosKlient;
        this.prosessTaskTjeneste = prosessTaskTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var currentMigreringSteg = Optional.ofNullable(prosessTaskData.getPropertyValue(STEG))
            .map(MigreringSteg::valueOf)
            .orElse(MigreringSteg.DEL1_ORGANISASJON_OG_KØ);

        int startPosisjon = Optional.ofNullable(prosessTaskData.getPropertyValue(START_POSISJON))
            .map(Integer::parseInt)
            .orElse(0);
        int antallHentetDenneTasken = 0;

        LOG.info("MIGRERING (FSS): steg {} starter fra startPosisjon {}", currentMigreringSteg, startPosisjon);

        while (true) {
            var bulkData = currentMigreringSteg.hent(fssEksportRepository, startPosisjon, ITERASJON_BULK_STØRRELSE);

            gcpLosKlient.lagreBulkData(bulkData);

            int antallHentetIterasjon = currentMigreringSteg.hentetAntall(bulkData);
            startPosisjon += antallHentetIterasjon;
            antallHentetDenneTasken += antallHentetIterasjon;

            logg(currentMigreringSteg, startPosisjon, antallHentetDenneTasken);

            if (currentMigreringSteg.erFerdig(antallHentetIterasjon, ITERASJON_BULK_STØRRELSE)) {
                lagTaskNesteSteg(currentMigreringSteg);
                break;
            }

            if (antallHentetDenneTasken >= PROSESSTASK_MAKS_ANTALL) {
                lagTaskFortsetterSammeSteg(currentMigreringSteg, startPosisjon);
                break;
            }
        }
    }

    private void logg(MigreringSteg currentSteg, int startPosisjon, int antallHentetDenneTasken) {
        LOG.info("MIGRERING (FSS): steg {}, neste startPosisjon {}, antallHentetDenneTasken {}", currentSteg, startPosisjon, antallHentetDenneTasken);
    }

    private void lagTaskFortsetterSammeSteg(MigreringSteg currentSteg, int startPosisjon) {
        LOG.info("MIGRERING (FSS): steg {} ikke ferdig, lagrer task for samme steg med startPosisjon {}", currentSteg, startPosisjon);
        var t = ProsessTaskData.forProsessTask(FssGcpMigrasjonTask.class);
        t.setProperty(STEG, currentSteg.name());
        t.setProperty(START_POSISJON, String.valueOf(startPosisjon));
        prosessTaskTjeneste.lagre(t);
    }

    private void lagTaskNesteSteg(MigreringSteg currentSteg) {
        var nesteSteg = currentSteg.neste();
        LOG.info("MIGRERING (FSS): steg {} ferdig, neste steg {}", currentSteg, nesteSteg);
        if (nesteSteg != MigreringSteg.DEL7_FERDIG) {
            var t = ProsessTaskData.forProsessTask(FssGcpMigrasjonTask.class);
            t.setProperty(STEG, nesteSteg.name());
            prosessTaskTjeneste.lagre(t);
        }
    }

    public enum MigreringSteg {
        DEL1_ORGANISASJON_OG_KØ,
        DEL2_BEHANDLINGER,       // Må komme før oppgaver pga. FK-constraint i GCP (oppgave.behandling_id → behandling.id)
        DEL3_AKTIVE_OPPGAVER,
        DEL4_INAKTIVE_OPPGAVER,
        DEL5_STATISTIKK_OF,
        DEL6_STATISTIKK_EYB,
        DEL7_FERDIG;

        BulkDataWrapper hent(FssEksportRepository repo, int currentAntall, int batchSize) {
            return switch (this) {
                case DEL1_ORGANISASJON_OG_KØ -> repo.hentOrganisasjonOgKøer();
                case DEL2_BEHANDLINGER -> repo.hentBehandlinger(currentAntall, batchSize);
                case DEL3_AKTIVE_OPPGAVER -> repo.hentAktiveOppgaverOgReservasjoner(currentAntall, batchSize);
                case DEL4_INAKTIVE_OPPGAVER -> repo.hentInaktiveOppgaverOgReservasjoner(currentAntall, batchSize);
                case DEL5_STATISTIKK_OF -> repo.hentStatistikkOppgaveFilter(currentAntall, batchSize);
                case DEL6_STATISTIKK_EYB -> repo.hentStatistikkEnhetYtelseBehandling(currentAntall, batchSize);
                case DEL7_FERDIG -> throw new IllegalStateException("MIGRERING (FSS): Kalt hent() i ferdig tilstand");
            };
        }

        boolean erFerdig(int hentetAntall, int batchSize) {
            return switch (this) {
                case DEL1_ORGANISASJON_OG_KØ -> true;
                case DEL2_BEHANDLINGER, DEL3_AKTIVE_OPPGAVER, DEL4_INAKTIVE_OPPGAVER, DEL5_STATISTIKK_OF, DEL6_STATISTIKK_EYB -> hentetAntall < batchSize;
                case DEL7_FERDIG -> throw new IllegalStateException("MIGRERING (FSS): Kalt erFerdig() i ferdig tilstand");
            };
        }

        MigreringSteg neste() {
            return switch (this) {
                case DEL1_ORGANISASJON_OG_KØ -> DEL2_BEHANDLINGER;
                case DEL2_BEHANDLINGER -> DEL3_AKTIVE_OPPGAVER;
                case DEL3_AKTIVE_OPPGAVER -> DEL4_INAKTIVE_OPPGAVER;
                case DEL4_INAKTIVE_OPPGAVER -> DEL5_STATISTIKK_OF;
                case DEL5_STATISTIKK_OF -> DEL6_STATISTIKK_EYB;
                case DEL6_STATISTIKK_EYB, DEL7_FERDIG -> DEL7_FERDIG;
            };
        }

        int hentetAntall(BulkDataWrapper bulkData) {
            return switch (this) {
                case DEL1_ORGANISASJON_OG_KØ -> 1;
                case DEL2_BEHANDLINGER -> bulkData.behandlinger().size();
                case DEL3_AKTIVE_OPPGAVER -> bulkData.aktiveOppgaver().size();
                case DEL4_INAKTIVE_OPPGAVER -> bulkData.inaktiveOppgaver().size();
                case DEL5_STATISTIKK_OF -> bulkData.statistikkOppgaveFilter().size();
                case DEL6_STATISTIKK_EYB -> bulkData.statistikkEnhetYtelseBehandling().size();
                case DEL7_FERDIG -> 0;
            };
        }
    }
}
