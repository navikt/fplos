package no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient;

import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeRessursIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.OrganisasjonRessursEnhetV1;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeResponse;
import no.nav.vedtak.felles.integrasjon.felles.ws.SoapWebServiceFeil;

import javax.xml.ws.soap.SOAPFaultException;

public class OrganisasjonRessursEnhetConsumerImpl implements OrganisasjonRessursEnhetConsumer {

    public static final String SERVICE_IDENTIFIER = "OrganisasjonRessursEnhetV1";

    private OrganisasjonRessursEnhetV1 port;

    public OrganisasjonRessursEnhetConsumerImpl(OrganisasjonRessursEnhetV1 port) {
        this.port = port;
    }

    @Override
    public WSHentEnhetListeResponse hentEnhetListe(WSHentEnhetListeRequest request)
            throws HentEnhetListeUgyldigInput, HentEnhetListeRessursIkkeFunnet {
        try {
            return port.hentEnhetListe(request);
        } catch (SOAPFaultException e) { // NOSONAR
            throw SoapWebServiceFeil.FACTORY.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e).toException();
        }
    }
}
