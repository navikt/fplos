package no.nav.foreldrepenger.los.avdelingsleder;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.organisasjon.ansatt.BrukerProfil;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class AvdelingslederSaksbehandlerTjenesteTest {

    private static final String NY_SAKSBEHANDLER_IDENT = "zNySaksbehandler";

    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;

    @Mock
    private AnsattTjeneste ansattTjeneste;

    @BeforeEach
    void setup(EntityManager entityManager) {
        var oppgaveRepository = new OppgaveRepository(entityManager);
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        when(ansattTjeneste.hentBrukerProfil(anyString())).thenReturn(new BrukerProfil(UUID.randomUUID(), "A000001", "Ansatt Navn", "4867"));
        avdelingslederSaksbehandlerTjeneste = new AvdelingslederSaksbehandlerTjeneste(oppgaveRepository, organisasjonRepository, ansattTjeneste);
    }

    @Test
    void testHentSaksbehandlere() {
        var saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isNotEmpty();
        assertThat(saksbehandlers.get(0).getId()).isNotNull();
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
    }

    @Test
    void testLagreNySaksbehandler() {
        var saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
    }

    @Test
    void testSlettSaksbehandler() {
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        var saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
        avdelingslederSaksbehandlerTjeneste.fjernSaksbehandlerFraAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        var saksb = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksb).isEmpty();
    }
}
