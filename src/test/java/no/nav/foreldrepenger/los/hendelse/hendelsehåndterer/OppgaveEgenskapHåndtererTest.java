package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.PAPIRSØKNAD;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.UTLANDSSAK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;

import no.nav.foreldrepenger.los.DBTestUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class OppgaveEgenskapHåndtererTest {

    private static final BehandlingId BEHANDLING_ID = BehandlingId.random();

    private OppgaveEgenskapHåndterer egenskapHåndterer;

    @Mock
    private OppgaveEgenskapFinner oppgaveEgenskapFinner;
    @Mock
    private Beskyttelsesbehov beskyttelsesbehov;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        egenskapHåndterer = new OppgaveEgenskapHåndterer(beskyttelsesbehov);
    }

    @Test
    void opprettOppgaveEgenskaperTest() {
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(List.of(UTLANDSSAK, PAPIRSØKNAD));
        var oppgave = lagOppgave();

        egenskapHåndterer.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);

        assertThat(oppgave.getOppgaveEgenskaper().stream().map(OppgaveEgenskap::getAndreKriterierType))
            .containsOnly(UTLANDSSAK, PAPIRSØKNAD);
    }

    @Test
    void opprettOppgaveEgenskaperMedKode7Test() {
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(List.of());
        when(beskyttelsesbehov.getBeskyttelsesKriterier(any(Saksnummer.class))).thenReturn(Set.of(AndreKriterierType.KODE7_SAK));
        var oppgave = lagOppgave();

        egenskapHåndterer.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);

        assertThat(oppgave.getOppgaveEgenskaper().stream().map(OppgaveEgenskap::getAndreKriterierType))
            .containsOnly(AndreKriterierType.KODE7_SAK);
    }

    @Test
    void deaktiverUaktuelleEksisterendeOppgaveEgenskaper() {
        var oppgave = lagOppgave();
        var uaktuellEgenskap = OppgaveEgenskap.builder().medAndreKriterierType(PAPIRSØKNAD).build();
        oppgave.leggTilOppgaveEgenskap(uaktuellEgenskap);
        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(emptyList());

        egenskapHåndterer.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);

        assertThat(oppgave.getOppgaveEgenskaper()).isEmpty();
    }

    @Test
    void kunEttAktivtTilfelleAvHverEgenskap() {
        var oppgave = lagOppgave();
        oppgave.leggTilOppgaveEgenskap(OppgaveEgenskap.builder().medAndreKriterierType(UTLANDSSAK).build());
        entityManager.persist(oppgave);
        entityManager.flush();

        when(oppgaveEgenskapFinner.getAndreKriterier()).thenReturn(List.of(UTLANDSSAK));

        egenskapHåndterer.håndterOppgaveEgenskaper(oppgave, oppgaveEgenskapFinner);
        entityManager.flush(); // sørge for å trigge orphanremoval / avdekke eventuelle feil med equals etc
        entityManager.refresh(oppgave);

        assertThat(oppgave.getOppgaveEgenskaper()).size().isEqualTo(1);
        assertThat(oppgave.getOppgaveEgenskaper()).first().matches(oe -> oe.getAndreKriterierType().equals(UTLANDSSAK));
        var oe = DBTestUtil.hentAlle(entityManager, OppgaveEgenskap.class);
        assertThat(oe).hasSize(1);
    }

    private Oppgave lagOppgave() {
        return Oppgave.builder()
            .medSaksnummer(new Saksnummer("42"))
            .medBehandlingId(BEHANDLING_ID)
            .medAktørId(AktørId.dummy())
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medBehandlendeEnhet("0000")
            .medBehandlingsfrist(LocalDateTime.now())
            .medBehandlingOpprettet(LocalDateTime.now())
            .medFørsteStønadsdag(LocalDate.now().plusMonths(1))
            .build();
    }
}
