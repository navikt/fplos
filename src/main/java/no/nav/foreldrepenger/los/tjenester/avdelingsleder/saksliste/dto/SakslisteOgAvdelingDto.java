package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto;

import no.nav.foreldrepenger.los.oppgave.Periodefilter;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class SakslisteOgAvdelingDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    @Valid
    private Periodefilter periodefilter;

    public SakslisteIdDto getSakslisteId() {
        return sakslisteId;
    }

    public AvdelingEnhetDto getAvdelingEnhet() {
        return avdelingEnhet;
    }

    public Periodefilter getPeriodeFilterType() {
        return periodefilter;
    }

    @Override
    public String toString() {
        return "SakslisteOgAvdelingDto{" + "sakslisteId=" + sakslisteId + ", avdelingEnhet=" + avdelingEnhet + ", periodefilter="
            + periodefilter + '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());
    }
}
