package no.nav.fplos.kafkatjenester.eventresultat;

import java.util.List;

import no.nav.foreldrepenger.loslager.hendelse.Aksjonspunkt;

public class TilbakekrevingEventMapper {

    public static EventResultat tilbakekrevingEventFra(List<Aksjonspunkt> aksjonspunkter) {
        if (aktivManuellVent(aksjonspunkter)) {
            return EventResultat.LUKK_OPPGAVE_MANUELT_VENT;
        } else if (harAksjonspunktMedStatusOpprettet(aksjonspunkter)) {
            return EventResultat.OPPRETT_OPPGAVE;
        }
        return EventResultat.LUKK_OPPGAVE;
    }

    private static boolean harAksjonspunktMedStatusOpprettet(List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream().anyMatch(a -> a.erOpprettet());
    }

    private static boolean aktivManuellVent(List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream().anyMatch(a -> List.of("7001", "7002").contains(a.getKode()) && a.erOpprettet());
    }
}
