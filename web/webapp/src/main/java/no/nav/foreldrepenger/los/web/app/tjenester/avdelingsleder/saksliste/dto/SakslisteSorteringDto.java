package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.los.web.app.validering.ValidKodeverk;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SakslisteSorteringDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @NotNull
    @ValidKodeverk
    private KøSortering sakslisteSorteringValg;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteSorteringDto() {
    }

    public SakslisteSorteringDto(SakslisteIdDto sakslisteId, KøSortering sakslisteSorteringValg, AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.sakslisteSorteringValg = sakslisteSorteringValg;
        this.avdelingEnhet = avdelingEnhet;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public KøSortering getSakslisteSorteringValg() {
        return sakslisteSorteringValg;
    }

    public AvdelingEnhetDto getAvdelingEnhet(){
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "SakslisteSorteringDto{" +
                "sakslisteId='" + sakslisteId + '\'' +
                "sakslisteSorteringValg='" + sakslisteSorteringValg + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTilOppgavestyringEnhet(avdelingEnhet.getAvdelingEnhet());
    }
}