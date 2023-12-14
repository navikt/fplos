package no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto;

import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class SaksbehandlerOgAvdelingDto implements AbacDto {

    @NotNull
    @Valid
    private SaksbehandlerBrukerIdentDto brukerIdent;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SaksbehandlerOgAvdelingDto() {
    }

    public SaksbehandlerOgAvdelingDto(SaksbehandlerBrukerIdentDto brukerIdent, AvdelingEnhetDto avdelingEnhet) {
        this.brukerIdent = brukerIdent;
        this.avdelingEnhet = avdelingEnhet;
    }

    public SaksbehandlerBrukerIdentDto getBrukerIdent() {
        return brukerIdent;
    }

    public AvdelingEnhetDto getAvdelingEnhet() {
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "SaksbehandlerOgAvdelingDto{" + "brukerIdent=" + brukerIdent + ", avdelingEnhet=" + avdelingEnhet + '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());

    }
}
