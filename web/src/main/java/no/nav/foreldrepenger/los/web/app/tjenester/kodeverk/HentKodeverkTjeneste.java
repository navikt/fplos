package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.los.felles.Kodeverdi;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakStatus;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.BehandlingVenteStatus;

@ApplicationScoped
class HentKodeverkTjeneste {

    private static final Map<String, Collection<Kodeverdi>> KODEVERK = Map.of(
            BehandlingType.class.getSimpleName(), List.of(BehandlingType.values()),
            FagsakYtelseType.class.getSimpleName(), List.of(FagsakYtelseType.values()),
            KøSortering.class.getSimpleName(), List.of(KøSortering.values()),
            FagsakStatus.class.getSimpleName(), List.of(FagsakStatus.values()),
            AndreKriterierType.class.getSimpleName(), List.of(AndreKriterierType.values()),
            BehandlingVenteStatus.class.getSimpleName(), List.of(BehandlingVenteStatus.values()));


    HentKodeverkTjeneste() {
        // cdi
    }

    Map<String, Collection<Kodeverdi>> hentGruppertKodeliste() {
        return KODEVERK;
    }
}
