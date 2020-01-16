package no.nav.fplos.verdikjedetester.mock;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MockKafkaMessages {

    public static Map<Long, MeldingsTestInfo> tomtMap = Map.of();
    static List<String> messages = new ArrayList<>();

    public static Map<Long,MeldingsTestInfo> førstegangsbehandlingMeldinger = Map.of(
            1L,new MeldingsTestInfo(1L,1L, "3", BehandlingType.FØRSTEGANGSSØKNAD, FagsakYtelseType.FORELDREPENGER),
            2L, new MeldingsTestInfo(2L,2L, "3", BehandlingType.FØRSTEGANGSSØKNAD, FagsakYtelseType.FORELDREPENGER),
            3L,new MeldingsTestInfo(3L,3L, "3", BehandlingType.FØRSTEGANGSSØKNAD, FagsakYtelseType.FORELDREPENGER) );

    public static Map<Long,MeldingsTestInfo> innsynMeldinger = Map.of(
            4L,new MeldingsTestInfo(4L,4L, "3", BehandlingType.INNSYN, FagsakYtelseType.FORELDREPENGER),
            5L, new MeldingsTestInfo(5L,5L, "3",BehandlingType.INNSYN, FagsakYtelseType.FORELDREPENGER));


    public static Map<Long,MeldingsTestInfo> defaultførstegangsbehandlingMelding = Map.of(
            6L,new MeldingsTestInfo(6L, "3"));

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
        MeldingsTestInfo meldingsTestInfo = new MeldingsTestInfo(behandlingId, 1L,  "3", BehandlingType.FØRSTEGANGSSØKNAD, FagsakYtelseType.FORELDREPENGER);
        messages.add(meldingsTestInfo.tilmeldingstekst());
    }
}
