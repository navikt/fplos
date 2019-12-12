package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.fplos.kodeverk.Kodeverdi;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FagsakStatus implements Kodeverdi {

    //TODO: kan ikke se at denne enumtypen er brukt noe sted i basen. Brukes til dels i fpsak-klienten. Vurder fjerning eller flytting.

    OPPRETTET("OPPR", "Opprettet"),
    UNDER_BEHANDLING("UBEH", "Under behandling"),
    LØPENDE("LOP", "Løpende"),
    AVSLUTTET("AVSLU", "Avsluttet");

    private String kode;
    private final String navn;
    public static final String KODEVERK = "FAGSAK_STATUS";

    FagsakStatus(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    private static final Map<String, FagsakStatus> kodeMap = Collections.unmodifiableMap(initializeMapping());

    private static HashMap<String, FagsakStatus> initializeMapping() {
        HashMap<String, FagsakStatus> map = new HashMap<>();
        for (var v : values()) {
            map.putIfAbsent(v.kode, v);
        }
        return map;
    }

    public static FagsakStatus fraKode(String value) {
        return Optional.ofNullable(kodeMap.get(value))
                .orElse(null);
    }

    public static List<FagsakStatus> getEnums() {
        return Arrays.stream(values())
                .collect(Collectors.toList());
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }

    public String getKodeverk() {
        return KODEVERK;
    }

    @JsonCreator
    static FagsakStatus findValue(@JsonProperty("kode") String kode) {
        return fraKode(kode);
    }
}
