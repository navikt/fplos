package no.nav.foreldrepenger.loslager.organisasjon;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import no.nav.foreldrepenger.loslager.BaseEntitet;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "avdeling")
@Table(name = "AVDELING")
public class Avdeling extends BaseEntitet{

    public static final String AVDELING_DRAMMEN_ENHET = "4806";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AVDELING")
    private Long id;

    @Column(name = "AVDELING_ENHET")
    private String avdelingEnhet;

    @Column(name = "NAVN")
    private String navn;

    @ManyToMany(mappedBy = "avdelinger")
    private List<Saksbehandler> saksbehandlere;

    @OneToMany(mappedBy = "avdeling")
    private List<OppgaveFiltrering> oppgaveFiltrering;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "KREVER_KODE_6")
    private Boolean kreverKode6 = Boolean.FALSE;


    public Long getId() {
        return id;
    }

    public String getAvdelingEnhet() {
        return avdelingEnhet;
    }

    public String getNavn() {
        return navn;
    }

    public List<Saksbehandler> getSaksbehandlere() {
        return Collections.unmodifiableList(saksbehandlere);
    }

    public List<OppgaveFiltrering> getOppgaveFiltrering() {
        return Collections.unmodifiableList(oppgaveFiltrering);
    }

    public Boolean getKreverKode6() {
        return kreverKode6;
    }
}
