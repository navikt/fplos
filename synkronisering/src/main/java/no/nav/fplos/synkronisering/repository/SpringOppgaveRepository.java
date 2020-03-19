package no.nav.fplos.synkronisering.repository;

import no.nav.fplos.synkronisering.dao.Oppgave;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringOppgaveRepository extends CrudRepository<Oppgave, Long> {

    @Query("SELECT distinct o.behandlingId FROM Oppgave o " +
            "WHERE o.aktiv = true " +
            "AND o.system = 'FPSAK'" +
            "AND (o.forsteStonadsdag IS NULL OR o.behandlingsfrist IS NULL)")
    List<UUID> finnBehandlingerUtenFelter();

    @Modifying
    @Query("UPDATE Oppgave o SET o.forsteStonadsdag = :forsteStonadsdag, o.behandlingsfrist = :behandlingsfrist " +
            "WHERE o.aktiv = true AND o.behandlingId = :behandlingId")
    @Transactional
    void oppdaterBehandlingsfristOgFørstestønadsdag(@Param("behandlingId") UUID behandlingId,
                                                    @Param("forsteStonadsdag") LocalDate forsteStonadsdag,
                                                    @Param("behandlingsfrist") LocalDateTime behandlingsfrist);
}
