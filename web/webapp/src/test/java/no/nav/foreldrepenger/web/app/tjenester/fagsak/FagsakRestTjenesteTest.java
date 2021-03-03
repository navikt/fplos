package no.nav.foreldrepenger.web.app.tjenester.fagsak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.FagsakRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.dto.SokefeltDto;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerFagsakKlient;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakMedPersonDto;

public class FagsakRestTjenesteTest {

    private FagsakRestTjeneste tjeneste;
    private FagsakApplikasjonTjeneste applikasjonTjeneste;
    private ForeldrepengerFagsakKlient klient;

    @BeforeEach
    public void oppsett() {
        klient = mock(ForeldrepengerFagsakKlient.class);
        applikasjonTjeneste = new FagsakApplikasjonTjeneste(klient, null);
        tjeneste = new FagsakRestTjeneste(applikasjonTjeneste);
    }

    @Test
    public void skal_returnere_tom_liste_dersom_tomt_view() {
        Collection<FagsakMedPersonDto> fagsakDtos = tjeneste.søkFagsaker(new SokefeltDto("ugyldig_søkestreng"));
        assertThat(fagsakDtos).hasSize(0);
    }
}
