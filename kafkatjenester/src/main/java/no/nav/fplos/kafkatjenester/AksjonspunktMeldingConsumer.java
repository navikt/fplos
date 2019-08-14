package no.nav.fplos.kafkatjenester;

import java.util.List;

public interface AksjonspunktMeldingConsumer {
    List<String> hentConsumerMeldingene();

    void manualCommitSync();
}
