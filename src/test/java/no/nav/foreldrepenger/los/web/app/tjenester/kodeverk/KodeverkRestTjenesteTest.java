package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk;

import no.nav.foreldrepenger.los.oppgave.FagsakStatus;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.web.app.jackson.JacksonJsonConfig;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KodeverkRestTjenesteTest {

    @Test
    void skal_hente_kodeverk_og_gruppere_på_kodeverknavn() throws IOException {

        var tjeneste = new KodeverkRestTjeneste();
        var response = tjeneste.hentGruppertKodeliste();

        var rawJson = (String) response.getEntity();
        assertThat(rawJson).isNotNull();

        var gruppertKodeliste = new JacksonJsonConfig().getObjectMapper().readValue(rawJson, Map.class);

        assertThat(gruppertKodeliste).containsKeys(FagsakStatus.class.getSimpleName(), KøSortering.class.getSimpleName());
    }

    @Test
    void serialize_kodeverdi_uttak() throws Exception {

        var jsonConfig = new JacksonJsonConfig();

        var om = jsonConfig.getObjectMapper();

        var json = om.writer().withDefaultPrettyPrinter().writeValueAsString(new X(FagsakStatus.AVSLUTTET));

        assertThat(json).contains("\"fagsakStatus\" : \"AVSLU\"");
    }

    @Test
    void serialize_kodeverdi_uttak_full() throws Exception {

        var jsonConfig = new JacksonJsonConfig(true);

        var om = jsonConfig.getObjectMapper();

        var json = om.writer().withDefaultPrettyPrinter().writeValueAsString(new X(FagsakStatus.OPPRETTET));

        assertThat(json).contains("\"kode\" : \"OPPR\"").contains("\"navn\" : \"Opprettet\"");
    }

    private record X(FagsakStatus fagsakStatus) {
    }

}
