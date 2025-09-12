package no.nav.foreldrepenger.los.avdelingsleder.innlogget;

public record InnloggetNavAnsatt(String brukernavn, String navn) {

    @Override
    public String toString() {
        return "InnloggetNavAnsatt{}";
    }

}
