package no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class OrganisasjonRessursEnhetConsumerProducerDelegator {
    private OrganisasjonRessursEnhetConsumerProducer producer;

    @Inject
    public OrganisasjonRessursEnhetConsumerProducerDelegator(OrganisasjonRessursEnhetConsumerProducer producer) {
        this.producer = producer;
    }

    @Produces
    public OrganisasjonRessursEnhetConsumer organisasjonRessursEnhetConsumerForEndUser() {
        return producer.organisasjonRessursEnhetConsumer();
    }

    @Produces
    public OrganisasjonRessursEnhetSelftestConsumer organisasjonRessursEnhetSelftestConsumerForSystemUser() {
        return producer.organisasjonRessursEnhetSelftestConsumer();
    }
}
