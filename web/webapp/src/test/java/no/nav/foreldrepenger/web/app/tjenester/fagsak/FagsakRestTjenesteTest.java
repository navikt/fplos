package no.nav.foreldrepenger.web.app.tjenester.fagsak;

import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.FagsakRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.dto.SokefeltDto;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.person.api.TpsTjeneste;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("deprecation")
public class FagsakRestTjenesteTest {

    private FagsakRestTjeneste tjeneste;

    private FagsakApplikasjonTjeneste applikasjonTjeneste;
    private ForeldrepengerBehandlingRestKlient klient;
    private TpsTjeneste tpsTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;
    private AnsattTjeneste ansattTjeneste;

    @Before
    public void oppsett() {
        tpsTjeneste = mock(TpsTjeneste.class);
        klient = mock(ForeldrepengerBehandlingRestKlient.class);
        oppgaveTjeneste = mock(OppgaveTjeneste.class);
        ansattTjeneste = mock(AnsattTjeneste.class);
        applikasjonTjeneste = new FagsakApplikasjonTjeneste(tpsTjeneste, klient);
        tjeneste = new FagsakRestTjeneste(applikasjonTjeneste);
    }

    @Test
    public void skal_returnere_tom_liste_dersom_tomt_view() {
        Collection<FagsakDto> fagsakDtos = tjeneste.søkFagsaker(new SokefeltDto("ugyldig_søkestreng"));
        assertThat(fagsakDtos).hasSize(0);
    }
}
