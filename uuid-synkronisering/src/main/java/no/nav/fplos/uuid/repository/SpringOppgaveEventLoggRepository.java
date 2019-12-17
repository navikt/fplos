package no.nav.fplos.uuid.repository;

import no.nav.fplos.uuid.dao.OppgaveEventLogg;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringOppgaveEventLoggRepository extends CrudRepository<OppgaveEventLogg, Long> {

    @Query("SELECT oe FROM OppgaveEventLogg oe WHERE oe.eksternId is null")
   List<OppgaveEventLogg> finnOppgaveEventerUtenEksternId();

    @Modifying
    @Query("UPDATE OppgaveEventLogg oe set oe.eksternId = :eksternId where oe.behandlingId = :behandlingId AND oe.eksternId is null")
    @Transactional
    void settInnUUIDForOppgaveEventerMedBehandlingId(@Param("behandlingId") Long behandlingId, @Param("eksternId") UUID eksternId);

}

