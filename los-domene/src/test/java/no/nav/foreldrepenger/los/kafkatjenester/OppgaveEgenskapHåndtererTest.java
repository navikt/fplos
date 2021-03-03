package no.nav.foreldrepenger.los.kafkatjenester;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.PAPIRSØKNAD;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.UTLANDSSAK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
@ExtendWith(MockitoExtension.class)
public class OppgaveEgenskapHåndtererTest {

    private static final BehandlingId BEHANDLING_ID = BehandlingId.random();

    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer egenskapHandler;

    @Mock
    private OppgaveEgenskapFinner oppgaveEgenskapFinner;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        egenskapHandler = new OppgaveEgenskapHåndterer(oppgaveRepository);
    }

    @Test
    public void opprettOppgaveEgenskaperTest() {
        // arrange
        var ønskedeEgenskaper = kriterieArrayOf(UTLANDSSAK, PAPIRSØKNAD);
        when(oppgaveEgenskapFinner.getSaksbehandlerForTotrinn()).thenReturn("T12345");
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(Arrays.asList(ønskedeEgenskaper));

        // act
        egenskapHandler.håndterOppgaveEgenskaper(lagOppgave(), oppgaveEgenskapFinner);

        // assert
        assertThat(hentAktiveKriterierPåOppgave(42L)).containsExactlyInAnyOrder(ønskedeEgenskaper);
    }

    @Test
    public void deaktiverUaktuelleEksisterendeOppgaveEgenskaper() {
        var oppgave = lagOppgave();
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, PAPIRSØKNAD));
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(emptyList());
        egenskapHandler.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);

        assertThat(hentAktiveKriterierPåOppgave(42L)).isEmpty();
    }

    @Test
    public void kunEttAktivtTilfelleAvHverEgenskap() {
        var oppgave = lagOppgave();
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, UTLANDSSAK));
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(List.of(UTLANDSSAK));
        egenskapHandler.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);

        assertThat(hentAktiveKriterierPåOppgave(42L)).containsExactly(UTLANDSSAK);
    }

    @Test
    public void deaktiveringHåndtererDuplikateOppgaveEgenskaper() {
        // Bug i prod har opprettet dubletter. Tester deaktivering av samtlige dubletter.
        var oppgave = lagOppgave();
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, PAPIRSØKNAD));
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, PAPIRSØKNAD));
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, PAPIRSØKNAD));
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, UTLANDSSAK));
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(emptyList());
        egenskapHandler.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);

        assertThat(hentAktiveKriterierPåOppgave(42L)).isEmpty();
    }

    private static AndreKriterierType[] kriterieArrayOf(AndreKriterierType... kriterier) {
        return kriterier; // ble hakket penere enn å ha AndreKriterierType[] overalt.
    }

    private List<AndreKriterierType> hentAktiveKriterierPåOppgave(Long saksnummer) {
        return oppgaveRepository.hentOppgaveEgenskaper(oppgaveId(saksnummer)).stream()
                .filter(OppgaveEgenskap::getAktiv)
                .map(OppgaveEgenskap::getAndreKriterierType)
                .collect(Collectors.toList());
    }

    private Long oppgaveId(Long saksnummer) {
        return oppgaveRepository.hentAktiveOppgaverForSaksnummer(List.of(saksnummer)).stream()
                .map(Oppgave::getId)
                .findFirst()
                .orElseThrow();
    }

    private Oppgave lagOppgave() {
        Oppgave oppgave = Oppgave.builder()
                .medFagsakSaksnummer(42L)
                .medBehandlingId(BEHANDLING_ID)
                .medAktorId(1L)
                .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlendeEnhet("0000")
                .medBehandlingsfrist(LocalDateTime.now())
                .medBehandlingOpprettet(LocalDateTime.now())
                .medForsteStonadsdag(LocalDate.now().plusMonths(1))
                .medBehandlingStatus(BehandlingStatus.UTREDES)
                .build();
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }
}
