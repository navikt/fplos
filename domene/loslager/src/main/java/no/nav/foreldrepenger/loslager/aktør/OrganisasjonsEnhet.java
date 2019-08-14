package no.nav.foreldrepenger.loslager.aktør;

public class OrganisasjonsEnhet {

    private String enhetId;
    private String enhetNavn;

    public OrganisasjonsEnhet(String enhetId, String enhetNavn) {
        this.enhetId = enhetId;
        this.enhetNavn = enhetNavn;
    }

    public String getEnhetId() { return enhetId; }

    public String getEnhetNavn(){ return enhetNavn; }
}
