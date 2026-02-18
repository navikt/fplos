package no.nav.foreldrepenger.los.organisasjon;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.felles.BaseEntitet;

@Entity(name = "saksbehandler")
@Table(name = "SAKSBEHANDLER")
public class Saksbehandler extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SAKSBEHANDLER")
    private Long id;

    @Column(name = "SAKSBEHANDLER_IDENT")
    private String saksbehandlerIdent;

    @Column(name = "NAVN")
    private String navn;

    @Column(name = "ANSATT_ENHET")
    private String ansattVedEnhet;

    public Saksbehandler() {
        //CDI
    }

    public Saksbehandler(String saksbehandlerIdent, String navn, String ansattVedEnhet) {
        this.saksbehandlerIdent = saksbehandlerIdent;
        this.navn = navn;
        this.ansattVedEnhet = ansattVedEnhet;
    }

    public Long getId() {
        return id;
    }

    public String getSaksbehandlerIdent() {
        return saksbehandlerIdent;
    }

    public String getNavn() {
        return navn;
    }

    public String getNavnEllerUkjent() {
        return Optional.ofNullable(navn).orElse("Ukjent saksbehandler");
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getAnsattVedEnhet() {
        return ansattVedEnhet;
    }

    public String getAnsattVedEnhetEllerUkjent() {
        return Optional.ofNullable(ansattVedEnhet).orElse("9999");
    }


    public void setAnsattVedEnhet(String ansattVedEnhet) {
        this.ansattVedEnhet = ansattVedEnhet;
    }

}
