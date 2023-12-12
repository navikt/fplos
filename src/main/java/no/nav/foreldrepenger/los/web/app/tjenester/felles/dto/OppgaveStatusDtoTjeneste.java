package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import static no.nav.foreldrepenger.los.felles.util.OptionalUtil.tryOrEmpty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

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
            var reservertAvNavn = reservasjon.getReservertAv().equalsIgnoreCase(flyttetAvIdent) ? flyttetAvNavn : hentNavn(
                reservasjon.getReservertAv());
            return OppgaveStatusDto.reservert(reservasjon, reservertAvNavn, flyttetAvNavn);
        }
        return OppgaveStatusDto.ikkeReservert();
    }

    private String hentNavn(String ident) {
        if (ident == null) {
            return null;
        }
        return tryOrEmpty(() -> ansattTjeneste.hentAnsattNavn(ident), "ldap").orElse("Ukjent");
    }

    private OppgaveStatusDto systembrukerSpesialTilfelle(Reservasjon reservasjon) {
        // hack for å forskjønne visning av systembrukers navn i frontend
        var flyttetReservasjonDto = new FlyttetReservasjonDto(reservasjon.getFlyttetTidspunkt(), "Fplos", "oppgavesystem",
            reservasjon.getBegrunnelse());
        return OppgaveStatusDto.reservert(reservasjon, hentNavn(reservasjon.getReservertAv()), flyttetReservasjonDto);
    }
}
