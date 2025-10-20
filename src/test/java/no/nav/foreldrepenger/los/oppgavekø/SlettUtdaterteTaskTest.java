package no.nav.foreldrepenger.los.oppgavekø;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.JpaExtension;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(JpaExtension.class)
class SlettUtdaterteTaskTest {

    private EntityManager entityManager;

    @BeforeEach
    void setup(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Test
    void enkelTestAvKjørbarhetSlettUtdaterteOppgaveEventLogg() {
        for (int i = 0; i < 20; i++) {
            var oel = OppgaveEventLogg.builder().type(OppgaveEventType.LUKKET).behandlendeEnhet("4867").behandlingId(BehandlingId.random()).build();
            entityManager.persist(oel);
        }
        entityManager.flush();
        entityManager.createNativeQuery("UPDATE oppgave_event_logg SET OPPRETTET_TID = current_timestamp - interval '14' month WHERE mod(id, 2) = 0")
            .executeUpdate();

        new SlettUtdaterteTask(entityManager).doTask(null);

        var antallGjenværende = DBTestUtil.hentAlle(entityManager, OppgaveEventLogg.class);
        assertThat(antallGjenværende).hasSize(10);
    }


}
