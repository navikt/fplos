package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.app;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.fplos.kodeverk.Kodeverdi;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Map;

@ApplicationScoped
class HentKodeverkTjenesteImpl implements HentKodeverkTjeneste {

    private static Map<String, Collection<? extends Kodeverdi>> KODEVERK_ENUM = Map.of(
            BehandlingType.class.getSimpleName(), BehandlingType.getEnums(),
            FagsakYtelseType.class.getSimpleName(), FagsakYtelseType.getEnums(),
            KøSortering.class.getSimpleName(), KøSortering.getEnums(),
            FagsakStatus.class.getSimpleName(), FagsakStatus.getEnums(),
            AndreKriterierType.class.getSimpleName(), AndreKriterierType.getEnums());

    public HentKodeverkTjenesteImpl() {
        // For CDI
    }

    @Override
    public Map<String, Collection<? extends Kodeverdi>> hentGruppertKodeliste() {
        return KODEVERK_ENUM;
    }


//    @Override
//    public Map<String, List<Kodeliste>> hentGruppertKodeliste() {
//        Map<String, List<Kodeliste>> klientKoder = new HashMap<>();
//        KODEVERK_SOM_BRUKES_PÅ_KLIENT.forEach(k -> {
//            //TODO (TOR) Kjører repository-kall for kvar kodeliste. Er nok ikkje naudsynt
//            List<Kodeliste> filtrertKodeliste = kodeverkRepository.hentAlle(k).stream()
//                    .filter(ads -> !"-".equals(ads.getKode()))
//                    .collect(toList());
//            klientKoder.put(k.getSimpleName(), filtrertKodeliste);
//        });
//
//        return klientKoder;
//    }
}
