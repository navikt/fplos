package no.nav.foreldrepenger.los.kafkatjenester;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.PAPIRSØKNAD;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.UTLANDSSAK;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.los.oppgave.oppgaveegenskap.AktuelleOppgaveEgenskaperData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class OppgaveEgenskapHåndtererTest {

    private static final BehandlingId BEHANDLING_ID = BehandlingId.random();
    private static final AktuelleOppgaveEgenskaperData INGEN_AKTUELLE_OPPGAVEEGENSKAPER = new AktuelleOppgaveEgenskaperData("IDENT", emptyList());

    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer egenskapHandler;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        egenskapHandler = new OppgaveEgenskapHåndterer(oppgaveRepository);
    }

    @Test
    public void opprettOppgaveEgenskaperTest() {
        // arrange
        var ønskedeEgenskaper = List.of(UTLANDSSAK, PAPIRSØKNAD);
        var aktuelleEgenskaper = new AktuelleOppgaveEgenskaperData("T12345", ønskedeEgenskaper);

        // act
        egenskapHandler.håndterOppgaveEgenskaper(lagOppgave(), aktuelleEgenskaper);

        // assert
        assertThat(hentAktiveKriterierPåOppgave(42L)).containsAll(ønskedeEgenskaper);
    }

    @Test
    public void deaktiverUaktuelleEksisterendeOppgaveEgenskaper() {
        var oppgave = lagOppgave();
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, PAPIRSØKNAD));

        egenskapHandler.håndterOppgaveEgenskaper(oppgave, INGEN_AKTUELLE_OPPGAVEEGENSKAPER);

        assertThat(hentAktiveKriterierPåOppgave(42L)).isEmpty();
    }

    @Test
    public void kunEttAktivtTilfelleAvHverEgenskap() {
        var oppgave = lagOppgave();
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, UTLANDSSAK));
        var aktuelleEgenskaper = new AktuelleOppgaveEgenskaperData("IDENT", List.of(UTLANDSSAK));

        egenskapHandler.håndterOppgaveEgenskaper(oppgave, aktuelleEgenskaper);

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

        egenskapHandler.håndterOppgaveEgenskaper(oppgave, INGEN_AKTUELLE_OPPGAVEEGENSKAPER);

        assertThat(hentAktiveKriterierPåOppgave(42L)).isEmpty();
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
