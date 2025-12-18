package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;

@ApplicationScoped
@Transactional
public class OppgaveEgenskapHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveEgenskapHåndterer.class);

    private Beskyttelsesbehov beskyttelsesbehov;

    public OppgaveEgenskapHåndterer() {
        // for cdi
    }

    @Inject
    public OppgaveEgenskapHåndterer(Beskyttelsesbehov beskyttelsesbehov) {
        this.beskyttelsesbehov = beskyttelsesbehov;
    }

    public void håndterOppgaveEgenskaper(Oppgave oppgave, OppgaveEgenskapFinner aktuelleEgenskaper) {
        var andreKriterier = new ArrayList<>(aktuelleEgenskaper.getAndreKriterier());
        andreKriterier.addAll(beskyttelsesbehov.getBeskyttelsesKriterier(oppgave.getSaksnummer()));
        LOG.info("Legger på oppgaveegenskaper {}", andreKriterier);

        var ønskedeEgenskaper = andreKriterier.stream()
            .map(ak -> lagOppgaveEgenskap(ak, aktuelleEgenskaper))
            .collect(Collectors.toSet());

        oppgave.tilbakestillOppgaveEgenskaper();
        ønskedeEgenskaper.forEach(oppgave::leggTilOppgaveEgenskap);
    }

    private static OppgaveEgenskap lagOppgaveEgenskap(AndreKriterierType andreKriterierType, OppgaveEgenskapFinner aktuelleEgenskaper) {
        var builder = OppgaveEgenskap.builder().medAndreKriterierType(andreKriterierType);
        if (andreKriterierType.erTilBeslutter()) {
            builder.medSisteSaksbehandlerForTotrinn(aktuelleEgenskaper.getSaksbehandlerForTotrinn());
        }
        return builder.build();
    }

}
