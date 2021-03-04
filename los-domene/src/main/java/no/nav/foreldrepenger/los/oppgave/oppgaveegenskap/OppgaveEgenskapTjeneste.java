package no.nav.foreldrepenger.los.oppgave.oppgaveegenskap;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OppgaveEgenskapTjeneste {
    private OppgaveRepository oppgaveRepository;

    @Inject
    public OppgaveEgenskapTjeneste(OppgaveRepository oppgaveRepository) {
        this.oppgaveRepository = oppgaveRepository;
    }

    public OppgaveEgenskapTjeneste() { }

    public void aktiverOppgaveEgenskap(Oppgave oppgave, AndreKriterierType andreKriterierType) {
        var eksisterendeOppgaveegenskap = oppgaveRepository.hentOppgaveEgenskaper(oppgave.getId()).stream()
                .filter(akt -> akt.getAndreKriterierType().equals(andreKriterierType))
                .findFirst();
        eksisterendeOppgaveegenskap.ifPresentOrElse(this::aktiver, () -> opprett(oppgave, andreKriterierType));
    }

    public void deaktiverOppgaveEgenskap(Oppgave oppgave, AndreKriterierType andreKriterierType) {
        var eksisterendeOppgaveegenskap = oppgaveRepository.hentOppgaveEgenskaper(oppgave.getId()).stream()
                .filter(akt -> akt.getAndreKriterierType().equals(andreKriterierType))
                .findFirst();
        eksisterendeOppgaveegenskap.ifPresent(oe -> {
            oe.deaktiverOppgaveEgenskap();
            oppgaveRepository.lagre(oe);
        });
    }

    private void opprett(Oppgave oppgave, AndreKriterierType andreKriterierType) {
        var oe = new OppgaveEgenskap(oppgave, andreKriterierType);
        oppgaveRepository.lagre(oe);
    }

    private void aktiver(OppgaveEgenskap oppgaveEgenskap) {
        oppgaveEgenskap.aktiverOppgaveEgenskap();
        oppgaveRepository.lagre(oppgaveEgenskap);
    }
}

