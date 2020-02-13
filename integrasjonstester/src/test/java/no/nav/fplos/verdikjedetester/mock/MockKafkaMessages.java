package no.nav.fplos.verdikjedetester.mock;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MockKafkaMessages {

    private static UUID uuid1 = UUID.nameUUIDFromBytes("uuid_1".getBytes());
    private static UUID uuid2 = UUID.nameUUIDFromBytes("uuid_2".getBytes());
    private static UUID uuid3 = UUID.nameUUIDFromBytes("uuid_3".getBytes());
    private static UUID uuid4 = UUID.nameUUIDFromBytes("uuid_4".getBytes());
    private static UUID uuid5 = UUID.nameUUIDFromBytes("uuid_5".getBytes());
    private static UUID uuid6 = UUID.nameUUIDFromBytes("uuid_6".getBytes());

    public static Map<Long, MeldingsTestInfo> tomtMap = Map.of();
    static List<String> messages = new ArrayList<>();

    public static Map<Long,MeldingsTestInfo> førstegangsbehandlingMeldinger = Map.of(
            1L,new MeldingsTestInfo(1L, uuid1,1L, "3", BehandlingType.FØRSTEGANGSSØKNAD, FagsakYtelseType.FORELDREPENGER),
            2L, new MeldingsTestInfo(2L, uuid2,2L, "3", BehandlingType.FØRSTEGANGSSØKNAD, FagsakYtelseType.FORELDREPENGER),
            3L,new MeldingsTestInfo(3L, uuid3,3L, "3", BehandlingType.FØRSTEGANGSSØKNAD, FagsakYtelseType.FORELDREPENGER) );

    public static Map<Long,MeldingsTestInfo> innsynMeldinger = Map.of(
            4L,new MeldingsTestInfo(4L, uuid4,4L, "3", BehandlingType.INNSYN, FagsakYtelseType.FORELDREPENGER),
            5L, new MeldingsTestInfo(5L, uuid5,5L, "3",BehandlingType.INNSYN, FagsakYtelseType.FORELDREPENGER));


    public static Map<Long,MeldingsTestInfo> defaultførstegangsbehandlingMelding = Map.of(
            6L,new MeldingsTestInfo(6L, uuid6, "3"));

    public static void clearMessages(){
        messages.clear();
    }

    public static void sendNyeOppgaver(Map<Long, MeldingsTestInfo> innkommendeMeldinger){
        innkommendeMeldinger.values().forEach(melding -> messages.add(melding.tilmeldingstekst()));
    }

    public static void sendEvent(String melding){
        messages.add(melding);
    }

    public static void sendAvsluttetFørstegangsbehandlingOppgave(Long behandlingId, UUID uuid){
        MeldingsTestInfo meldingsTestInfo = new MeldingsTestInfo(behandlingId, uuid, 1L,  "3", BehandlingType.FØRSTEGANGSSØKNAD, FagsakYtelseType.FORELDREPENGER);
        messages.add(meldingsTestInfo.tilmeldingstekst());
    }
}
