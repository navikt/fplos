package no.nav.foreldrepenger.web.app.tjenester.fagsak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.FagsakRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.dto.SokefeltDto;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.person.PersonTjeneste;

public class FagsakRestTjenesteTest {

    private FagsakRestTjeneste tjeneste;

    private FagsakApplikasjonTjeneste applikasjonTjeneste;
    private ForeldrepengerBehandlingRestKlient klient;
    private PersonTjeneste personTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;
    private AnsattTjeneste ansattTjeneste;

    @BeforeEach
    public void oppsett() {
        personTjeneste = mock(PersonTjeneste.class);
        klient = mock(ForeldrepengerBehandlingRestKlient.class);
        oppgaveTjeneste = mock(OppgaveTjeneste.class);
        ansattTjeneste = mock(AnsattTjeneste.class);
        applikasjonTjeneste = new FagsakApplikasjonTjeneste(personTjeneste, klient);
        tjeneste = new FagsakRestTjeneste(applikasjonTjeneste);
    }

    @Test
    public void skal_returnere_tom_liste_dersom_tomt_view() {
        Collection<FagsakDto> fagsakDtos = tjeneste.søkFagsaker(new SokefeltDto("ugyldig_søkestreng"));
        assertThat(fagsakDtos).hasSize(0);
    }
}
