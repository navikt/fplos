package no.nav.fplos.verdikjedetester.mock;

import java.util.HashMap;

import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;

public class OppretteOppgaveEgenskapTilBeslutterAksjonspunkteventTestInfo extends AksjonspunkteventTestInfo{

    OppretteOppgaveEgenskapTilBeslutterAksjonspunkteventTestInfo(Long behandlingId, String behandlendeEnhet, Long saksnummer, String behandlingtypeKode
            , String fagsakYtelseTypeKode) {
        super(behandlingId, behandlendeEnhet, saksnummer, behandlingtypeKode, fagsakYtelseTypeKode);
        HashMap<String, String> aksjonspunktMap = new HashMap<>();
        aksjonspunktMap.put("5016", "OPPR");
        behandlingProsessEventDto = behandlingProsessEventDtoBuilder.medEventHendelse(BehandlingProsessEventDto.EventHendelse.AKSJONSPUNKT_OPPRETTET)
                .medAksjonspunktKoderMedStatusListe(aksjonspunktMap).build();
    }

}
