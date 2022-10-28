package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SakslisteOgAvdelingDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteOgAvdelingDto() {
    }

    public SakslisteOgAvdelingDto(SakslisteIdDto sakslisteId, AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.avdelingEnhet = avdelingEnhet;
    }

    public SakslisteIdDto getSakslisteId() {
        return sakslisteId;
    }

    public AvdelingEnhetDto getAvdelingEnhet() { return avdelingEnhet; }

    @Override
    public String toString() {
        return "SakslisteOgAvdelingDto{" +
                "sakslisteId='" + sakslisteId + '\'' +
                "avdelingEnhet='" + avdelingEnhet + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());
    }
}
