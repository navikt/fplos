package no.nav.fplos.batch.repository;

import no.nav.fplos.batch.dao.OppgaveEventLogg;
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

    @Query("SELECT distinct oe.behandlingId FROM OppgaveEventLogg oe WHERE oe.eksternId is null")
   List<Long> finnBehandlingIdForOppgaveEventerUtenEksternId();

    @Modifying
    @Query("UPDATE OppgaveEventLogg oe set oe.eksternId = :eksternId where oe.behandlingId = :behandlingId AND oe.eksternId is null")
    @Transactional
    void settInnUUIDForOppgaveEventerMedBehandlingId(@Param("behandlingId") Long behandlingId, @Param("eksternId") UUID eksternId);

}

