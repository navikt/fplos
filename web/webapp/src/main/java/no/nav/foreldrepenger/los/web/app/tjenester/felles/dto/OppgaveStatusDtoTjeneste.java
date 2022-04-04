package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static no.nav.foreldrepenger.los.felles.util.OptionalUtil.tryOrEmpty;

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
            var reservasjon = oppgave.getReservasjon();
            var flyttetAvIdent = reservasjon.getFlyttetAv().orElse(null);
            var flyttetAvNavn = hentNavn(flyttetAvIdent);
            var reservertAvNavn = reservasjon.getReservertAv().equalsIgnoreCase(flyttetAvIdent)
                    ? flyttetAvNavn
                    : hentNavn(reservasjon.getReservertAv());
            return OppgaveStatusDto.reservert(reservasjon, reservertAvNavn, flyttetAvNavn);
        }
        return OppgaveStatusDto.ikkeReservert();
    }

    private String hentNavn(String ident) {
        if (ident == null) {
            return null;
        } else if ("SRVFPLOS".equalsIgnoreCase(ident)) {
            return "Fplos";
        }
        return tryOrEmpty(() -> ansattTjeneste.hentAnsattNavn(ident))
                .orElse("Ukjent");
    }
}
