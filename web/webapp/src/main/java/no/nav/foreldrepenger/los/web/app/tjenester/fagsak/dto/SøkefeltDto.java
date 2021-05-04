package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public class SøkefeltDto implements AbacDto {

    @NotNull
    @Digits(integer = 18, fraction = 0)
    private String searchString;

    @SuppressWarnings("unused")
    private SøkefeltDto() { // NOSONAR
    }

    public SøkefeltDto(String searchString) {
        this.searchString = searchString;
    }

    public SøkefeltDto(Saksnummer saksnummer) {
        this.searchString = saksnummer.value();
    }

    public String getSearchString() {
        return searchString;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        var attributter = AbacDataAttributter.opprett();
        if (searchString.length() == 11 /* guess - fødselsnummer */) {
            attributter
                    .leggTil(StandardAbacAttributtType.FNR, searchString)
                    .leggTil(FplosAbacAttributtType.SAKER_MED_FNR, searchString);
        } else {
            attributter.leggTil(StandardAbacAttributtType.SAKSNUMMER, searchString);
        }
        return attributter;
    }

}
