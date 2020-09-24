package no.nav.foreldrepenger.los.web.app.selftest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.web.app.selftest.checks.DatabaseHealthCheck;
import no.nav.fplos.kafkatjenester.KafkaConsumer;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class Selftests {

    private DatabaseHealthCheck databaseHealthCheck;
    private final List<KafkaConsumer<?>> kafkaList = new ArrayList<>();

    private boolean isDatabaseReady;
    private LocalDateTime sistOppdatertTid = LocalDateTime.now().minusDays(1);

    private String applicationName;
    private SelftestResultat selftestResultat;

    @Inject
    public Selftests(DatabaseHealthCheck databaseHealthCheck,
                     @Any Instance<KafkaConsumer<?>> kafkaIntegrations,
                     @KonfigVerdi(value = "application.name") String applicationName) {
        this.databaseHealthCheck = databaseHealthCheck;
        kafkaIntegrations.forEach(this.kafkaList::add);
        this.applicationName = applicationName;
    }

    Selftests() {
        // for CDI proxy
    }

    public SelftestResultat run() {
        oppdaterSelftestResultatHvisNødvendig();
        return selftestResultat; // NOSONAR
    }

    public boolean isReady() {
        // Bruk denne for NAIS-respons og skill omfanget her.
        oppdaterSelftestResultatHvisNødvendig();
        return isDatabaseReady; // NOSONAR
    }

    public boolean isKafkaAlive() {
        return kafkaList.stream().allMatch(KafkaConsumer::isRunning);
    }

    private synchronized void oppdaterSelftestResultatHvisNødvendig() {
        if (sistOppdatertTid.isBefore(LocalDateTime.now().minusSeconds(30))) {
            isDatabaseReady = databaseHealthCheck.isOK();
            selftestResultat = innhentSelftestResultat();
            sistOppdatertTid = LocalDateTime.now();
        }
    }

    private SelftestResultat innhentSelftestResultat() {
        SelftestResultat samletResultat = new SelftestResultat();
        samletResultat.setApplication(applicationName);
        samletResultat.setTimestamp(LocalDateTime.now());

        samletResultat.leggTilResultatForKritiskTjeneste(isDatabaseReady, databaseHealthCheck.getDescription(), databaseHealthCheck.getEndpoint());

        return samletResultat;
    }

}
