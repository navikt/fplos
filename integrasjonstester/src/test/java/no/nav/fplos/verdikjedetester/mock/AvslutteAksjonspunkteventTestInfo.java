package no.nav.fplos.verdikjedetester.mock;

import no.nav.vedtak.felles.integrasjon.kafka.EventHendelse;

import java.util.HashMap;

public class AvslutteAksjonspunkteventTestInfo extends AksjonspunkteventTestInfo{

    AvslutteAksjonspunkteventTestInfo(Long behandlingId, String behandlendeEnhet, Long saksnummer, String behandlingtypeKode
            , String fagsakYtelseTypeKode) {
        super(behandlingId, behandlendeEnhet, saksnummer, behandlingtypeKode, fagsakYtelseTypeKode);
        HashMap<String, String> aksjonspunktMap = new HashMap<>();
        aksjonspunktMap.put("5025", "UTFO");
        behandlingProsessEventDto = behandlingProsessEventDtoBuilder.medEventHendelse(EventHendelse.AKSJONSPUNKT_UTFØRT)
                .medAksjonspunktKoderMedStatusListe(aksjonspunktMap).build();
    }

}
