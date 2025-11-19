package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

public record KøSorteringFeltDto(@NotNull KøSortering sorteringType, @NotNull String sorteringTittel,
                                 @NotNull KøSortering.FeltType feltType, @NotNull KøSortering.FeltKategori feltKategori) {

    private static final List<KøSorteringFeltDto> ALLE = Arrays.stream(KøSortering.values())
        .map(ks -> new KøSorteringFeltDto(ks, ks.getNavn(), ks.getFelttype(), ks.getFeltkategori()))
        .toList();

    public static List<KøSorteringFeltDto> alle() {
        return ALLE;
    }

}
