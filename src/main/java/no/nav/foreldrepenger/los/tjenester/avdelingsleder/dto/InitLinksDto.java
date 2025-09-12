package no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto;

import no.nav.foreldrepenger.los.avdelingsleder.innlogget.InnloggetNavAnsattDto;

import java.util.List;


public record InitLinksDto(InnloggetNavAnsattDto innloggetBruker, List<AvdelingDto> avdelinger) {
}
