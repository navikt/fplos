package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.app;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Kodeverdi;
import no.nav.vedtak.util.env.Cluster;
import no.nav.vedtak.util.env.Environment;

// enkel toggle-mekanisme som alternativ til unleash
@ApplicationScoped
public class KodeverkFeatureToggle {

    private static final Map<? extends Kodeverdi, Set<Cluster>> ekskludertKodeverdi = new HashMap<>() {{
        put(AndreKriterierType.ENDRINGSSØKNAD, Set.of(Cluster.PROD_FSS, Cluster.DEV_FSS, Cluster.LOCAL));
    }};

    public static <T extends Kodeverdi> boolean skalEkskludereFraFrontend(T input) {
        return Optional.ofNullable(ekskludertKodeverdi.get(input))
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(e -> e.equals(aktivtCluster()));
    }

    private static Cluster aktivtCluster() {
        return Environment.current().getCluster();
    }
}
