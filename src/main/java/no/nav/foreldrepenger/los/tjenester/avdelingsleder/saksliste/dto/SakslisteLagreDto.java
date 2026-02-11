package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import no.nav.foreldrepenger.los.felles.util.validering.ValidKodeverk;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Periodefilter;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

public record SakslisteLagreDto(@NotNull @Pattern(regexp = InputValideringRegex.FRITEKST) String avdelingEnhet,
                                @NotNull @Digits(integer = 18, fraction = 0) Long sakslisteId,
                                @NotNull @Pattern(regexp = InputValideringRegex.FRITEKST) String navn,
                                @NotNull @Valid SorteringDto sortering,
                                @Size(max = 20) Set<@ValidKodeverk @NotNull BehandlingType> behandlingTyper,
                                @Size(max = 20) Set<@ValidKodeverk @NotNull FagsakYtelseType> fagsakYtelseTyper,
                                @Valid @NotNull AndreKriterieDto andreKriterie) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet);
    }

    public record SorteringDto(@NotNull @ValidKodeverk KøSortering sorteringType,
                               @Valid @NotNull Periodefilter periodefilter,
                               @Min(-500) @Max(10_000_000) Long fra,
                               @Min(-500) @Max(10_000_000) Long til,
                               LocalDate fomDato,
                               LocalDate tomDato) {
    }

    public record AndreKriterieDto(@Size(max = 50) Set<@ValidKodeverk @NotNull AndreKriterierType> inkluder, @Size(max = 50) Set<@ValidKodeverk @NotNull AndreKriterierType> ekskluder) {
    }
}
