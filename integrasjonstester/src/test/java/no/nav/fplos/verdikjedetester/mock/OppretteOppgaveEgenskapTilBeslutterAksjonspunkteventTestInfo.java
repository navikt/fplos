package no.nav.fplos.verdikjedetester.mock;

import no.nav.vedtak.felles.integrasjon.kafka.EventHendelse;

import java.util.HashMap;

public class OppretteOppgaveEgenskapTilBeslutterAksjonspunkteventTestInfo extends AksjonspunkteventTestInfo{

    OppretteOppgaveEgenskapTilBeslutterAksjonspunkteventTestInfo(Long behandlingId, String behandlendeEnhet, Long saksnummer, String behandlingtypeKode
            , String fagsakYtelseTypeKode) {
        super(behandlingId, behandlendeEnhet, saksnummer, behandlingtypeKode, fagsakYtelseTypeKode);
        HashMap<String, String> aksjonspunktMap = new HashMap<>();
        aksjonspunktMap.put("5016", "OPPR");
        behandlingProsessEventDto = behandlingProsessEventDtoBuilder.medEventHendelse(EventHendelse.AKSJONSPUNKT_OPPRETTET)
                .medAksjonspunktKoderMedStatusListe(aksjonspunktMap).build();
    }

}
