package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

public class SakslisteNavnDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @NotNull
    @Size(max = 100)
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private String navn;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteNavnDto() {
    }

    public SakslisteNavnDto(SakslisteIdDto sakslisteId, String navn, AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.navn = navn;
        this.avdelingEnhet = avdelingEnhet;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public String getNavn() {
        return navn;
    }

    public AvdelingEnhetDto getAvdelingEnhet() {
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "<id=" + sakslisteId + //$NON-NLS-1$
            ", navn=" + navn + //$NON-NLS-1$
            ", avdelingEnhet=" + avdelingEnhet + //$NON-NLS-1$
            ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SakslisteNavnDto)) {
            return false;
        }

        var sakslisteDto = (SakslisteNavnDto) o;
        return sakslisteId.getVerdi().equals(sakslisteDto.sakslisteId.getVerdi());
    }

    @Override
    public int hashCode() {
        return 31 * sakslisteId.hashCode();
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());
    }
}
