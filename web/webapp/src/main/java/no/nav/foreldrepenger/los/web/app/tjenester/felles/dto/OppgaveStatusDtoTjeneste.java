package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.fplos.ansatt.AnsattTjeneste;

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
        return flyttetAv.map(saksbehandler -> hentNavn(saksbehandler));
    }

    private String hentNavn(String saksbehandlerIdent) {
        return ansattTjeneste.hentAnsattNavn(saksbehandlerIdent);
    }
}
