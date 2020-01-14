package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.app;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Kodeverdi;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Map;

import static java.util.Arrays.asList;

@ApplicationScoped
class HentKodeverkTjenesteImpl implements HentKodeverkTjeneste {

    private static Map<String, Collection<? extends Kodeverdi>> KODEVERK_ENUM = Map.of(
            BehandlingType.class.getSimpleName(), asList(BehandlingType.values()),
            FagsakYtelseType.class.getSimpleName(), asList(FagsakYtelseType.values()),
            KøSortering.class.getSimpleName(), asList(KøSortering.values()),
            FagsakStatus.class.getSimpleName(), asList(FagsakStatus.values()),
            AndreKriterierType.class.getSimpleName(), asList(AndreKriterierType.values()));

    public HentKodeverkTjenesteImpl() {
        // For CDI
    }

    @Override
    public Map<String, Collection<? extends Kodeverdi>> hentGruppertKodeliste() {
        return KODEVERK_ENUM;
    }
}
