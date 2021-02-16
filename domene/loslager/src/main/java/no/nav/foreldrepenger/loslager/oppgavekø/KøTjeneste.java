package no.nav.foreldrepenger.loslager.oppgavekø;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Oppgavekø> finnKøerSomInneholder(Oppgave oppgave) {
        var enhet = oppgave.getBehandlendeEnhet();

        //var potensielleKøer = oppgaveRepository.hentAlleOppgaveFiltreringsettTilknyttetAvdeling(enhet);
        //var oppgaveFiltreringer = oppgaveRepository.hent

        return Collections.emptyList();


    }

}
