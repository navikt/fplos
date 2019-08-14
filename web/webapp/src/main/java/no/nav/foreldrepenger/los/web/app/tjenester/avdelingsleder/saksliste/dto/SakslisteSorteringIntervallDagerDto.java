package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SakslisteSorteringIntervallDagerDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @Valid
    private Long fomDager;

    @Valid
    private Long tomDager;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteSorteringIntervallDagerDto() {
    }

    public SakslisteSorteringIntervallDagerDto(SakslisteIdDto sakslisteId, Long fomDager, Long tomDager, AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.fomDager = fomDager;
        this.tomDager = tomDager;
        this.avdelingEnhet = avdelingEnhet;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public Long getFomDager() {
        return fomDager;
    }

    public Long getTomDager() {
        return tomDager;
    }

    public AvdelingEnhetDto getAvdelingEnhet(){
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "SakslisteSorteringDto{" +
                "sakslisteId='" + sakslisteId + '\'' +
                "fomDager='" + fomDager + '\'' +
                "tomDager='" + tomDager + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTilOppgavestyringEnhet(avdelingEnhet.getAvdelingEnhet());
    }
}