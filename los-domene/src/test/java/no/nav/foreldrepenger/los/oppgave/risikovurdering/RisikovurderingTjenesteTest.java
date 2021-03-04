package no.nav.foreldrepenger.los.oppgave.risikovurdering;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.oppgaveegenskap.OppgaveEgenskapTjeneste;
import no.nav.foreldrepenger.los.oppgave.risikovurdering.modell.Kontrollresultat;
import no.nav.foreldrepenger.los.oppgave.risikovurdering.modell.KontrollresultatWrapper;
import no.nav.foreldrepenger.los.oppgave.risikovurdering.modell.RisikoklassifiseringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class RisikovurderingTjenesteTest {

    private final RisikoklassifiseringRepository risikoklassifiseringRepository = mock(RisikoklassifiseringRepository.class);
    private final OppgaveTjeneste oppgaveTjeneste = mock(OppgaveTjeneste.class);
    private final OppgaveEgenskapTjeneste oppgaveEgenskapTjeneste = mock(OppgaveEgenskapTjeneste.class);
    private RisikovurderingTjeneste risikovurderingTjeneste;

    @BeforeEach
    public void setup() {
        risikovurderingTjeneste = new RisikovurderingTjeneste(risikoklassifiseringRepository, oppgaveTjeneste, oppgaveEgenskapTjeneste);
    }

    @Test
    public void skal_lagre_risikovurdering_på_behandlingId() {
        var behandlingId = BehandlingId.random();
        var kontrollResultatWrapper = new KontrollresultatWrapper(behandlingId, Kontrollresultat.IKKE_HØY);
        risikovurderingTjeneste.lagreKontrollresultat(kontrollResultatWrapper);
        verify(risikoklassifiseringRepository).lagreRisikoklassifisering(any(), any());
    }
}

