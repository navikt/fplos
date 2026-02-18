package no.nav.foreldrepenger.los.avdelingsleder.innlogget;

public record InnloggetNavAnsattDto(String brukernavn,
                                    String navn,
                                    boolean kanOppgavestyre) {

    public static InnloggetNavAnsattDto ukjentNavAnsatt(String brukernavn, String navn) {
        return new InnloggetNavAnsattDto(brukernavn, navn, false);
    }

}
