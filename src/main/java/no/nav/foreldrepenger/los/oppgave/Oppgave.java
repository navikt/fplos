package no.nav.foreldrepenger.los.oppgave;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "Oppgave")
@Table(name = "OPPGAVE")
@Inheritance(strategy = InheritanceType.JOINED)
public class Oppgave extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPPGAVE")
    protected Long id;

    @Embedded
    protected Saksnummer saksnummer; // Denne er de-facto non-null

    @Embedded
    protected AktørId aktørId;

    @Column(name = "BEHANDLENDE_ENHET")
    protected String behandlendeEnhet;

    @Column(name = "BEHANDLINGSFRIST")
    protected LocalDateTime behandlingsfrist;

    @Column(name = "BEHANDLING_OPPRETTET")
    protected LocalDateTime behandlingOpprettet;

    @Column(name = "FORSTE_STONADSDAG")
    protected LocalDate førsteStønadsdag;

    @Convert(converter = BehandlingType.KodeverdiConverter.class)
    @Column(name = "BEHANDLING_TYPE")
    protected BehandlingType behandlingType = BehandlingType.INNSYN;

    @OneToMany(mappedBy = "oppgave")
    protected Set<OppgaveEgenskap> oppgaveEgenskaper;

    @Convert(converter = FagsakYtelseType.KodeverdiConverter.class)
    @Column(name = "FAGSAK_YTELSE_TYPE")
    protected FagsakYtelseType fagsakYtelseType;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "AKTIV")
    protected Boolean aktiv = Boolean.TRUE;

    @Convert(converter = Fagsystem.FagSystemConverter.class)
    @Column(name = "SYSTEM")
    protected Fagsystem system;

    @Column(name = "OPPGAVE_AVSLUTTET")
    protected LocalDateTime oppgaveAvsluttet;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "UTFORT_FRA_ADMIN")
    protected Boolean utfortFraAdmin = Boolean.FALSE;

    @Embedded
    protected BehandlingId behandlingId;

    @OneToOne(mappedBy = "oppgave")
    protected Reservasjon reservasjon;

    @Column(name = "FEILUTBETALING_BELOP")
    protected BigDecimal feilutbetalingBelop;

    @Column(name = "FEILUTBETALING_START")
    protected LocalDateTime feilutbetalingStart;

    public Long getId() {
        return id;
    }

    public Saksnummer getSaksnummer() {
        return saksnummer;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public boolean getAktiv() {
        return aktiv != null && aktiv;
    }

    public Fagsystem getSystem() {
        return system;
    }

    public BehandlingId getBehandlingId() {
        return behandlingId;
    }

    public LocalDateTime getBehandlingsfrist() {
        return behandlingsfrist;
    }

    public LocalDateTime getBehandlingOpprettet() {
        return behandlingOpprettet;
    }

    public LocalDate getFørsteStønadsdag() {
        return førsteStønadsdag;
    }

    public LocalDateTime getOppgaveAvsluttet() {
        return oppgaveAvsluttet;
    }

    public Reservasjon getReservasjon() {
        return reservasjon;
    }

    public Set<OppgaveEgenskap> getOppgaveEgenskaper() {
        return oppgaveEgenskaper == null ? Set.of() : oppgaveEgenskaper;
    }

    public BigDecimal getFeilutbetalingBelop() {
        return feilutbetalingBelop;
    }

    public LocalDateTime getFeilutbetalingStart() {
        return feilutbetalingStart;
    }

    public void avsluttOppgave() {
        aktiv = false;
        oppgaveAvsluttet = LocalDateTime.now();
    }

    public void gjenåpneOppgave() {
        aktiv = true;
        oppgaveAvsluttet = null;
    }

    public boolean harAktivReservasjon() {
        return reservasjon != null && reservasjon.erAktiv();
    }

    public static <T extends Builder<T>> Builder<T> builder() {
        return new Builder<>();
    }

    @Override
    public String toString() {
        return "Oppgave{" + "id=" + id + ", saksnummer=" + saksnummer + ", aktiv=" + aktiv + ", system='" + system + '\'' + '}';
    }

    public static class Builder<T extends Builder<T>> {
        protected Oppgave tempOppgave;

        Builder() {
            tempOppgave = new Oppgave();
        }

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        public T medBehandlingId(BehandlingId behandlingId) {
            tempOppgave.behandlingId = behandlingId;
            return self();
        }

        public T medSaksnummer(Saksnummer saksnummer) {
            tempOppgave.saksnummer = saksnummer;
            return self();
        }

        public T medAktørId(AktørId aktørId) {
            tempOppgave.aktørId = aktørId;
            return self();
        }

        public T medBehandlendeEnhet(String behandlendeEnhet) {
            tempOppgave.behandlendeEnhet = behandlendeEnhet;
            return self();
        }

        public T medAktiv(Boolean aktiv) {
            tempOppgave.aktiv = aktiv;
            return self();
        }

        public T medBehandlingType(BehandlingType behandlingType) {
            tempOppgave.behandlingType = behandlingType;
            return self();
        }

        public T medSystem(Fagsystem fagsystem) {
            tempOppgave.system = fagsystem;
            return self();
        }

        public T medBehandlingsfrist(LocalDateTime behandlingsfrist) {
            tempOppgave.behandlingsfrist = behandlingsfrist;
            return self();
        }

        public T medBehandlingOpprettet(LocalDateTime behandlingOpprettet) {
            tempOppgave.behandlingOpprettet = behandlingOpprettet;
            return self();
        }

        public T medFørsteStønadsdag(LocalDate førsteStønadsdag) {
            tempOppgave.førsteStønadsdag = førsteStønadsdag;
            return self();
        }


        public T medOppgaveAvsluttet(LocalDateTime oppgaveAvsluttet) {
            tempOppgave.oppgaveAvsluttet = oppgaveAvsluttet;
            return self();
        }

        public T medUtfortFraAdmin(Boolean utfortFraAdmin) {
            tempOppgave.utfortFraAdmin = utfortFraAdmin;
            return self();
        }

        public T medFagsakYtelseType(FagsakYtelseType fagsakYtelseType) {
            tempOppgave.fagsakYtelseType = fagsakYtelseType;
            return self();
        }

        public T medFeilutbetalingBelop(BigDecimal feilutbetalingBelop) {
            throw new IllegalStateException("Kan ikke benyttes direkte i expandfasen, ref TFP-6398");
            //tempOppgave.feilutbetalingBelop = belop;
            //return self();
        }

        public T medFeilutbetalingStart(LocalDateTime feilutbetalingStart) {
            throw new IllegalStateException("Kan ikke benyttes direkte i expandfase, ref TFP-6398");
            //tempOppgave.feilutbetalingStart = feilutbetalingstart;
            //return self();
        }

        public T dummyOppgave(String enhet) {
            tempOppgave.behandlingId = new BehandlingId(UUID.nameUUIDFromBytes("331133L".getBytes()));
            tempOppgave.saksnummer = new Saksnummer("3478293");
            tempOppgave.aktørId = AktørId.dummy();
            tempOppgave.fagsakYtelseType = FagsakYtelseType.FORELDREPENGER;
            tempOppgave.behandlingType = BehandlingType.FØRSTEGANGSSØKNAD;
            tempOppgave.behandlendeEnhet = enhet;
            tempOppgave.behandlingsfrist = LocalDateTime.now();
            tempOppgave.behandlingOpprettet = LocalDateTime.now();
            tempOppgave.førsteStønadsdag = LocalDate.now().plusMonths(1);
            return self();
        }

        public Oppgave build() {
            Objects.requireNonNull(tempOppgave.saksnummer, "saksnummer");
            var oppgave = tempOppgave;
            tempOppgave = new Oppgave();
            if (!"FPTILBAKE".equals(tempOppgave.system) && (tempOppgave.feilutbetalingStart != null
                || tempOppgave.feilutbetalingBelop != null)) {
                throw new IllegalArgumentException("Utviklerfeil: Angitt tilbakebetalingsinformasjon i FPSAK-oppgave");
            }
            tempOppgave = new Oppgave();
            return oppgave;
        }
    }
}
