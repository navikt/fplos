package no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient;

import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.OrganisasjonRessursEnhetV1;

class OrganisasjonRessursEnhetSelftestConsumerImpl implements OrganisasjonRessursEnhetSelftestConsumer {
    private OrganisasjonRessursEnhetV1 port;
    private String endpointUrl;

    public OrganisasjonRessursEnhetSelftestConsumerImpl(OrganisasjonRessursEnhetV1 port, String endpointUrl) {
        this.port = port;
        this.endpointUrl = endpointUrl;
    }

    @Override
    public void ping() {
        port.ping();
    }

    @Override
    public String getEndpointUrl() {
        return endpointUrl;
    }
}
