package no.nav.foreldrepenger.los.avdelingsleder.innlogget;

import java.util.Objects;

public record InnloggetNavAnsattDto(String brukernavn,
                                    String navn,
                                    boolean kanOppgavestyre) {

    private InnloggetNavAnsattDto(Builder builder) {
        this(builder.brukernavn, builder.navn, builder.kanOppgavestyre);
    }

    public static InnloggetNavAnsattDto ukjentNavAnsatt(String brukernavn, String navn) {
        return new InnloggetNavAnsattDto(brukernavn, navn, false);
    }


    @Override
    public String toString() {
        return "InnloggetNavAnsattDto{" + "kanOppgavestyre=" + kanOppgavestyre + '}';
    }

    public static class Builder {
        private final String brukernavn;
        private final String navn;
        private boolean kanOppgavestyre;

        public Builder(String brukernavn, String navn) {
            this.brukernavn = brukernavn;
            this.navn = navn;
        }

        public Builder kanOppgavestyre(boolean kanOppgavestyre) {
            this.kanOppgavestyre = kanOppgavestyre;
            return this;
        }

        public InnloggetNavAnsattDto build() {
            return new InnloggetNavAnsattDto(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InnloggetNavAnsattDto that = (InnloggetNavAnsattDto) o;
        return kanOppgavestyre == that.kanOppgavestyre
            && Objects.equals(navn, that.navn)
            && Objects.equals(brukernavn, that.brukernavn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brukernavn, navn, kanOppgavestyre);
    }
}
