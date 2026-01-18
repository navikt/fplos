package no.nav.foreldrepenger.los.oppgave;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;
import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum BehandlingTilstand implements Kodeverdi {

    OPPRETTET("OPPRETTET", "Opprettet"),
    INGEN("INGEN", "Behandlinsprosess venter svar"),
    VENT_TIDLIG("VENT_TIDLIG", "Venter på tidligst mulig behandlingsdato"),
    VENT_KOMPLETT("VENT_KOMPLETT", "Venter på komplett søknad"),
    VENT_REGISTERDATA("VENT_REGISTERDATA", "Venter på registerdata"),
    VENT_KLAGEINSTANS("VENT_KLAGEINSTANS", "Venter på klageinstans/trygderett"),
    VENT_KØ("VENT_KØ", "Venter i kø"),
    VENT_MANUELL("VENT_MANUELL", "Satt på vent av saksbehandler"),
    VENT_SØKNAD("VENT_SØKNAD", "Venter på søknad"),
    PAPIRSØKNAD("PAPIRSØKNAD", "Aktivt aksjonspunkt papirsøknad"),
    AKSJONSPUNKT("AKSJONSPUNKT", "Aktivt aksjonspunkt"),
    BESLUTTER("BESLUTTER", "Beslutter"),
    AVSLUTTET("AVSLUTTET", "Avsluttet");



    @JsonValue
    @EnumeratedValue
    private final String kode;
    @JsonIgnore
    private final String navn;

    BehandlingTilstand(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }


}
