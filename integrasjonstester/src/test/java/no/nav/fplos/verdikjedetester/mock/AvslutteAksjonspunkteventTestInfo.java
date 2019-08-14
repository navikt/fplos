package no.nav.fplos.verdikjedetester.mock;

import java.util.HashMap;

import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;

public class AvslutteAksjonspunkteventTestInfo extends AksjonspunkteventTestInfo{

    AvslutteAksjonspunkteventTestInfo(Long behandlingId, String behandlendeEnhet, Long saksnummer, String behandlingtypeKode
            , String fagsakYtelseTypeKode) {
        super(behandlingId, behandlendeEnhet, saksnummer, behandlingtypeKode, fagsakYtelseTypeKode);
        HashMap<String, String> aksjonspunktMap = new HashMap<>();
        aksjonspunktMap.put("5025", "UTFO");
        behandlingProsessEventDto = behandlingProsessEventDtoBuilder.medEventHendelse(BehandlingProsessEventDto.EventHendelse.AKSJONSPUNKT_UTFØRT)
                .medAksjonspunktKoderMedStatusListe(aksjonspunktMap).build();
    }

}
