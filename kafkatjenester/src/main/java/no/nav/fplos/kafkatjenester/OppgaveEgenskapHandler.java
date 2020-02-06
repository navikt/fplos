package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.vedtak.felles.jpa.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Transaction
public class OppgaveEgenskapHandler {

    private static final Logger log = LoggerFactory.getLogger(OppgaveEgenskapHandler.class);

    private OppgaveRepository repository;

    public OppgaveEgenskapHandler() {
        // for cdi
    }

    @Inject
    public OppgaveEgenskapHandler(OppgaveRepository oppgaveRepository) {
        this.repository = oppgaveRepository;
    }

    void håndterOppgaveEgenskaper(Oppgave oppgave, OppgaveEgenskapFinner event) {
        var andreKriterier = event.getAndreKriterier();
        log.info("Legger på oppgaveegenskaper {}", andreKriterier);
        List<OppgaveEgenskap> eksisterende = hentEksisterendeEgenskaper(oppgave);

        // deaktiver uaktuelle eksisterende
        eksisterende.stream()
                .filter(akt -> !andreKriterier.contains(akt.getAndreKriterierType()))
                .forEach(this::deaktiver);

        // aktiver aktuelle eksisterende
        eksisterende.stream()
                .filter(akt -> andreKriterier.contains(akt.getAndreKriterierType()))
                .forEach(oe -> aktiver(oe, event.getSaksbehandlerForTotrinn()));

        var eksisterendeOppgaveEgenskaper = eksisterende.stream()
                .map(OppgaveEgenskap::getAndreKriterierType)
                .collect(Collectors.toList());

        // aktiver nye
        andreKriterier.stream()
                .filter(akt -> !eksisterendeOppgaveEgenskaper.contains(akt))
                .forEach(k -> opprettOppgaveEgenskap(oppgave, k, event.getSaksbehandlerForTotrinn()));
    }

    private void opprettOppgaveEgenskap(Oppgave oppgave, AndreKriterierType kritere, String saksbehandler) {
        if (kritere.equals(AndreKriterierType.TIL_BESLUTTER)) {
            repository.lagre(new OppgaveEgenskap(oppgave, kritere, saksbehandler));
        } else {
            repository.lagre(new OppgaveEgenskap(oppgave, kritere));
        }
    }

    private void deaktiver(OppgaveEgenskap oppgaveEgenskap) {
        oppgaveEgenskap.deaktiverOppgaveEgenskap();
        repository.lagre(oppgaveEgenskap);
    }

    private void aktiver(OppgaveEgenskap oppgaveEgenskap, String saksbehandler) {
        if (oppgaveEgenskap.getAndreKriterierType().erTilBeslutter()) {
            oppgaveEgenskap.aktiverOppgaveEgenskap();
            oppgaveEgenskap.setSisteSaksbehandlerForTotrinn(saksbehandler);
        } else {
            oppgaveEgenskap.aktiverOppgaveEgenskap();
        }
        repository.lagre(oppgaveEgenskap);
    }

    List<OppgaveEgenskap> hentEksisterendeEgenskaper(Oppgave oppgave) {
        return Optional.ofNullable(repository.hentOppgaveEgenskaper(oppgave.getId()))
                .orElse(Collections.emptyList());
    }

}
