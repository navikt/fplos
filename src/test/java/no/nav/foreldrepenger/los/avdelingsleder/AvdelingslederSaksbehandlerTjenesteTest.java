package no.nav.foreldrepenger.los.avdelingsleder;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
    private OrganisasjonRepository organisasjonRepository;
    private EntityManager em;

    @Mock
    private AnsattTjeneste ansattTjeneste;

    @BeforeEach
    void setup(EntityManager entityManager) {
        var oppgaveRepository = new OppgaveRepository(entityManager);
        organisasjonRepository = new OrganisasjonRepository(entityManager);
        when(ansattTjeneste.hentBrukerProfil(anyString())).thenReturn(Optional.of(new BrukerProfil("A000001", "Ansatt Navn", "4867")));
        avdelingslederSaksbehandlerTjeneste = new AvdelingslederSaksbehandlerTjeneste(oppgaveRepository, organisasjonRepository, ansattTjeneste);
        em = entityManager;
    }

    @Test
    void testHentSaksbehandlere() {
        var saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        em.flush();
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
        em.flush();
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(organisasjonRepository.avdelingerForSaksbehandler(saksbehandlers.get(0))).hasSize(1);
    }

    @Test
    void testSlettSaksbehandler() {
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        em.flush();
        var saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(organisasjonRepository.avdelingerForSaksbehandler(saksbehandlers.get(0))).hasSize(1);
        avdelingslederSaksbehandlerTjeneste.fjernSaksbehandlerFraAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        em.flush();
        var saksb = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksb).isEmpty();
    }
}
