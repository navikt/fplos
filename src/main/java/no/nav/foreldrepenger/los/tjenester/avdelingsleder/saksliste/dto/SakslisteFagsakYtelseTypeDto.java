package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import no.nav.foreldrepenger.los.felles.util.validering.ValidKodeverk;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class SakslisteFagsakYtelseTypeDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @ValidKodeverk
    private FagsakYtelseType fagsakYtelseType;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteFagsakYtelseTypeDto() {
    }

    public SakslisteFagsakYtelseTypeDto(SakslisteIdDto sakslisteId, FagsakYtelseType fagsakYtelseType, AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.fagsakYtelseType = fagsakYtelseType;
        this.avdelingEnhet = avdelingEnhet;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public AvdelingEnhetDto getAvdelingEnhet() {
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "<id=" + sakslisteId + //$NON-NLS-1$
            ", fagsakYtelseType=" + fagsakYtelseType + //$NON-NLS-1$
            ", avdelingEnhet=" + avdelingEnhet + //$NON-NLS-1$
            ">";
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());
    }
}
