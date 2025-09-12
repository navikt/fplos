package no.nav.foreldrepenger.los.tjenester.avdelingsleder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.avdelingsleder.innlogget.AnsattInfoKlient;
import no.nav.foreldrepenger.los.avdelingsleder.innlogget.InnloggetNavAnsattDto;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingDto;
import no.nav.vedtak.sikkerhet.kontekst.AnsattGruppe;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class AvdelingslederRestTjenesteTest {

    private AvdelingslederRestTjeneste avdelingslederRestTjeneste;
    private AnsattInfoKlient ansattInfoKlient;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        AvdelingslederTjeneste avdelingslederTjeneste = new AvdelingslederTjeneste(mock(OppgaveRepository.class), organisasjonRepository);
        ansattInfoKlient = mock(AnsattInfoKlient.class);
        avdelingslederRestTjeneste = new AvdelingslederRestTjeneste(avdelingslederTjeneste, ansattInfoKlient);
        this.entityManager = entityManager;
    }

    @Test
    void skalFiltrereAvdelingerPåTilgang() {
        // Sett opp avdelinger og bruker
        entityManager.persist(new Avdeling("4321", "Ordinær avdeling", false));
        entityManager.persist(new Avdeling("1234", "Strengt fortrolig avdeling", true));
        entityManager.flush();
        var innloggetBruker = new InnloggetNavAnsattDto("L123456", "Test Bruker", true);

        // innlogget avdelingsleder har tilgang til strengt fortrolig avdeling
        when(ansattInfoKlient.medlemAvAnsattGruppe(AnsattGruppe.STRENGTFORTROLIG)).thenReturn(true);
        when(ansattInfoKlient.innloggetBruker()).thenReturn(innloggetBruker);
        var result = avdelingslederRestTjeneste.hentInitielleRessurser();
        assertThat(result.avdelinger())
            .anyMatch(
            a -> a.getKreverKode6() && a.getAvdelingEnhet().equals("1234") && a.getNavn().equals("Strengt fortrolig avdeling"))
            .anyMatch(a -> !a.getKreverKode6() && a.getAvdelingEnhet().equals("4321"));

        // her skal innlogget avdelingsleder ikke ha tilgang til strengt fortrolig avdeling
        when(ansattInfoKlient.medlemAvAnsattGruppe(AnsattGruppe.STRENGTFORTROLIG)).thenReturn(false);
        var resultIkkeTilgang = avdelingslederRestTjeneste.hentInitielleRessurser();
        assertThat(resultIkkeTilgang.avdelinger()).noneMatch(AvdelingDto::getKreverKode6).anyMatch(a -> !a.getKreverKode6());
    }

}
