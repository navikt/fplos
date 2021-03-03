package no.nav.foreldrepenger.los.avdelingsleder;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepositoryImpl;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.EntityManager;
import java.util.List;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

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
        Assertions.assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        Assertions.assertThat(saksbehandlers).isNotEmpty();
        Assertions.assertThat(saksbehandlers.get(0).getId()).isNotNull();
        Assertions.assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
    }

    @Test
    public void testLagreNySaksbehandler() {
        List<Saksbehandler> saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        Assertions.assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        Assertions.assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        Assertions.assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
    }

    @Test
    public void testSlettSaksbehandler() {
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        List<Saksbehandler> saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        Assertions.assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        Assertions.assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
        avdelingslederSaksbehandlerTjeneste.fjernSaksbehandlerFraAvdeling(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        var saksb = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksb).isEmpty();
    }
}
