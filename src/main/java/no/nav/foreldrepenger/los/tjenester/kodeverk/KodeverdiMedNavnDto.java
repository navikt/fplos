package no.nav.foreldrepenger.los.tjenester.kodeverk;

import jakarta.validation.constraints.NotNull;

public record KodeverdiMedNavnDto(@NotNull String kode, @NotNull String navn) { }
