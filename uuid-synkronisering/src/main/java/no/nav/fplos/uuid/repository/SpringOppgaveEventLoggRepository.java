package no.nav.fplos.uuid.repository;

import no.nav.fplos.uuid.dao.OppgaveEventLogg;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringOppgaveEventLoggRepository extends CrudRepository<OppgaveEventLogg, Long> {

    @Query("SELECT oe FROM OppgaveEventLogg oe WHERE oe.eksternId is null")
   List<OppgaveEventLogg> finnOppgaveEventerUtenEksternId();

}

