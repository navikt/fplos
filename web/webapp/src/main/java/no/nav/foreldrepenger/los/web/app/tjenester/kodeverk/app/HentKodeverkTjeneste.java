package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.app;

import java.util.List;
import java.util.Map;

import no.nav.fplos.kodeverk.Kodeliste;

public interface HentKodeverkTjeneste {

    Map<String, List<Kodeliste>> hentGruppertKodeliste();
}
