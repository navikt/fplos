package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import no.nav.foreldrepenger.los.felles.util.validering.ValidKodeverk;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;


public class SakslisteFagsakYtelseTyperDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @Valid
    @NotNull
    private @ValidKodeverk FagsakYtelseType fagsakYtelseType;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    private boolean checked;

    public SakslisteFagsakYtelseTyperDto() {
    }

    public SakslisteFagsakYtelseTyperDto(SakslisteIdDto sakslisteId,
                                         FagsakYtelseType fagsakYtelseType,
                                         AvdelingEnhetDto avdelingEnhet,
                                         boolean checked) {
        this.sakslisteId = sakslisteId;
        this.fagsakYtelseType = fagsakYtelseType;
        this.avdelingEnhet = avdelingEnhet;
        this.checked = checked;
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

    public boolean isChecked() {
        return checked;
    }

    @Override
    public String toString() {
        return "SakslisteFagsakYtelseTyperDto{" + "sakslisteId=" + sakslisteId + ", fagsakYtelseTyper=" + fagsakYtelseType + ", avdelingEnhet="
            + avdelingEnhet + '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());

    }
}

