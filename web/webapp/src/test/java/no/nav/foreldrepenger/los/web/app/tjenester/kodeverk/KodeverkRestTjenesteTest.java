package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.oppgave.FagsakStatus;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.web.app.jackson.JacksonJsonConfig;


public class KodeverkRestTjenesteTest {

    private HentKodeverkTjeneste hentKodeverkTjeneste;

    @BeforeEach
    public void before() {
        hentKodeverkTjeneste = new HentKodeverkTjeneste();
    }

    @Test
    public void skal_hente_kodeverk_og_gruppere_på_kodeverknavn() throws IOException {

        var tjeneste = new KodeverkRestTjeneste(hentKodeverkTjeneste);
        var response = tjeneste.hentGruppertKodeliste();

        var rawJson = (String) response.getEntity();
        assertThat(rawJson).isNotNull();

        Map<String, Object> gruppertKodeliste = new JacksonJsonConfig().getObjectMapper().readValue(rawJson, Map.class);

        assertThat(gruppertKodeliste.keySet())
                .contains(FagsakStatus.class.getSimpleName(), KøSortering.class.getSimpleName());
        //System.out.println(gruppertKodeliste);
    }

    @Test
    public void serialize_kodeverdi_uttak() throws Exception {

        var jsonConfig = new JacksonJsonConfig();

        var om = jsonConfig.getObjectMapper();

        var json = om.writer().withDefaultPrettyPrinter().writeValueAsString(new X(FagsakStatus.AVSLUTTET));

        assertThat(json).contains("\"fagsakStatus\" : \"AVSLU\"");
    }

    @Test
    public void serialize_kodeverdi_uttak_full() throws Exception {

        var jsonConfig = new JacksonJsonConfig(true);

        var om = jsonConfig.getObjectMapper();

        var json = om.writer().withDefaultPrettyPrinter().writeValueAsString(new X(FagsakStatus.OPPRETTET));

        assertThat(json).contains("\"kode\" : \"OPPR\"");
        assertThat(json).contains("\"navn\" : \"Opprettet\"");
    }

    private static record X(FagsakStatus fagsakStatus) {}

}
