package no.nav.foreldrepenger.los.organisasjon.ansatt;

import no.nav.foreldrepenger.los.domene.typer.aktør.OrganisasjonsEnhet;

import java.util.List;

class EnhetstilgangResponse {
    private List<OrganisasjonsEnhet> enheter;

    void setEnheter(List<OrganisasjonsEnhet> enheter) {
        this.enheter = enheter;
    }

    List<OrganisasjonsEnhet> getEnheter() {
        return enheter;
    }

}
