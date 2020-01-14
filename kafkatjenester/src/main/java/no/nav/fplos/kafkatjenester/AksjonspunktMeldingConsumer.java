package no.nav.fplos.kafkatjenester;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface AksjonspunktMeldingConsumer {
    ConsumerRecords<String, String> hentConsumerMeldingene();

    void manualCommitSync();
}
