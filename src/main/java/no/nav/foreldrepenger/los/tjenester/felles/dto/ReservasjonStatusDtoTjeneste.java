package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

@ApplicationScoped
public class ReservasjonStatusDtoTjeneste {

    private static final String SYSTEMBRUKER = "SRVFPLOS";
    private OrganisasjonRepository organisasjonRepository;

    @Inject
    public ReservasjonStatusDtoTjeneste(OrganisasjonRepository organisasjonRepository) {
        this.organisasjonRepository = organisasjonRepository;
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
        return Optional.ofNullable(ident)
            .flatMap(organisasjonRepository::hentSaksbehandlerHvisEksisterer)
            .map(Saksbehandler::getNavn).orElse("Ukjent");
    }

    private ReservasjonStatusDto systembrukerSpesialTilfelle(Reservasjon reservasjon) {
        // forskjønne visning av systembrukers navn i frontend
        var flyttetReservasjonDto = new FlyttetReservasjonDto(reservasjon.getFlyttetTidspunkt(), "Fplos", "oppgavesystem",
            reservasjon.getBegrunnelse());
        return ReservasjonStatusDto.reservert(reservasjon, hentNavn(reservasjon.getReservertAv()), flyttetReservasjonDto);
    }
}
