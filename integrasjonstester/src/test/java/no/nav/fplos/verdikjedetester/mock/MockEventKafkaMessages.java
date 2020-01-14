package no.nav.fplos.verdikjedetester.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

public class MockEventKafkaMessages {

    public static Map<Long, AksjonspunkteventTestInfo> tomtMap = Map.of();
    static List<String> eventer = new ArrayList<>();

    public static final long BEHANDLING_ID_1 = 1L;
    public static final long BEHANDLING_ID_2 = 2L;
    public static final long BEHANDLING_ID_3 = 3L;

    public static final String BEHANDLENDE_ENHET = "4806";
    public static Map<Long, AksjonspunkteventTestInfo> førstegangsbehandlingMeldinger = Map.of(
            BEHANDLING_ID_1, new OppretteAksjonspunkteventTestInfo(BEHANDLING_ID_1, BEHANDLENDE_ENHET, 1L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            BEHANDLING_ID_2, new OppretteAksjonspunkteventTestInfo(BEHANDLING_ID_2, BEHANDLENDE_ENHET, 2L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            BEHANDLING_ID_3, new OppretteAksjonspunkteventTestInfo(BEHANDLING_ID_3, BEHANDLENDE_ENHET, 3L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()));

    public static Map<Long, AksjonspunkteventTestInfo> innsynMeldinger = Map.of(
            4L, new OppretteAksjonspunkteventTestInfo(4L, BEHANDLENDE_ENHET, 4L, BehandlingType.INNSYN.getKode(), FagsakYtelseType.ENGANGSTØNAD.getKode()),
            5L, new OppretteAksjonspunkteventTestInfo(5L, BEHANDLENDE_ENHET, 5L, BehandlingType.INNSYN.getKode(), FagsakYtelseType.ENGANGSTØNAD.getKode()));


    public static Map<Long, AksjonspunkteventTestInfo> defaultførstegangsbehandlingMelding = Map.of(
            6L, new OppretteAksjonspunkteventTestInfo(6L, BEHANDLENDE_ENHET, 6L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()));

    public static Map<Long, AksjonspunkteventTestInfo> tilBeslutter = Map.of(
            7L, new OppretteOppgaveEgenskapTilBeslutterAksjonspunkteventTestInfo(7L, BEHANDLENDE_ENHET, 7L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            8L, new OppretteOppgaveEgenskapTilBeslutterAksjonspunkteventTestInfo(8L, BEHANDLENDE_ENHET, 8L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()));

    public static Map<Long, AksjonspunkteventTestInfo> medManuellSattpåVent = Map.of(
            BEHANDLING_ID_1, new OppretteAksjonspunktManuellPaaVent(BEHANDLING_ID_1, BEHANDLENDE_ENHET, 1L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            BEHANDLING_ID_2, new OppretteAksjonspunktManuellPaaVent(BEHANDLING_ID_2, BEHANDLENDE_ENHET, 1L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode())
    );

    public static void clearMessages(){
        eventer.clear();
    }

    public static void sendNyeOppgaver(Map<Long, AksjonspunkteventTestInfo> innkommendeMeldinger){
        innkommendeMeldinger.values().forEach(event -> eventer.add(event.tilmeldingstekst()));
    }

    public static Map<Long, AksjonspunkteventTestInfo> avsluttførstegangsbehandlingMeldinger = Map.of(
            1L,new AvslutteAksjonspunkteventTestInfo(1L, BEHANDLENDE_ENHET,1L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            2L,new AvslutteAksjonspunkteventTestInfo(2L, BEHANDLENDE_ENHET,2L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()),
            3L,new AvslutteAksjonspunkteventTestInfo(3L, BEHANDLENDE_ENHET,3L, BehandlingType.FØRSTEGANGSSØKNAD.getKode(), FagsakYtelseType.FORELDREPENGER.getKode()));

}
