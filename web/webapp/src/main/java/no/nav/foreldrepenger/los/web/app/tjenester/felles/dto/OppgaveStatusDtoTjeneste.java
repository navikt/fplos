package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static no.nav.foreldrepenger.los.felles.util.OptionalUtil.tryOrEmpty;

@ApplicationScoped
public class OppgaveStatusDtoTjeneste {

    private static final String SYSTEMBRUKER = "SRVFPLOS";
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
            if (SYSTEMBRUKER.equalsIgnoreCase(reservasjon.getFlyttetAv())) {
                return systembrukerSpesialTilfelle(reservasjon);
            }
            var flyttetAvIdent = reservasjon.getFlyttetAv();
            var flyttetAvNavn = hentNavn(reservasjon.getFlyttetAv());
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
        }
        return tryOrEmpty(() -> ansattTjeneste.hentAnsattNavn(ident))
                .orElse("Ukjent");
    }

    private OppgaveStatusDto systembrukerSpesialTilfelle(Reservasjon reservasjon) {
        // hack for å forskjønne visning av systembrukers navn i frontend
        reservasjon.setFlyttetAv("Fplos");
        return OppgaveStatusDto.reservert(reservasjon, hentNavn(reservasjon.getReservertAv()), "oppgavesystem");
    }
}
