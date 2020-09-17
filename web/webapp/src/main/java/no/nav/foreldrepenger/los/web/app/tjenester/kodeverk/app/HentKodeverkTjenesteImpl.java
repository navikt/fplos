package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.app;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Kodeverdi;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
class HentKodeverkTjenesteImpl implements HentKodeverkTjeneste {

    private final static Map<String, Collection<? extends Kodeverdi>> KODEVERK = Map.of(
            BehandlingType.class.getSimpleName(), toggletListe(BehandlingType.values()),
            FagsakYtelseType.class.getSimpleName(), toggletListe(FagsakYtelseType.values()),
            KøSortering.class.getSimpleName(), toggletListe(KøSortering.values()),
            FagsakStatus.class.getSimpleName(), toggletListe(FagsakStatus.values()),
            AndreKriterierType.class.getSimpleName(), toggletListe(AndreKriterierType.values()));


    HentKodeverkTjenesteImpl() {
        // cdi
    }

    @Override
    public Map<String, Collection<? extends Kodeverdi>> hentGruppertKodeliste() {
        return KODEVERK;
    }

    @SafeVarargs
    private static <T extends Kodeverdi> List<T> toggletListe(T... kodeverdi) {
        return Arrays.stream(kodeverdi)
                .filter(v -> !KodeverkFeatureToggle.skalEkskludereFraFrontend(v))
                .collect(Collectors.toList());
    }
}
