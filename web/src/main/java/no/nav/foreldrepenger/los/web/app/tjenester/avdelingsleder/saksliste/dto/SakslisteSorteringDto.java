package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.los.felles.util.validering.ValidKodeverk;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

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

    public AvdelingEnhetDto getAvdelingEnhet() {
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "SakslisteSorteringDto{" + "sakslisteId='" + sakslisteId + '\'' + "sakslisteSorteringValg='" + sakslisteSorteringValg + '\'' + '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());

    }
}
