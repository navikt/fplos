package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class OppgaveStatusDtoTjeneste {

    private AnsattTjeneste ansattTjeneste;

    @Inject
    public OppgaveStatusDtoTjeneste(AnsattTjeneste ansattTjeneste) {
        this.ansattTjeneste = ansattTjeneste;
    }

    OppgaveStatusDtoTjeneste() {
        //CDI
    }

    OppgaveStatusDto lagStatusFor(Oppgave oppgave) {
        if (oppgave.harAktivReservasjon()) {
            var flyttetAv = hentFlyttetAv(oppgave.getReservasjon()).orElse(null);
            var reservertAv = oppgave.getReservasjon().getReservertAv();
            return OppgaveStatusDto.reservert(oppgave.getReservasjon(), hentNavn(reservertAv), flyttetAv);
        }
        return OppgaveStatusDto.ikkeReservert();
    }

    private Optional<String> hentFlyttetAv(Reservasjon reservasjon) {
        var flyttetAv = reservasjon.getFlyttetAv();
        return flyttetAv.map(this::hentNavn);
    }

    private String hentNavn(String saksbehandlerIdent) {
        return ansattTjeneste.hentAnsattNavn(saksbehandlerIdent);
    }
}
