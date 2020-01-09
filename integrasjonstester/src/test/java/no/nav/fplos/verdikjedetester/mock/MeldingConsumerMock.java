package no.nav.fplos.verdikjedetester.mock;

import no.nav.fplos.kafkatjenester.AksjonspunktMeldingConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Alternative
@Priority(1)
@Dependent
public class MeldingConsumerMock implements AksjonspunktMeldingConsumer {

    public static final String TOPIC = "topic";
    public static final int PARTITION = 0;
    public static final TopicPartition topicPartition  = new TopicPartition(TOPIC,PARTITION);

    @Override
    public ConsumerRecords<String, String> hentConsumerMeldingene() {
        List<ConsumerRecord<String, String>> topicPartitionEntries = new ArrayList<ConsumerRecord<String, String>>();
        ConsumerRecords<String, String> response;
        if (!MockEventKafkaMessages.eventer.isEmpty()) {
            for (String event : MockEventKafkaMessages.eventer){
                topicPartitionEntries.add(new ConsumerRecord<String, String>(TOPIC, PARTITION, topicPartitionEntries.size(), "event", event));
            }
        } else {
            for (String msg : MockKafkaMessages.messages) {
                topicPartitionEntries.add(new ConsumerRecord<String, String>(TOPIC, PARTITION, topicPartitionEntries.size(), "message", msg));
            }
        }
        Map<TopicPartition, List<ConsumerRecord<String, String>>> msgRecordslist = new LinkedHashMap<>();
        msgRecordslist.put(topicPartition, topicPartitionEntries);
        response = new ConsumerRecords<>(msgRecordslist);
        return response;
    }

    @Override
    public void manualCommitSync() {

    }
}
