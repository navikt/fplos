package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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

    public AvdelingEnhetDto getAvdelingEnhet(){
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
        if (this == o) return true;
        if (!(o instanceof SakslisteNavnDto)) return false;

        SakslisteNavnDto sakslisteDto = (SakslisteNavnDto) o;
        return sakslisteId.getVerdi().equals(sakslisteDto.sakslisteId.getVerdi());
    }

    @Override
    public int hashCode() {
        return 31 * sakslisteId.hashCode();
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTilOppgavestyringEnhet(avdelingEnhet.getAvdelingEnhet());
    }
}
