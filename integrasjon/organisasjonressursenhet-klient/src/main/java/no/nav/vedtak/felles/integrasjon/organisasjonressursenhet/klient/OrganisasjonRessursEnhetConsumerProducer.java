package no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient;

import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.OrganisasjonRessursEnhetV1;

import no.nav.vedtak.sts.client.NAVSTSClient;
import no.nav.vedtak.sts.client.StsConfigurationUtil;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static no.nav.vedtak.sts.client.NAVSTSClient.StsClientType.SECURITYCONTEXT_TIL_SAML;
import static no.nav.vedtak.sts.client.NAVSTSClient.StsClientType.SYSTEM_SAML;

@Dependent
public class OrganisasjonRessursEnhetConsumerProducer {
    private OrganisasjonRessursEnhetConsumerConfig consumerConfig;

    @Inject
    public void setConfig(OrganisasjonRessursEnhetConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public OrganisasjonRessursEnhetConsumer organisasjonRessursEnhetConsumer() {
        OrganisasjonRessursEnhetV1 port = wrapWithSts(consumerConfig.getPort(), SECURITYCONTEXT_TIL_SAML);
        return new OrganisasjonRessursEnhetConsumerImpl(port);
    }

    public OrganisasjonRessursEnhetSelftestConsumer organisasjonRessursEnhetSelftestConsumer() {
        OrganisasjonRessursEnhetV1 port = wrapWithSts(consumerConfig.getPort(), SYSTEM_SAML);
        return new OrganisasjonRessursEnhetSelftestConsumerImpl(port, consumerConfig.getEndpointUrl());
    }

    OrganisasjonRessursEnhetV1 wrapWithSts(OrganisasjonRessursEnhetV1 port, NAVSTSClient.StsClientType samlTokenType) {
        return StsConfigurationUtil.wrapWithSts(port, samlTokenType);
    }

}
