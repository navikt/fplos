package no.nav.fplos.avdelingsleder;

import static no.nav.foreldrepenger.loslager.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class AvdelingslederSaksbehandlerTjenesteImplTest {

    private static final String NY_SAKSBEHANDLER_IDENT = "zNySaksbehandler";

    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;

    @BeforeEach
    public void setup(EntityManager entityManager){
        OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        OrganisasjonRepository organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
        avdelingslederSaksbehandlerTjeneste = new AvdelingslederSaksbehandlerTjenesteImpl(oppgaveRepository, organisasjonRepository);
    }

    @Test
    public void testHentSaksbehandlere() {
        List<Saksbehandler> saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggTilSaksbehandler(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isNotEmpty();
        assertThat(saksbehandlers.get(0).getId()).isNotNull();
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
    }

    @Test
    public void testLagreNySaksbehandler() {
        List<Saksbehandler> saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggTilSaksbehandler(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
    }

    @Test
    public void testSlettSaksbehandler() {
        avdelingslederSaksbehandlerTjeneste.leggTilSaksbehandler(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        List<Saksbehandler> saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
        avdelingslederSaksbehandlerTjeneste.slettSaksbehandler(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        var saksb = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksb).isEmpty();
    }
}
