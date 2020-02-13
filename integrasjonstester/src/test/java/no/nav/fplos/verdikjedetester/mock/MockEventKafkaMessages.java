package no.nav.fplos.verdikjedetester.mock;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MockEventKafkaMessages {

    public static Map<Long, AksjonspunkteventTestInfo> tomtMap = Map.of();
    static List<String> eventer = new ArrayList<>();

    private static UUID uuid1 = UUID.nameUUIDFromBytes("uuid_1".getBytes());
    private static UUID uuid2 = UUID.nameUUIDFromBytes("uuid_2".getBytes());
    private static UUID uuid3 = UUID.nameUUIDFromBytes("uuid_3".getBytes());
    private static UUID uuid4 = UUID.nameUUIDFromBytes("uuid_4".getBytes());
    private static UUID uuid5 = UUID.nameUUIDFromBytes("uuid_5".getBytes());
    private static UUID uuid6 = UUID.nameUUIDFromBytes("uuid_6".getBytes());
    private static UUID uuid7 = UUID.nameUUIDFromBytes("uuid_7".getBytes());
    private static UUID uuid8 = UUID.nameUUIDFromBytes("uuid_8".getBytes());

    public static final long BEHANDLING_ID_1 = 1L;
    public static final long BEHANDLING_ID_2 = 2L;
    public static final long BEHANDLING_ID_3 = 3L;

    public static final String BEHANDLENDE_ENHET = "4806";
    public static Map<Long, AksjonspunkteventTestInfo> førstegangsbehandlingMeldinger = Map.of(
            BEHANDLING_ID_1, new OppretteAksjonspunkteventTestInfo(BEHANDLING_ID_1, uuid1, BEHANDLENDE_ENHET, 1L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            BEHANDLING_ID_2, new OppretteAksjonspunkteventTestInfo(BEHANDLING_ID_2, uuid2, BEHANDLENDE_ENHET, 2L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            BEHANDLING_ID_3, new OppretteAksjonspunkteventTestInfo(BEHANDLING_ID_3, uuid3, BEHANDLENDE_ENHET, 3L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()));

    public static Map<Long, AksjonspunkteventTestInfo> innsynMeldinger = Map.of(
            4L, new OppretteAksjonspunkteventTestInfo(4L, uuid4, BEHANDLENDE_ENHET, 4L, BehandlingType.INNSYN.getKode(), FagsakYtelseType.ENGANGSTØNAD.getKode()),
            5L, new OppretteAksjonspunkteventTestInfo(5L, uuid5, BEHANDLENDE_ENHET, 5L, BehandlingType.INNSYN.getKode(), FagsakYtelseType.ENGANGSTØNAD.getKode()));


    public static Map<Long, AksjonspunkteventTestInfo> defaultførstegangsbehandlingMelding = Map.of(
            6L, new OppretteAksjonspunkteventTestInfo(6L, uuid6, BEHANDLENDE_ENHET, 6L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()));

    public static Map<Long, AksjonspunkteventTestInfo> tilBeslutter = Map.of(
            7L, new OppretteOppgaveEgenskapTilBeslutterAksjonspunkteventTestInfo(7L, uuid7, BEHANDLENDE_ENHET, 7L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            8L, new OppretteOppgaveEgenskapTilBeslutterAksjonspunkteventTestInfo(8L, uuid8, BEHANDLENDE_ENHET, 8L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()));

    public static Map<Long, AksjonspunkteventTestInfo> medManuellSattpåVent = Map.of(
            BEHANDLING_ID_1, new OppretteAksjonspunktManuellPaaVent(BEHANDLING_ID_1, uuid1, BEHANDLENDE_ENHET, 1L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            BEHANDLING_ID_2, new OppretteAksjonspunktManuellPaaVent(BEHANDLING_ID_2, uuid2, BEHANDLENDE_ENHET, 1L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode())
    );

    public static void clearMessages(){
        eventer.clear();
    }

    public static void sendNyeOppgaver(Map<Long, AksjonspunkteventTestInfo> innkommendeMeldinger){
        innkommendeMeldinger.values().forEach(event -> eventer.add(event.tilmeldingstekst()));
    }

    public static Map<Long, AksjonspunkteventTestInfo> avsluttførstegangsbehandlingMeldinger = Map.of(
            1L,new AvslutteAksjonspunkteventTestInfo(1L, uuid1, BEHANDLENDE_ENHET,1L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            2L,new AvslutteAksjonspunkteventTestInfo(2L, uuid2, BEHANDLENDE_ENHET,2L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            3L,new AvslutteAksjonspunkteventTestInfo(3L, uuid3, BEHANDLENDE_ENHET,3L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()));

}
