package no.nav.foreldrepenger.los.tjenester.felles.dto;

import static no.nav.foreldrepenger.los.felles.util.OptionalUtil.tryOrEmpty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.organisasjon.ansatt.BrukerProfil;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

@ApplicationScoped
public class ReservasjonStatusDtoTjeneste {

    private static final String SYSTEMBRUKER = "SRVFPLOS";
    private AnsattTjeneste ansattTjeneste;

    @Inject
    public ReservasjonStatusDtoTjeneste(AnsattTjeneste ansattTjeneste) {
        this.ansattTjeneste = ansattTjeneste;
    }

    ReservasjonStatusDtoTjeneste() {
        //CDI
    }

    ReservasjonStatusDto lagStatusFor(Oppgave oppgave) {
        if (oppgave.harAktivReservasjon()) {
            var reservasjon = oppgave.getReservasjon();
            if (SYSTEMBRUKER.equalsIgnoreCase(reservasjon.getFlyttetAv())) {
                return systembrukerSpesialTilfelle(reservasjon);
            }
            var flyttetAvIdent = reservasjon.getFlyttetAv();
            var flyttetAvNavn = hentNavn(reservasjon.getFlyttetAv());
            var reservertAvNavn = reservasjon.getReservertAv().equalsIgnoreCase(flyttetAvIdent) ? flyttetAvNavn : hentNavn(
                reservasjon.getReservertAv());
            return ReservasjonStatusDto.reservert(reservasjon, reservertAvNavn, flyttetAvNavn);
        }
        return ReservasjonStatusDto.ikkeReservert();
    }

    private String hentNavn(String ident) {
        if (ident == null) {
            return null;
        }
        return tryOrEmpty(() -> ansattTjeneste.hentBrukerProfil(ident), "brukerprofil")
            .map(BrukerProfil::navn)
            .orElse("Ukjent");
    }

    private ReservasjonStatusDto systembrukerSpesialTilfelle(Reservasjon reservasjon) {
        // forskjønne visning av systembrukers navn i frontend
        var flyttetReservasjonDto = new FlyttetReservasjonDto(reservasjon.getFlyttetTidspunkt(), "Fplos", "oppgavesystem",
            reservasjon.getBegrunnelse());
        return ReservasjonStatusDto.reservert(reservasjon, hentNavn(reservasjon.getReservertAv()), flyttetReservasjonDto);
    }
}
