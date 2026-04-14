package no.nav.foreldrepenger.los.migrering.fss;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.los.migrering.dto.BulkDataWrapper;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@Dependent
@ProsessTask(value = "vedlikehold.migrerfssgcp", maxFailedRuns = 1)
public class FssGcpMigrasjonTask implements ProsessTaskHandler {

    public static final String STEG = "CURRENT_MIGRASJONSTEG";

    private static final Logger LOG = LoggerFactory.getLogger(FssGcpMigrasjonTask.class);

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
        if (Environment.current().isGcp()) {
            throw new IllegalStateException("MIGRERING: FSS-task startet i GCP!");
        }

        var currentSteg = Optional.ofNullable(prosessTaskData.getPropertyValue(STEG))
            .map(MigreringSteg::valueOf)
            .orElse(MigreringSteg.DEL1_ORGANISASJON_OG_KØ);
        int batchSize = 100;

        LOG.info("MIGRERING (FSS): steg {} starter", currentSteg);

        int startPosisjon = 0;

        // Gjør dette enkelt med én taskkjøring per relaterte entiteter
        while (true) {
            var bulkData = currentSteg.hent(fssEksportRepository, startPosisjon, batchSize);
            gcpLosKlient.lagreBulkData(bulkData);

            var antallHentet = currentSteg.hentetAntall(bulkData);
            logg(currentSteg, startPosisjon, antallHentet, batchSize);

            if (currentSteg.erFerdig(antallHentet, batchSize)) {
                lagNesteSteg(currentSteg);
                break;
            } else {
                startPosisjon += antallHentet;
            }
        }
    }

    private void logg(MigreringSteg currentSteg, int startPosisjon, int antallHentet, int batchSize) {
        LOG.info("MIGRERING (FSS): steg {}, startPosisjon {}, antallHentet {}, batchSize {}",
            currentSteg, startPosisjon, antallHentet, batchSize);
    }

    private void lagNesteSteg(MigreringSteg currentSteg) {
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
        DEL2_AKTIVE_OPPGAVER,
        DEL3_INAKTIVE_OPPGAVER,
        DEL4_BEHANDLINGER,
        DEL5_STATISTIKK_OF,
        DEL6_STATISTIKK_EYB,
        DEL7_FERDIG;

        BulkDataWrapper hent(FssEksportRepository repo, int currentAntall, int batchSize) {
            return switch (this) {
                case DEL1_ORGANISASJON_OG_KØ -> repo.hentOrganisasjonOgKøer();
                case DEL2_AKTIVE_OPPGAVER -> repo.hentAktiveOppgaverOgReservasjoner(currentAntall, batchSize);
                case DEL3_INAKTIVE_OPPGAVER -> repo.hentInaktiveOppgaverOgReservasjoner(currentAntall, batchSize);
                case DEL4_BEHANDLINGER -> repo.hentBehandlinger(currentAntall, batchSize);
                case DEL5_STATISTIKK_OF -> repo.hentStatistikkOppgaveFilter(currentAntall, batchSize);
                case DEL6_STATISTIKK_EYB -> repo.hentStatistikkEnhetYtelseBehandling(currentAntall, batchSize);
                case DEL7_FERDIG -> throw new IllegalStateException("MIGRERING (FSS): Kalt hent() i ferdig tilstand");
            };
        }

        boolean erFerdig(int hentetAntall, int batchSize) {
            return switch (this) {
                case DEL1_ORGANISASJON_OG_KØ -> true;
                case DEL2_AKTIVE_OPPGAVER, DEL3_INAKTIVE_OPPGAVER, DEL4_BEHANDLINGER, DEL5_STATISTIKK_OF, DEL6_STATISTIKK_EYB -> hentetAntall < batchSize;
                case DEL7_FERDIG -> throw new IllegalStateException("MIGRERING (FSS): Kalt erFerdig() i ferdig tilstand");
            };
        }

        MigreringSteg neste() {
            return switch (this) {
                case DEL1_ORGANISASJON_OG_KØ -> DEL2_AKTIVE_OPPGAVER;
                case DEL2_AKTIVE_OPPGAVER -> DEL3_INAKTIVE_OPPGAVER;
                case DEL3_INAKTIVE_OPPGAVER -> DEL4_BEHANDLINGER;
                case DEL4_BEHANDLINGER -> DEL5_STATISTIKK_OF;
                case DEL5_STATISTIKK_OF -> DEL6_STATISTIKK_EYB;
                case DEL6_STATISTIKK_EYB, DEL7_FERDIG -> DEL7_FERDIG;
            };
        }

        int hentetAntall(BulkDataWrapper bulkData) {
            return switch (this) {
                case DEL1_ORGANISASJON_OG_KØ -> 1;
                case DEL2_AKTIVE_OPPGAVER -> bulkData.aktiveOppgaver().size();
                case DEL3_INAKTIVE_OPPGAVER -> bulkData.inaktiveOppgaver().size();
                case DEL4_BEHANDLINGER -> bulkData.behandlinger().size();
                case DEL5_STATISTIKK_OF -> bulkData.statistikkOppgaveFilter().size();
                case DEL6_STATISTIKK_EYB -> bulkData.statistikkEnhetYtelseBehandling().size();
                case DEL7_FERDIG -> 0;
            };
        }
    }
}
