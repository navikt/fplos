package no.nav.foreldrepenger.los.avdelingsleder.innlogget;

import jakarta.validation.constraints.NotNull;

public record InnloggetNavAnsattDto(String brukernavn,
                                    @NotNull String navn,
                                    boolean kanOppgavestyre) {

    public static InnloggetNavAnsattDto ukjentNavAnsatt(String brukernavn, String navn) {
        return new InnloggetNavAnsattDto(brukernavn, navn, false);
    }

}
