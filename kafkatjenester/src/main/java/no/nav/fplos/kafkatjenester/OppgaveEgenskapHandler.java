package no.nav.fplos.kafkatjenester;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;

@ApplicationScoped
@Transactional
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

    public void håndterOppgaveEgenskaper(Oppgave oppgave, OppgaveEgenskapFinner aktuelleEgenskaper) {
        var andreKriterier = aktuelleEgenskaper.getAndreKriterier();
        log.info("Legger på oppgaveegenskaper {}", andreKriterier);
        var eksisterendeOppgaveEgenskaper = hentEksisterendeEgenskaper(oppgave);

        // deaktiver uaktuelle eksisterende
        eksisterendeOppgaveEgenskaper.stream()
                .filter(akt -> !andreKriterier.contains(akt.getAndreKriterierType()))
                .forEach(this::deaktiver);

        // aktiver aktuelle eksisterende
        eksisterendeOppgaveEgenskaper.stream()
                .filter(akt -> andreKriterier.contains(akt.getAndreKriterierType()))
                .forEach(oe -> aktiver(oe, aktuelleEgenskaper.getSaksbehandlerForTotrinn()));

        var eksisterendeTyper = typer(eksisterendeOppgaveEgenskaper);

        // aktiver nye
        andreKriterier.stream()
                .filter(akt -> !eksisterendeTyper.contains(akt))
                .forEach(k -> opprettOppgaveEgenskap(oppgave, k, aktuelleEgenskaper.getSaksbehandlerForTotrinn()));
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

    private static List<AndreKriterierType> typer(List<OppgaveEgenskap> eksisterendeOppgaveEgenskaper) {
        return eksisterendeOppgaveEgenskaper.stream()
                .map(OppgaveEgenskap::getAndreKriterierType)
                .collect(Collectors.toList());
    }

}
