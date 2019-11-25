package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.app;

import java.util.Collection;
import java.util.Map;

import no.nav.fplos.kodeverk.Kodeverdi;

public interface HentKodeverkTjeneste {

    Map<String, Collection<? extends Kodeverdi>> hentGruppertKodeliste();
}
