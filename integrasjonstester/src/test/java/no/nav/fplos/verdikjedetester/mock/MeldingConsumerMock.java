package no.nav.fplos.verdikjedetester.mock;

import no.nav.fplos.kafkatjenester.AksjonspunktMeldingConsumer;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import java.util.List;

@Alternative
@Priority(1)
@Dependent
public class MeldingConsumerMock implements AksjonspunktMeldingConsumer {

    @Override
    public List<String> hentConsumerMeldingene() {
        if (!MockEventKafkaMessages.eventer.isEmpty()){
            return MockEventKafkaMessages.eventer;
        }
        return MockKafkaMessages.messages;
    }

    @Override
    public void manualCommitSync() {

    }
}
