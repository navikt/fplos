package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.List;

import no.nav.foreldrepenger.los.domene.typer.aktør.OrganisasjonsEnhet;

class EnhetstilgangResponse {
    private List<OrganisasjonsEnhet> enheter;

    void setEnheter(List<OrganisasjonsEnhet> enheter) {
        this.enheter = enheter;
    }

    List<OrganisasjonsEnhet> getEnheter() {
        return enheter;
    }

}
