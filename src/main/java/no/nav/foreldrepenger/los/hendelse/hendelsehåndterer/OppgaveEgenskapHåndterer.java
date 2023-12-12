package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.oppgave.Oppgave;

@ApplicationScoped
@Transactional
public class OppgaveEgenskapHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveEgenskapHåndterer.class);

    private OppgaveRepository repository;
    private Beskyttelsesbehov beskyttelsesbehov;

    public OppgaveEgenskapHåndterer() {
        // for cdi
    }

    @Inject
    public OppgaveEgenskapHåndterer(OppgaveRepository oppgaveRepository,
                                    Beskyttelsesbehov beskyttelsesbehov) {
        this.repository = oppgaveRepository;
        this.beskyttelsesbehov = beskyttelsesbehov;
    }

    public void håndterOppgaveEgenskaper(Oppgave oppgave, OppgaveEgenskapFinner aktuelleEgenskaper) {
        var andreKriterier = new ArrayList<>(aktuelleEgenskaper.getAndreKriterier());
        andreKriterier.addAll(beskyttelsesbehov.getBeskyttelsesKriterier(oppgave));
        LOG.info("Legger på oppgaveegenskaper {}", andreKriterier);
        var eksisterendeOppgaveEgenskaper = hentEksisterendeEgenskaper(oppgave);

        // deaktiver uaktuelle eksisterende
        eksisterendeOppgaveEgenskaper.stream().filter(akt -> !andreKriterier.contains(akt.getAndreKriterierType())).forEach(this::deaktiver);

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
        return Optional.ofNullable(repository.hentOppgaveEgenskaper(oppgave.getId())).orElse(Collections.emptyList());
    }

    private static List<AndreKriterierType> typer(List<OppgaveEgenskap> eksisterendeOppgaveEgenskaper) {
        return eksisterendeOppgaveEgenskaper.stream().map(OppgaveEgenskap::getAndreKriterierType).toList();
    }

}
