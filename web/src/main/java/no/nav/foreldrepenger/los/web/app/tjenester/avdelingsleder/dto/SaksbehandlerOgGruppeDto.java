package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public record SaksbehandlerOgGruppeDto(@NotNull @Valid SaksbehandlerBrukerIdentDto brukerIdent,
                                       @NotNull @Valid AvdelingEnhetDto avdelingEnhet,
                                       @Min(1) @Max(Integer.MAX_VALUE) int gruppeId) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());
    }
}
