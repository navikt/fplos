package no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient;

import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.OrganisasjonRessursEnhetV1;
import no.nav.vedtak.felles.integrasjon.felles.ws.CallIdOutInterceptor;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.namespace.QName;

@Dependent
public class OrganisasjonRessursEnhetConsumerConfig {
    private static final String ORGANISASJON_RESSURS_ENHET_V_1_WSDL = "wsdl/no/nav/tjeneste/virksomhet/organisasjonRessursEnhet/v1/Binding.wsdl";
    private static final String ORGANISASJON_RESSURS_ENHET_V_1_NAMESPACE = "http://nav.no/tjeneste/virksomhet/organisasjonRessursEnhet/v1/Binding";
    private static final QName ORGANISASJON_RESSURS_ENHET_V_1_SERVICE = new QName(ORGANISASJON_RESSURS_ENHET_V_1_NAMESPACE, "OrganisasjonRessursEnhet_v1");
    private static final QName ORGANISASJON_RESSURS_ENHET_V_1_PORT = new QName(ORGANISASJON_RESSURS_ENHET_V_1_NAMESPACE, "OrganisasjonRessursEnhet_v1Port");

    private String endpointUrl;

    @Inject
    public OrganisasjonRessursEnhetConsumerConfig(@KonfigVerdi("OrganisasjonRessursEnhet_v1.url") String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    OrganisasjonRessursEnhetV1 getPort() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(ORGANISASJON_RESSURS_ENHET_V_1_WSDL);
        factoryBean.setServiceName(ORGANISASJON_RESSURS_ENHET_V_1_SERVICE);
        factoryBean.setEndpointName(ORGANISASJON_RESSURS_ENHET_V_1_PORT);
        factoryBean.setServiceClass(OrganisasjonRessursEnhetV1.class);
        factoryBean.setAddress(endpointUrl);
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new LoggingFeature());
        factoryBean.getOutInterceptors().add(new CallIdOutInterceptor());
        return factoryBean.create(OrganisasjonRessursEnhetV1.class);
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }
}
