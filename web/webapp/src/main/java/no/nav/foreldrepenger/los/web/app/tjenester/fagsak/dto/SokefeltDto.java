package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.dto;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

public class SokefeltDto implements AbacDto {

    @NotNull
    @Digits(integer = 18, fraction = 0)
    private String searchString;

    @SuppressWarnings("unused")
    private SokefeltDto() { // NOSONAR
    }

    public SokefeltDto(String searchString) {
        this.searchString = searchString;
    }

    public SokefeltDto(Saksnummer saksnummer) {
        this.searchString = saksnummer.getVerdi();
    }

    public String getSearchString() {
        return searchString;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        AbacDataAttributter attributter = AbacDataAttributter.opprett();
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
