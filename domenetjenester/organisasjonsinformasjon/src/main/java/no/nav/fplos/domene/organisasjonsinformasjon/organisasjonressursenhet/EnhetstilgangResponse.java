package no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
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
