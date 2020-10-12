package no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.impl.OrganisasjonRessursEnhetTjenesteImpl;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.informasjon.WSEnhet;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeResponse;
import no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient.OrganisasjonRessursEnhetConsumer;

public class OrganisasjonRessursEnhetTjenesteImplTest {

    private static final String RESSURS_ID = "1234";
    private static final String RESPONSE_ENHETS_ID = "9999";
    private static final String RESPONSE_ENHETS_NAVN = "NAV Test";
    private static final String RESPONSE_ENHETS_ID_2 = "8888";
    private static final String RESPONSE_ENHETS_NAVN_2 = "NAV Oslo";

    private OrganisasjonRessursEnhetConsumer consumer = mock(OrganisasjonRessursEnhetConsumer.class);
    private EnhetstilgangTjeneste axsysTjeneste = mock(EnhetstilgangTjeneste.class);
    private OrganisasjonRessursEnhetTjeneste tjeneste = new OrganisasjonRessursEnhetTjenesteImpl(consumer, axsysTjeneste);

    @Test
    public void skal_hente_ut_enhetens_id_og_navn_ved_normalt_svar_ved_en_enhet() throws Exception {
        ArgumentCaptor<WSHentEnhetListeRequest> captor = ArgumentCaptor.forClass(WSHentEnhetListeRequest.class);
        when(consumer.hentEnhetListe(captor.capture())).thenReturn(opprettResponseMedEnEnhet());

        List<OrganisasjonsEnhet> organisasjonsEnhet = tjeneste.hentEnhetListe(RESSURS_ID);

        //Verifiser request
        WSHentEnhetListeRequest request = captor.getValue();
        assertThat(request.getRessursId()).isEqualTo(RESSURS_ID);

        //Verifiser svar
        assertThat(organisasjonsEnhet.get(0).getEnhetId()).isEqualTo(RESPONSE_ENHETS_ID);
        assertThat(organisasjonsEnhet.get(0).getEnhetNavn()).isEqualTo(RESPONSE_ENHETS_NAVN);
    }

    @Test
    public void skal_hente_ut_enhetens_id_og_navn_ved_normalt_svar_ved_to_enheter() throws Exception {
        ArgumentCaptor<WSHentEnhetListeRequest> captor = ArgumentCaptor.forClass(WSHentEnhetListeRequest.class);
        when(consumer.hentEnhetListe(captor.capture())).thenReturn(opprettResponseMedToEnhet());

        List<OrganisasjonsEnhet> organisasjonsEnhet = tjeneste.hentEnhetListe(RESSURS_ID);

        //Verifiser request
        WSHentEnhetListeRequest request = captor.getValue();
        assertThat(request.getRessursId()).isEqualTo(RESSURS_ID);

        //Verifiser svar
        assertThat(organisasjonsEnhet.get(0).getEnhetId()).isEqualTo(RESPONSE_ENHETS_ID);
        assertThat(organisasjonsEnhet.get(0).getEnhetNavn()).isEqualTo(RESPONSE_ENHETS_NAVN);
        assertThat(organisasjonsEnhet.get(1).getEnhetId()).isEqualTo(RESPONSE_ENHETS_ID_2);
        assertThat(organisasjonsEnhet.get(1).getEnhetNavn()).isEqualTo(RESPONSE_ENHETS_NAVN_2);
    }

    @Test
    public void skal_returnere_null_dersom_liste_er_tom() throws Exception {
        ArgumentCaptor<WSHentEnhetListeRequest> captor = ArgumentCaptor.forClass(WSHentEnhetListeRequest.class);
        WSHentEnhetListeResponse tomResponse = new WSHentEnhetListeResponse();
        when(consumer.hentEnhetListe(captor.capture())).thenReturn(tomResponse);

        List<OrganisasjonsEnhet> organisasjonsEnhet = tjeneste.hentEnhetListe(RESSURS_ID);

        //Verifiser svar
        assertThat(organisasjonsEnhet).isNullOrEmpty();
    }

    private WSHentEnhetListeResponse opprettResponseMedEnEnhet() {
        WSHentEnhetListeResponse response = new WSHentEnhetListeResponse();
        WSEnhet enhet = new WSEnhet();
        enhet.setEnhetId(RESPONSE_ENHETS_ID);
        enhet.setNavn(RESPONSE_ENHETS_NAVN);

        response.getEnhetListe().add(enhet);
        return response;
    }

    private WSHentEnhetListeResponse opprettResponseMedToEnhet() {
        WSHentEnhetListeResponse response = new WSHentEnhetListeResponse();

        WSEnhet enhet1 = new WSEnhet();
        enhet1.setEnhetId(RESPONSE_ENHETS_ID);
        enhet1.setNavn(RESPONSE_ENHETS_NAVN);

        WSEnhet enhet2 = new WSEnhet();
        enhet2.setEnhetId(RESPONSE_ENHETS_ID_2);
        enhet2.setNavn(RESPONSE_ENHETS_NAVN_2);

        response.getEnhetListe().add(enhet1);
        response.getEnhetListe().add(enhet2);

        return response;
    }
}
