package no.nav.foreldrepenger.los.hendelse.hendelseoppretter;

public interface KafkaConsumerProperties {

    String getTopic();

    String getBootstrapServers();

    String getGroupId();

    String getUsername();

    String getPassword();

    String getOffsetResetPolicy();
}
