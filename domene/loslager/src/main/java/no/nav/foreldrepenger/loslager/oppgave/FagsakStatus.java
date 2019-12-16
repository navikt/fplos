package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.fplos.kodeverk.Kodeverdi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FagsakStatus implements Kodeverdi {

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

    public static FagsakStatus fraKode(String kode) {
        return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElseThrow();
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
