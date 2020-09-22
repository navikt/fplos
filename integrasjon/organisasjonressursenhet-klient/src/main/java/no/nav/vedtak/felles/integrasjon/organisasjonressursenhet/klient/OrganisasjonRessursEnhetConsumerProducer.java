package no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.OrganisasjonRessursEnhetV1;
import no.nav.vedtak.sts.client.StsClientType;
import no.nav.vedtak.sts.client.StsConfigurationUtil;

@Dependent
public class OrganisasjonRessursEnhetConsumerProducer {
    private OrganisasjonRessursEnhetConsumerConfig consumerConfig;

    @Inject
    public void setConfig(OrganisasjonRessursEnhetConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public OrganisasjonRessursEnhetConsumer organisasjonRessursEnhetConsumer() {
        OrganisasjonRessursEnhetV1 port = wrapWithSts(consumerConfig.getPort(), StsClientType.SECURITYCONTEXT_TIL_SAML);
        return new OrganisasjonRessursEnhetConsumerImpl(port);
    }

    public OrganisasjonRessursEnhetSelftestConsumer organisasjonRessursEnhetSelftestConsumer() {
        OrganisasjonRessursEnhetV1 port = wrapWithSts(consumerConfig.getPort(), StsClientType.SYSTEM_SAML);
        return new OrganisasjonRessursEnhetSelftestConsumerImpl(port, consumerConfig.getEndpointUrl());
    }

    OrganisasjonRessursEnhetV1 wrapWithSts(OrganisasjonRessursEnhetV1 port, StsClientType stsClientType) {
        return StsConfigurationUtil.wrapWithSts(port, stsClientType);
    }

}
