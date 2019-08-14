package no.nav.foreldrepenger.loslager.aktør;

public enum NavBrukerKjønn {
    K("kvinne"),
    M("Mann");

    private final String beskrivelse;

    NavBrukerKjønn(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

}
