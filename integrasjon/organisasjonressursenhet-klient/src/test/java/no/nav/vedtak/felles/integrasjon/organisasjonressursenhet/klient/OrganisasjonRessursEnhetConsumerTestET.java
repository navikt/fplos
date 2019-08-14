package no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient;

import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.OrganisasjonRessursEnhetV1;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeRequest;
import no.nav.vedtak.exception.IntegrasjonException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrganisasjonRessursEnhetConsumerTestET {

    OrganisasjonRessursEnhetConsumer consumer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    OrganisasjonRessursEnhetV1 mockWebservice = mock(OrganisasjonRessursEnhetV1.class);

    @Before
    public void setUp() throws Exception {
        consumer = new OrganisasjonRessursEnhetConsumerImpl(mockWebservice);
    }

    @Test
    public void skalFangeSoapFaulOgKasteFeilmelding() throws Exception {
        when(mockWebservice.hentEnhetListe(any(WSHentEnhetListeRequest.class))).thenThrow(opprettSOAPFaultException());

        expectedException.expect(IntegrasjonException.class);
        expectedException.expectMessage("FP-942048");

        consumer.hentEnhetListe(mock(WSHentEnhetListeRequest.class));
    }

    private SOAPFaultException opprettSOAPFaultException() throws SOAPException {
        SOAPFault fault = SOAPFactory.newInstance().createFault();
        fault.setFaultString("testing");
        fault.setFaultCode(new QName("local"));
        return new SOAPFaultException(fault);
    }
}