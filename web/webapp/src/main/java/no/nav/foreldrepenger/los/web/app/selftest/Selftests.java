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

@ApplicationScoped
public class Selftests {

    private DatabaseHealthCheck databaseHealthCheck;
    private final List<KafkaConsumer<?>> kafkaList = new ArrayList<>();

    private boolean isReady;
    private LocalDateTime sistOppdatertTid = LocalDateTime.now().minusDays(1);

    @Inject
    public Selftests(DatabaseHealthCheck databaseHealthCheck,
                     @Any Instance<KafkaConsumer<?>> kafkaIntegrations) {
        this.databaseHealthCheck = databaseHealthCheck;
        kafkaIntegrations.forEach(this.kafkaList::add);
    }

    Selftests() {
        // for CDI proxy
    }

    public Selftests.Resultat run() {
        oppdaterSelftestResultatHvisNødvendig();
        return new Selftests.Resultat(isReady, databaseHealthCheck.getDescription(), databaseHealthCheck.getEndpoint());
    }

    public boolean isReady() {
        // Bruk denne for NAIS-respons og skill omfanget her.
        return run().isReady();
    }

    public boolean isKafkaAlive() {
        return kafkaList.stream().allMatch(KafkaConsumer::isRunning);
    }

    private synchronized void oppdaterSelftestResultatHvisNødvendig() {
        if (sistOppdatertTid.isBefore(LocalDateTime.now().minusSeconds(30))) {
            isReady = databaseHealthCheck.isOK();
            sistOppdatertTid = LocalDateTime.now();
        }
    }

    public static class Resultat {
        private final boolean isReady;
        private final String description;
        private final String endpoint;

        public Resultat(boolean isReady, String description, String endpoint) {
            this.isReady = isReady;
            this.description = description;
            this.endpoint = endpoint;
        }

        public boolean isReady() {
            return isReady;
        }

        public String getDescription() {
            return description;
        }

        public String getEndpoint() {
            return endpoint;
        }
    }
}
