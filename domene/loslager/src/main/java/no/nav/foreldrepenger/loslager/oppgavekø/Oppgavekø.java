package no.nav.foreldrepenger.loslager.oppgavekø;

public class Oppgavekø {
    private final Long id;

    public Oppgavekø(Long id) {
        this.id = id;
    }

    public Long asLong() {
        return id;
    }
}
