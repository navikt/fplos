package no.nav.foreldrepenger.los.organisasjon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;

@Entity(name = "saksbehandler")
@Table(name = "SAKSBEHANDLER")
public class Saksbehandler extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SAKSBEHANDLER")
    private Long id;

    @Column(name = "SAKSBEHANDLER_IDENT")
    private String saksbehandlerIdent;

    // TODO: Vurder å fjerne saksbehandlerUuid. Nå brukes den kun for innlogget saksbehandler. Endres ved bytte av avdeling
    @Column(name = "SAKSBEHANDLER_UUID")
    private UUID saksbehandlerUuid;

    @Column(name = "NAVN")
    private String navn;

    @Column(name = "ANSATT_ENHET")
    private String ansattVedEnhet;

    @ManyToMany
    @JoinTable(name = "AVDELING_SAKSBEHANDLER", joinColumns = {@JoinColumn(name = "SAKSBEHANDLER_ID")}, inverseJoinColumns = {@JoinColumn(name = "AVDELING_ID")})
    private List<Avdeling> avdelinger = new ArrayList<>();

    @ManyToMany(mappedBy = "saksbehandlere")
    private List<OppgaveFiltrering> oppgaveFiltreringer = new ArrayList<>();

    public Saksbehandler() {
        //CDI
    }

    public Saksbehandler(String saksbehandlerIdent, UUID saksbehandlerUuid, String navn, String ansattVedEnhet) {
        this.saksbehandlerIdent = saksbehandlerIdent;
        this.saksbehandlerUuid = saksbehandlerUuid;
        this.navn = navn;
        this.ansattVedEnhet = ansattVedEnhet;
    }

    public Long getId() {
        return id;
    }

    public String getSaksbehandlerIdent() {
        return saksbehandlerIdent;
    }

    public UUID getSaksbehandlerUuid() {
        return saksbehandlerUuid;
    }

    public void setSaksbehandlerUuid(UUID saksbehandlerUuid) {
        this.saksbehandlerUuid = saksbehandlerUuid;
    }

    public Optional<UUID> getSaksbehandlerUuidHvisFinnes() {
        return Optional.ofNullable(saksbehandlerUuid);
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

    public List<Avdeling> getAvdelinger() {
        return avdelinger;
    }

    public void leggTilAvdeling(Avdeling avdeling) {
        avdelinger.add(avdeling);
    }

    public void fjernAvdeling(Avdeling avdeling) {
        avdelinger.remove(avdeling);
    }

    public List<OppgaveFiltrering> getOppgaveFiltreringer() {
        return Collections.unmodifiableList(oppgaveFiltreringer);
    }
}
