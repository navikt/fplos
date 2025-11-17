package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.Arrays;
import java.util.List;

import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

public record KøSorteringFeltDto(KøSortering køSortering, KøSortering.FeltType feltType, KøSortering.FeltKategori feltKategori) {

    private static final List<KøSorteringFeltDto> ALLE = Arrays.stream(KøSortering.values())
        .map(ks -> new KøSorteringFeltDto(ks, ks.getFelttype(), ks.getFeltkategori()))
        .toList();

    public static List<KøSorteringFeltDto> alle() {
        return ALLE;
    }

}
