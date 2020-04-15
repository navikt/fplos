package no.nav.foreldrepenger.loslager.oppgave;

public enum OppgaveEventType {
    OPPRETTET, LUKKET, VENT, MANU_VENT, GJENAPNET;

    public boolean erÅpningsevent() {
        return this.equals(OPPRETTET) || this.equals(GJENAPNET);
    }

    public boolean erVenteEvent() {
        return this.equals(VENT) || this.equals(MANU_VENT);
    }
}
