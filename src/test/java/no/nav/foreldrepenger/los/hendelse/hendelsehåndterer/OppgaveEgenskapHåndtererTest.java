package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.PAPIRSØKNAD;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.UTLANDSSAK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.los.web.extensions.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class OppgaveEgenskapHåndtererTest {

    private static final BehandlingId BEHANDLING_ID = BehandlingId.random();

    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer egenskapHandler;

    @Mock
    private OppgaveEgenskapFinner oppgaveEgenskapFinner;
    @Mock
    private Beskyttelsesbehov beskyttelsesbehov;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepository(entityManager);
        egenskapHandler = new OppgaveEgenskapHåndterer(oppgaveRepository, beskyttelsesbehov);
    }

    @Test
    void opprettOppgaveEgenskaperTest() {
        // arrange
        var ønskedeEgenskaper = kriterieArrayOf(UTLANDSSAK, PAPIRSØKNAD);
        when(oppgaveEgenskapFinner.getSaksbehandlerForTotrinn()).thenReturn("T12345");
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(Arrays.asList(ønskedeEgenskaper));

        // act
        egenskapHandler.håndterOppgaveEgenskaper(lagOppgave(), oppgaveEgenskapFinner);

        // assert
        Assertions.assertThat(hentAktiveKriterierPåOppgave(42L)).containsExactlyInAnyOrder(ønskedeEgenskaper);
    }

    @Test
    void opprettOppgaveEgenskaperMedKode7Test() {
        // arrange
        var ønskedeEgenskaper = kriterieArrayOf(UTLANDSSAK, PAPIRSØKNAD);
        when(oppgaveEgenskapFinner.getSaksbehandlerForTotrinn()).thenReturn("T12345");
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(Arrays.asList(ønskedeEgenskaper));
        when(beskyttelsesbehov.getBeskyttelsesKriterier(any())).thenReturn(Set.of(AndreKriterierType.KODE7_SAK));

        // act
        egenskapHandler.håndterOppgaveEgenskaper(lagOppgave(), oppgaveEgenskapFinner);

        // assert
        Assertions.assertThat(hentAktiveKriterierPåOppgave(42L)).contains(AndreKriterierType.KODE7_SAK);
    }

    @Test
    void deaktiverUaktuelleEksisterendeOppgaveEgenskaper() {
        var oppgave = lagOppgave();
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, PAPIRSØKNAD));
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(emptyList());
        egenskapHandler.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);

        Assertions.assertThat(hentAktiveKriterierPåOppgave(42L)).isEmpty();
    }

    @Test
    void kunEttAktivtTilfelleAvHverEgenskap() {
        var oppgave = lagOppgave();
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, UTLANDSSAK));
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(List.of(UTLANDSSAK));
        egenskapHandler.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);

        Assertions.assertThat(hentAktiveKriterierPåOppgave(42L)).containsExactly(UTLANDSSAK);
    }

    @Test
    void deaktiveringHåndtererDuplikateOppgaveEgenskaper() {
        // Bug i prod har opprettet dubletter. Tester deaktivering av samtlige dubletter.
        var oppgave = lagOppgave();
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, PAPIRSØKNAD));
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, PAPIRSØKNAD));
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, PAPIRSØKNAD));
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, UTLANDSSAK));
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(emptyList());
        egenskapHandler.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);

        Assertions.assertThat(hentAktiveKriterierPåOppgave(42L)).isEmpty();
    }

    private static AndreKriterierType[] kriterieArrayOf(AndreKriterierType... kriterier) {
        return kriterier; // ble hakket penere enn å ha AndreKriterierType[] overalt.
    }

    private List<AndreKriterierType> hentAktiveKriterierPåOppgave(Long saksnummer) {
        return oppgaveRepository.hentOppgaveEgenskaper(oppgaveId(saksnummer))
            .stream()
            .filter(OppgaveEgenskap::getAktiv)
            .map(OppgaveEgenskap::getAndreKriterierType)
            .collect(Collectors.toList());
    }

    private Long oppgaveId(Long saksnummer) {
        return oppgaveRepository.hentAktiveOppgaverForSaksnummer(List.of(saksnummer)).stream().map(Oppgave::getId).findFirst().orElseThrow();
    }

    private Oppgave lagOppgave() {
        var oppgave = Oppgave.builder()
            .medFagsakSaksnummer(42L)
            .medBehandlingId(BEHANDLING_ID)
            .medAktørId(AktørId.dummy())
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medBehandlendeEnhet("0000")
            .medBehandlingsfrist(LocalDateTime.now())
            .medBehandlingOpprettet(LocalDateTime.now())
            .medFørsteStønadsdag(LocalDate.now().plusMonths(1))
            .medBehandlingStatus(BehandlingStatus.UTREDES)
            .build();
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }
}
