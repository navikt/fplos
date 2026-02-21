package no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.avdelingsleder.innlogget.InnloggetNavAnsattDto;

import java.util.List;


public record InitLinksDto(@NotNull InnloggetNavAnsattDto innloggetBruker, @NotNull List<AvdelingDto> avdelinger) {
}
