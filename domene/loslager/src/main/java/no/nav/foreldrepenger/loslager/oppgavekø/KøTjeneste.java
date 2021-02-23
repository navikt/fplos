package no.nav.foreldrepenger.loslager.oppgavekø;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class KøTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(KøTjeneste.class);

    private OppgaveRepository oppgaveRepository;
    private EntityManager entityManager;

    @Inject
    public KøTjeneste(OppgaveRepository oppgaveRepository, EntityManager entityManager) {
        this.oppgaveRepository = oppgaveRepository;
        this.entityManager = entityManager;
    }

    public List<OppgaveFiltreringKnytning> finnKøerSomInneholder(Oppgave oppgave) {
        var enhet = oppgave.getBehandlendeEnhet();

        //var potensielleKøer = oppgaveRepository.hentAlleOppgaveFiltreringsettTilknyttetAvdeling(enhet);
        //var oppgaveFiltreringer = oppgaveRepository.hent

        return Collections.emptyList();


    }

}
