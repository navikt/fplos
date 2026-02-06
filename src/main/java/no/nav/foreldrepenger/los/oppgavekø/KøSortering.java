package no.nav.foreldrepenger.los.oppgavekø;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;
import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum KøSortering implements Kodeverdi {

    BEHANDLINGSFRIST("BEHFRIST", "Dato for behandlingsfrist"),
    OPPRETT_BEHANDLING("OPPRBEH", "Dato for opprettelse av behandling"),
    FØRSTE_STØNADSDAG("FORSTONAD", "Dato for første stønadsdag"),
    FØRSTE_STØNADSDAG_SYNKENDE("FORSTONAD_SYNK", "Dato for første stønadsdag synkende"),
    BELØP("BELOP", "Feilutbetalt beløp", FeltType.HELTALL, FeltKategori.TILBAKEKREVING),
    FEILUTBETALINGSTART("FEILUTBETALINGSTART", "Dato for første feilutbetaling", FeltType.DATO, FeltKategori.TILBAKEKREVING),
    OPPGAVE_OPPRETTET("OPPGAVE_OPPRETTET", "Dato oppgaven ble opprettet", FeltType.DATO_UTEN_FILTER, FeltKategori.OPPGAVE_OPPRETTET);

    public enum FeltType { HELTALL, DATO, DATO_UTEN_FILTER }

    public enum FeltKategori { UNIVERSAL, OPPGAVE_OPPRETTET, TILBAKEKREVING }

    @JsonValue
    @EnumeratedValue
    private final String kode;
    @JsonIgnore
    private final String navn;
    @JsonIgnore
    private final FeltType felttype;
    @JsonIgnore
    private final FeltKategori feltkategori;

    KøSortering(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
        this.felttype = FeltType.DATO;
        this.feltkategori = FeltKategori.UNIVERSAL;
    }

    KøSortering(String kode, String navn, FeltType felttype, FeltKategori feltkategori) {
        this.kode = kode;
        this.navn = navn;
        this.felttype = felttype;
        this.feltkategori = feltkategori;
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }


    public FeltType getFelttype() {
        return felttype;
    }

    public FeltKategori getFeltkategori() {
        return feltkategori;
    }

}
