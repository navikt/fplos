package no.nav.foreldrepenger.los.organisasjon;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "avdeling")
@Table(name = "AVDELING")
public class Avdeling extends BaseEntitet {
    public static final String AVDELING_DRAMMEN_ENHET = "4806";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AVDELING")
    private Long id;

    @Column(name = "AVDELING_ENHET")
    private String avdelingEnhet;

    @Column(name = "NAVN")
    private String navn;

    @Column(name = "KREVER_KODE_6")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean kreverKode6 = Boolean.FALSE;

    @Column(name = "AKTIV")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean erAktiv = Boolean.TRUE;

    public Avdeling() {
    }

    public Avdeling(String avdelingEnhet, String navn, Boolean kreverKode6) {
        this.avdelingEnhet = avdelingEnhet;
        this.navn = navn;
        this.kreverKode6 = kreverKode6;
    }

    public Long getId() {
        return id;
    }

    public String getAvdelingEnhet() {
        return avdelingEnhet;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public Boolean getKreverKode6() {
        return kreverKode6;
    }

    public Boolean getErAktiv() {
        return erAktiv;
    }

    public void setErAktiv(boolean erAktiv) {
        this.erAktiv = erAktiv;
    }
}
