package no.nav.foreldrepenger.los.klient.fpsak.dto.kodeverk;

public class KodeDto {
    private String kodeverk;
    private String kode;
    private String navn;

    public KodeDto() {
    }

    public KodeDto(String kodeverk, String kode, String navn) {
        this.kodeverk = kodeverk;
        this.kode = kode;
        this.navn = navn;
    }

    public String getKode() {
        return kode;
    }

    public String getKodeverk() {
        return kodeverk;
    }

    @Override
    public String toString() {
        return "Kode{" +
                "kodeverk='" + kodeverk + '\'' +
                ", kode='" + kode + '\'' +
                ", navn='" + navn + '\'' +
                '}';
    }
}