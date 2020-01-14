package no.nav.fplos.verdikjedetester.mock;

import no.nav.vedtak.felles.integrasjon.kafka.EventHendelse;

import java.util.HashMap;

public class OppretteAksjonspunktManuellPaaVent extends AksjonspunkteventTestInfo{

    public OppretteAksjonspunktManuellPaaVent(Long behandlingId, String behandlendeEnhet, Long saksnummer, String behandlingtypeKode
            ,String fagsakYtelseTypeKode) {
        super(behandlingId, behandlendeEnhet, saksnummer, behandlingtypeKode, fagsakYtelseTypeKode);
        HashMap<String, String> aksjonspunktMap = new HashMap<>();
        aksjonspunktMap.put("7001", "OPPR");
        behandlingProsessEventDtoBuilder.medEventHendelse(EventHendelse.AKSJONSPUNKT_OPPRETTET)
                .medAksjonspunktKoderMedStatusListe(aksjonspunktMap).build();
    }

}
