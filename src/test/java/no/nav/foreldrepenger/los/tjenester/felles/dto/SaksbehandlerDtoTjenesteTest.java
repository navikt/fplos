package no.nav.foreldrepenger.los.tjenester.felles.dto;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgave.OppgaveKøRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.organisasjon.ansatt.BrukerProfil;

@ExtendWith(JpaExtension.class)
class SaksbehandlerDtoTjenesteTest {

    private AvdelingslederTjeneste avdelingslederTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;
    private AnsattTjeneste ansattTjeneste;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        var oppgaveRepository = new OppgaveRepository(entityManager);
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
        ansattTjeneste = mock(AnsattTjeneste.class);
        var oppgaveKøRepository = new OppgaveKøRepository(entityManager);
        saksbehandlerDtoTjeneste = new SaksbehandlerDtoTjeneste(organisasjonRepository, avdelingslederTjeneste, ansattTjeneste,
            new OppgaveKøTjeneste(oppgaveRepository, oppgaveKøRepository, organisasjonRepository));
    }

    @Test
    void testHentSaksbehandlerNavnOgAvdelinger(EntityManager entityManager) {
        var saksbehandler1Ident = "1234567";
        var saksbehandler2Ident = "9876543";
        var saksbehandler3Ident = "1234";

        var saksbehandler1 = new Saksbehandler(saksbehandler1Ident, UUID.randomUUID(), "Navn Navnesen", "1234");
        var saksbehandler2 = new Saksbehandler(saksbehandler2Ident, UUID.randomUUID(), "Navn2 Navnesen", "1234");
        entityManager.persist(saksbehandler1);
        entityManager.persist(saksbehandler2);
        entityManager.flush();

        var lagtInnLister = leggInnEtSettMedLister(1, entityManager);

        avdelingslederTjeneste.leggSaksbehandlerTilListe(lagtInnLister.get(0).getId(), saksbehandler1.getSaksbehandlerIdent());
        entityManager.refresh(saksbehandler1);

        assertThat(saksbehandlerDtoTjeneste.hentSaksbehandlerTilknyttetMinstEnKø(saksbehandler3Ident)).isEmpty();
        assertThat(saksbehandlerDtoTjeneste.hentSaksbehandlerTilknyttetMinstEnKø(saksbehandler2Ident)).isEmpty();
    }

    @Test
    void testHentSaksbehandlerSomIkkeFinnesILos(EntityManager entityManager) {
        var saksbehandler1Ident = "Z999999";

        when(ansattTjeneste.hentBrukerProfil(saksbehandler1Ident))
            .thenReturn(Optional.of(new BrukerProfil(UUID.randomUUID(), saksbehandler1Ident, "Navn Navnesen", "Avdelingsnavnet")));
        var saksbehandlerDto = saksbehandlerDtoTjeneste.saksbehandlerDtoForNavIdent(saksbehandler1Ident);

        assertThat(saksbehandlerDto)
            .isPresent()
            .hasValueSatisfying(dto -> {
                assertThat(dto.getBrukerIdent()).isEqualTo(saksbehandler1Ident);
                assertThat(dto.navn()).isEqualTo("Navn Navnesen");
                assertThat(dto.ansattAvdeling()).isEqualTo("Avdelingsnavnet");
            });
    }

    private List<OppgaveFiltrering> leggInnEtSettMedLister(int antallLister, EntityManager entityManager) {
        List<OppgaveFiltrering> filtre = new ArrayList<>();

        var avdelings = avdelingslederTjeneste.hentAvdelinger();
        var avdelingDrammen = avdelings.stream()
            .filter(avdeling -> AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet()))
            .findFirst()
            .orElseThrow();
        for (var i = 0; i < antallLister; i++) {
            var oppgaveFiltrering = OppgaveFiltrering.builder()
                .medNavn("Test " + i)
                .medSortering(KøSortering.BEHANDLINGSFRIST)
                .medAvdeling(avdelingDrammen)
                .build();
            entityManager.persist(oppgaveFiltrering);
            filtre.add(oppgaveFiltrering);
        }
        entityManager.flush();
        return filtre;
    }

}
