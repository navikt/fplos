package no.nav.foreldrepenger.web.app.tjenester.fagsak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerFagsakKlient;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.FagsakRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.dto.SokefeltDto;

public class FagsakRestTjenesteTest {

    private FagsakRestTjeneste tjeneste;

    @BeforeEach
    public void oppsett() {
        var klient = mock(ForeldrepengerFagsakKlient.class);
        var applikasjonTjeneste = new FagsakApplikasjonTjeneste(klient, null);
        tjeneste = new FagsakRestTjeneste(applikasjonTjeneste);
    }

    @Test
    public void skal_returnere_tom_liste_dersom_tomt_view() {
        var fagsakDtos = tjeneste.søkFagsaker(new SokefeltDto("ugyldig_søkestreng"));
        assertThat(fagsakDtos).hasSize(0);
    }
}
