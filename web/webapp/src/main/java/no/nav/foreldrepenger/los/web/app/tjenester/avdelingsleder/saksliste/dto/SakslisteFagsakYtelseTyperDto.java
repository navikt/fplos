package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.felles.util.validering.ValidKodeverk;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import java.util.List;


public class SakslisteFagsakYtelseTyperDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @Valid
    @NotNull
    private List<@ValidKodeverk FagsakYtelseType> fagsakYtelseTyper;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteFagsakYtelseTyperDto() {
    }

    public SakslisteFagsakYtelseTyperDto(SakslisteIdDto sakslisteId,
                                         List<FagsakYtelseType> fagsakYtelseTyper,
                                         AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.fagsakYtelseTyper = fagsakYtelseTyper;
        this.avdelingEnhet = avdelingEnhet;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public List<FagsakYtelseType> getFagsakYtelseTyper() {
        return fagsakYtelseTyper;
    }

    public AvdelingEnhetDto getAvdelingEnhet() {
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "SakslisteFagsakYtelseTyperDto{" +
                "sakslisteId=" + sakslisteId +
                ", fagsakYtelseTyper=" + fagsakYtelseTyper +
                ", avdelingEnhet=" + avdelingEnhet +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett()
                .leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());

    }
}

