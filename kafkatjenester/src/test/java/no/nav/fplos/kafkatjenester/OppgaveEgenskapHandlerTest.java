package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class OppgaveEgenskapHandlerTest {

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private OppgaveEgenskapHandler egenskapHandler = new OppgaveEgenskapHandler(oppgaveRepository);
    @Mock
    private OppgaveEgenskapFinner oppgaveEgenskapFinner;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

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
        return oppgaveRepository.hentOppgaverForSaksnummer(saksnummer).stream()
                .map(Oppgave::getId)
                .findFirst()
                .orElseThrow();
    }

    private Oppgave lagOppgave() {
        Oppgave oppgave = Oppgave.builder()
                .medFagsakSaksnummer(42L)
                .medBehandlingId(1L)
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
