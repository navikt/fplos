package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import no.nav.foreldrepenger.los.oppgave.Periodefilter;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class SakslisteSorteringIntervallDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @Valid
    @Min(-500)
    @Max(10_000_000)
    private Long fra;

    @Valid
    @Min(-500)
    @Max(10_000_000)
    private Long til;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    @Valid
    private Periodefilter periodefilter;

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public Long getFra() {
        return fra;
    }

    public Long getTil() {
        return til;
    }

    public AvdelingEnhetDto getAvdelingEnhet() {
        return avdelingEnhet;
    }

    public Periodefilter getPeriodefilter() {
        return periodefilter;
    }

    @Override
    public String toString() {
        return "SakslisteSorteringIntervallDto{" + "sakslisteId=" + sakslisteId + ", fra=" + fra + ", til=" + til + ", avdelingEnhet=" + avdelingEnhet
            + ", periodefilter=" + periodefilter + '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());

    }
}
