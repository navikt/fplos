package no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;

import java.util.List;

public interface OrganisasjonRessursEnhetTjeneste {

    List<OrganisasjonsEnhet> hentEnhetListe(String ressursId);

}
