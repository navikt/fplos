package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class SakslisteSorteringIntervallDagerDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @Valid
    @Min(0)
    @Max(5000)
    private Long fra;

    @Valid
    @Min(0)
    @Max(5000)
    private Long til;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteSorteringIntervallDagerDto() {
    }

    public SakslisteSorteringIntervallDagerDto(SakslisteIdDto sakslisteId, Long fra, Long til, AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.fra = fra;
        this.til = til;
        this.avdelingEnhet = avdelingEnhet;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public Long getFra() {
        return fra;
    }

    public Long getTil() {
        return til;
    }

    public AvdelingEnhetDto getAvdelingEnhet(){
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "SakslisteSorteringDto{" +
                "sakslisteId='" + sakslisteId + '\'' +
                "fra='" + fra + '\'' +
                "til='" + til + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());

    }
}
