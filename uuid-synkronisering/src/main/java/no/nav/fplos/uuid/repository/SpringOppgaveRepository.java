package no.nav.fplos.uuid.repository;


import no.nav.fplos.uuid.dao.Oppgave;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SpringOppgaveRepository extends CrudRepository<Oppgave, Long> {

    @Query("SELECT o FROM Oppgave o WHERE o.eksternId is null")
    List<Oppgave> finnOppgaverUtenEksternId();

}
