package no.nav.foreldrepenger.loslager.organisasjon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import no.nav.foreldrepenger.loslager.BaseEntitet;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;

@Entity(name = "saksbehandler")
@Table(name = "SAKSBEHANDLER")
public class Saksbehandler extends BaseEntitet{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SAKSBEHANDLER")
    private Long id;

    @Column(name = "SAKSBEHANDLER_IDENT")
    private String saksbehandlerIdent;

    @ManyToMany
    @JoinTable(name = "AVDELING_SAKSBEHANDLER",
            joinColumns = {@JoinColumn(name = "SAKSBEHANDLER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "AVDELING_ID")})
    private List<Avdeling> avdelinger = new ArrayList<>();

    @ManyToMany(mappedBy = "saksbehandlere")
    private List<OppgaveFiltrering> oppgaveFiltreringer = new ArrayList<>();

    public Saksbehandler(){
        //CDI
    }

    public Saksbehandler(String saksbehandlerIdent){
        this.saksbehandlerIdent = saksbehandlerIdent;
    }

    public Long getId() {
        return id;
    }

    public String getSaksbehandlerIdent() {
        return saksbehandlerIdent;
    }

    public List<Avdeling> getAvdelinger() {
        return avdelinger;
    }

    public void leggTilAvdeling(Avdeling avdeling){
        avdelinger.add(avdeling);
    }

    public void fjernAvdeling(Avdeling avdeling){
        avdelinger.remove(avdeling);
    }

    public List<OppgaveFiltrering> getOppgaveFiltreringer() {
        return Collections.unmodifiableList(oppgaveFiltreringer);
    }
}
