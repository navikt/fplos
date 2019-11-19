package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.fplos.kodeverk.Kodeliste;
import no.nav.fplos.kodeverk.KodeverkRepository;

@ApplicationScoped
class HentKodeverkTjenesteImpl implements HentKodeverkTjeneste {

    private KodeverkRepository kodeverkRepository;

    private static List<Class<? extends Kodeliste>> KODEVERK_SOM_BRUKES_PÅ_KLIENT = Arrays.asList(
            KøSortering.class,
            FagsakStatus.class,
            AndreKriterierType.class
    );

    public HentKodeverkTjenesteImpl() {
        // For CDI
    }

    @Inject
    public HentKodeverkTjenesteImpl(KodeverkRepository kodeverkRepository) {
        Objects.requireNonNull(kodeverkRepository, "kodeverkRepository"); //$NON-NLS-1$
        this.kodeverkRepository = kodeverkRepository;
    }


    @Override
    public Map<String, List<Kodeliste>> hentGruppertKodeliste() {
        Map<String, List<Kodeliste>> klientKoder = new HashMap<>();
        KODEVERK_SOM_BRUKES_PÅ_KLIENT.forEach(k -> {
            //TODO (TOR) Kjører repository-kall for kvar kodeliste. Er nok ikkje naudsynt
            List<Kodeliste> filtrertKodeliste = kodeverkRepository.hentAlle(k).stream()
                    .filter(ads -> !"-".equals(ads.getKode()))
                    .collect(toList());
            klientKoder.put(k.getSimpleName(), filtrertKodeliste);
        });

        return klientKoder;
    }
}
