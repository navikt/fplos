package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;

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
    public OppgaveEgenskapHåndterer(OppgaveRepository oppgaveRepository, Beskyttelsesbehov beskyttelsesbehov) {
        this.repository = oppgaveRepository;
        this.beskyttelsesbehov = beskyttelsesbehov;
    }

    public void håndterOppgaveEgenskaper(Oppgave oppgave, OppgaveEgenskapFinner aktuelleEgenskaper) {
        var andreKriterier = new ArrayList<>(aktuelleEgenskaper.getAndreKriterier());
        andreKriterier.addAll(beskyttelsesbehov.getBeskyttelsesKriterier(oppgave));
        LOG.info("Legger på oppgaveegenskaper {}", andreKriterier);
        var eksisterendeOppgaveEgenskaper = repository.hentOppgaveEgenskaper(oppgave.getId());

        // slett uaktuelle eksisterende
        eksisterendeOppgaveEgenskaper.stream()
            .filter(akt -> !andreKriterier.contains(akt.getAndreKriterierType()) || !akt.getAktiv())
            .forEach(repository::slett);

        var eksisterendeTyper = eksisterendeOppgaveEgenskaper.stream().map(OppgaveEgenskap::getAndreKriterierType).toList();
        for (var type : andreKriterier) {
            if (!eksisterendeTyper.contains(type)) {
                var builder = OppgaveEgenskap.builder().medOppgave(oppgave).medAndreKriterierType(type);
                if (type.erTilBeslutter()) {
                    builder.medSisteSaksbehandlerForTotrinn(aktuelleEgenskaper.getSaksbehandlerForTotrinn());
                }
                repository.lagre(builder.build());
            }
        }
    }

}
