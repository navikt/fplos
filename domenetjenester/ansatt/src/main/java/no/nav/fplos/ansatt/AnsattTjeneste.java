package no.nav.fplos.ansatt;

import java.util.List;

public interface AnsattTjeneste {

    String hentAnsattNavn(String ident);

    List<String> hentAvdelingerNavnForAnsatt(String ident);
}
