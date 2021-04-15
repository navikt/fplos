package no.nav.foreldrepenger.los.web.app.selftest;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.web.app.selftest.checks.DatabaseHealthCheck;
import no.nav.foreldrepenger.los.web.app.tjenester.KafkaConsumerStarter;

@ApplicationScoped
public class Selftests {
    private DatabaseHealthCheck databaseHealthCheck;
    private KafkaConsumerStarter kafkaConsumerStarter;

    private boolean isReady;
    private LocalDateTime sistOppdatertTid = LocalDateTime.now().minusDays(1);

    @Inject
    public Selftests(DatabaseHealthCheck databaseHealthCheck,
                     KafkaConsumerStarter kafkaConsumerStarter) {
        this.databaseHealthCheck = databaseHealthCheck;
        this.kafkaConsumerStarter = kafkaConsumerStarter;
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
        return run().ready();
    }

    public boolean isKafkaAlive() {
        return kafkaConsumerStarter.isKafkaAlive();
    }

    private synchronized void oppdaterSelftestResultatHvisNødvendig() {
        if (sistOppdatertTid.isBefore(LocalDateTime.now().minusSeconds(30))) {
            isReady = databaseHealthCheck.isOK();
            sistOppdatertTid = LocalDateTime.now();
        }
    }

    public record Resultat(boolean ready, String description, String endpoint) {
    }
}
