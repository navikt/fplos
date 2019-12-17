package no.nav.fplos.uuid.repository;


import no.nav.fplos.uuid.dao.Oppgave;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Repository
public interface SpringOppgaveRepository extends CrudRepository<Oppgave, Long> {

    @Query("SELECT o FROM Oppgave o WHERE o.eksternId is null")
    List<Oppgave> finnOppgaverUtenEksternId();

    @Modifying
    @Query("UPDATE Oppgave o set o.eksternId = :eksternId where o.behandlingId = :behandlingId and o.eksternId is null")
    @Transactional
    void settInnUUIDForOppgaverMedBehandlingId(@Param("behandlingId") Long behandlingId, @Param("eksternId") UUID eksternId);
}
