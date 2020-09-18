package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.app;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Kodeverdi;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ApplicationScoped
class HentKodeverkTjenesteImpl implements HentKodeverkTjeneste {

    private final static Map<String, Collection<? extends Kodeverdi>> KODEVERK = Map.of(
            BehandlingType.class.getSimpleName(), List.of(BehandlingType.values()),
            FagsakYtelseType.class.getSimpleName(), List.of(FagsakYtelseType.values()),
            KøSortering.class.getSimpleName(), List.of(KøSortering.values()),
            FagsakStatus.class.getSimpleName(), List.of(FagsakStatus.values()),
            AndreKriterierType.class.getSimpleName(), List.of(AndreKriterierType.values()));


    HentKodeverkTjenesteImpl() {
        // cdi
    }

    @Override
    public Map<String, Collection<? extends Kodeverdi>> hentGruppertKodeliste() {
        return KODEVERK;
    }
}
