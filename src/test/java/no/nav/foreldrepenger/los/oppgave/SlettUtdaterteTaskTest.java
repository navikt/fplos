package no.nav.foreldrepenger.los.oppgave;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.oppgavekø.SlettUtdaterteTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;

@ExtendWith(JpaExtension.class)
class SlettUtdaterteTaskTest {


    private EntityManager entityManager;


    @BeforeEach
    void setup(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    void testSletting() {
        lagStandardSettMedOppgaver();
        assertDoesNotThrow(() -> new SlettUtdaterteTask(entityManager).doTask(ProsessTaskData.forProsessTask(SlettUtdaterteTask.class)));
    }

    private void lagStandardSettMedOppgaver() {
        var behandlingId = UUID.randomUUID();

        var behandling = Behandling.builder(Optional.empty())
            .dummyBehandling(AVDELING_DRAMMEN_ENHET, BehandlingTilstand.AVSLUTTET)
            .medKildeSystem(Fagsystem.FPSAK)
            .medId(behandlingId)
            .medAvsluttet(LocalDateTime.now().minusMonths(5))
            .build();
        entityManager.persist(behandling);

        var oppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingId(new BehandlingId(behandlingId))
            .medSaksnummer(new Saksnummer("111"))
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(10))
            .medBehandlingsfrist(LocalDateTime.now().plusDays(10))
            .medAktiv(false)
            .build();
        oppgave.setEndretTidspunkt(LocalDateTime.now().minusMonths(5));
        oppgave.leggTilOppgaveEgenskap(OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.PAPIRSØKNAD).build());
        oppgave.leggTilOppgaveEgenskap(OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());

        entityManager.persist(oppgave);


        entityManager.flush();
    }

}
