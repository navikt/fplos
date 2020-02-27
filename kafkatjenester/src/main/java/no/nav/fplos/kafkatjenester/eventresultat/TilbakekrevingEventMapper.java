package no.nav.fplos.kafkatjenester.eventresultat;

import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;

import java.util.Map;

public class TilbakekrevingEventMapper {

    public static EventResultat tilbakekrevingEventFra(TilbakebetalingBehandlingProsessEventDto dto) {
        var koder = dto.getAksjonspunktKoderMedStatusListe();
        if (aktivManuellVent(koder)) {
            return EventResultat.LUKK_OPPGAVE_MANUELT_VENT;
        } else if (koder.containsValue("OPPR")) {
            return EventResultat.OPPRETT_OPPGAVE;
        }
        return EventResultat.LUKK_OPPGAVE;
    }

    private static boolean aktivManuellVent(Map<String, String> koder) {
        boolean found = false;
        if (koder.containsKey("7001") && koder.get("7001").equals("OPPR")) found = true;
        if (koder.containsKey("7002") && koder.get("7002").equals("OPPR")) found = true;
        return found;
    }
}
