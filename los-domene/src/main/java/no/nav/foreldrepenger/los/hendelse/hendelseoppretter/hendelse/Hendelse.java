package no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

@Entity(name = "Hendelse")
@Table(name = "HENDELSE")
@Inheritance(strategy= InheritanceType.JOINED)
public class Hendelse extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HENDELSE")
    private Long id;

    @Convert(converter = Fagsystem.FagSystemConverter.class)
    @Column(name = "fagsystem")
    private Fagsystem fagsystem;

    @Embedded
    private BehandlingId behandlingId;

    @Column(name = "behandlende_enhet")
    private String behandlendeEnhet;

    @Column(name = "saksnummer")
    private String saksnummer;

    @Column(name = "aktør_id")
    private String aktørId;

    @Column(name = "behandling_opprettet_tidspunkt")
    private LocalDateTime behandlingOpprettetTidspunkt;

    @Column(name = "behandling_type")
    private BehandlingType behandlingType;

    @Column(name = "ytelse_type")
    private FagsakYtelseType ytelseType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fagsystem getFagsystem() {
        return fagsystem;
    }

    public void setFagsystem(Fagsystem fagsystem) {
        this.fagsystem = fagsystem;
    }

    public BehandlingId getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(BehandlingId behandlingId) {
        this.behandlingId = behandlingId;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public void setBehandlendeEnhet(String behandlendeEnhet) {
        this.behandlendeEnhet = behandlendeEnhet;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public void setSaksnummer(String saksnummer) {
        this.saksnummer = saksnummer;
    }

    public String getAktørId() {
        return aktørId;
    }

    public void setAktørId(String aktørId) {
        this.aktørId = aktørId;
    }

    public LocalDateTime getBehandlingOpprettetTidspunkt() {
        return behandlingOpprettetTidspunkt;
    }

    public void setBehandlingOpprettetTidspunkt(LocalDateTime behandlingOpprettetDato) {
        this.behandlingOpprettetTidspunkt = behandlingOpprettetDato;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public void setBehandlingType(BehandlingType behandlingType) {
        this.behandlingType = behandlingType;
    }

    public FagsakYtelseType getYtelseType() {
        return ytelseType;
    }

    public void setYtelseType(FagsakYtelseType ytelseTypeKode) {
        this.ytelseType = ytelseTypeKode;
    }
}
