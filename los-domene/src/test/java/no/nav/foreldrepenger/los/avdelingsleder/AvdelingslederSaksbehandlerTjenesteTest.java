package no.nav.foreldrepenger.los.avdelingsleder;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;

@ExtendWith(JpaExtension.class)
public class AvdelingslederSaksbehandlerTjenesteTest {

    private static final String NY_SAKSBEHANDLER_IDENT = "zNySaksbehandler";

    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;

    @BeforeEach
    public void setup(EntityManager entityManager){
        var oppgaveRepository = new OppgaveRepository(entityManager);
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        avdelingslederSaksbehandlerTjeneste = new AvdelingslederSaksbehandlerTjeneste(oppgaveRepository, organisasjonRepository);
    }

    @Test
    public void testHentSaksbehandlere() {
        var saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isNotEmpty();
        assertThat(saksbehandlers.get(0).getId()).isNotNull();
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
    }

    @Test
    public void testLagreNySaksbehandler() {
        var saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
    }

    @Test
    public void testSlettSaksbehandler() {
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        var saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
        avdelingslederSaksbehandlerTjeneste.fjernSaksbehandlerFraAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        var saksb = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksb).isEmpty();
    }
}
