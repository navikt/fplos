package no.nav.fplos.verdikjedetester.mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;

public class MockKafkaMessages {

    public static Map<Long, MeldingsTestInfo> tomtMap = Map.of();
    static List<String> messages = new ArrayList<>();

    public static Map<Long,MeldingsTestInfo> førstegangsbehandlingMeldinger = Map.of(
            1L,new MeldingsTestInfo(1L,1L, LocalDateTime.now(),true, BehandlingType.FØRSTEGANGSSØKNAD),
            2L, new MeldingsTestInfo(2L,2L, LocalDateTime.now(),true,BehandlingType.FØRSTEGANGSSØKNAD),
            3L,new MeldingsTestInfo(3L,3L, LocalDateTime.now(),true,BehandlingType.FØRSTEGANGSSØKNAD) );

    public static Map<Long,MeldingsTestInfo> innsynMeldinger = Map.of(
            4L,new MeldingsTestInfo(4L,4L, LocalDateTime.now(),true, BehandlingType.INNSYN),
            5L, new MeldingsTestInfo(5L,5L, LocalDateTime.now(),true,BehandlingType.INNSYN));


    public static Map<Long,MeldingsTestInfo> defaultførstegangsbehandlingMelding = Map.of(
            6L,new MeldingsTestInfo(6L));

    public static void clearMessages(){
        messages.clear();
    }

    public static void sendNyeOppgaver(Map<Long, MeldingsTestInfo> innkommendeMeldinger){
        innkommendeMeldinger.values().forEach(melding -> messages.add(melding.tilmeldingstekst()));
    }

    public static void sendEvent(String melding){
        messages.add(melding);
    }

    public static void sendAvsluttetFørstegangsbehandlingOppgave(Long behandlingId){
        MeldingsTestInfo meldingsTestInfo = new MeldingsTestInfo(behandlingId, 1L,  LocalDateTime.now(), false, BehandlingType.FØRSTEGANGSSØKNAD);
        messages.add(meldingsTestInfo.tilmeldingstekst());
    }
}
