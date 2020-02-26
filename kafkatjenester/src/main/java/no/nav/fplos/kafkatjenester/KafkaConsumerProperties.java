package no.nav.fplos.kafkatjenester;

public interface KafkaConsumerProperties {

    String getTopic();

    String getBootstrapServers();

    String getGroupId();

    String getUsername();

    String getPassword();
}
