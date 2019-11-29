package no.nav.fplos.batch;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SpringOppgaveRepositoryInterface extends CrudRepository<Oppgave, Long> {

}
