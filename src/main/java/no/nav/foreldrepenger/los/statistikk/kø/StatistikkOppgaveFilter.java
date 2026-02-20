package no.nav.foreldrepenger.los.statistikk.kø;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(StatistikkOppgaveFilterType.class)
@Table(name = "STAT_OPPGAVE_FILTER")
public class StatistikkOppgaveFilter implements Serializable {
    @Id
    @Column(name = "OPPGAVE_FILTER_ID", updatable = false, nullable = false)
    private Long oppgaveFilterId;

    @Id
    @Column(name = "TIDSSTEMPEL", updatable = false, nullable = false)
    private Long tidsstempel;

    @Column(name = "STAT_DATO", updatable = false, nullable = false)
    private LocalDate statistikkDato;

    @Column(name = "ANTALL_AKTIVE", updatable = false, nullable = false)
    private Integer antallAktive;

    @Column(name = "ANTALL_TILGJENGELIGE", updatable = false, nullable = false)
    private Integer antallTilgjengelige;

    @Column(name = "ANTALL_VENTENDE")
    private Integer antallVentende;

    @Column(name = "ANTALL_OPPRETTET")
    private Integer antallOpprettet;

    @Column(name = "ANTALL_AVSLUTTET")
    private Integer antallAvsluttet;

    @Enumerated(EnumType.STRING)
    @Column(name = "INNSLAG_TYPE", updatable = false, nullable = false)
    private InnslagType innslagType;

    public StatistikkOppgaveFilter() {
        // for hibernate
    }

    public StatistikkOppgaveFilter(Long oppgaveFilterId,
                                   Long tidsstempel,
                                   LocalDate statistikkDato,
                                   Integer antallAktive,
                                   Integer antallTilgjengelige,
                                   Integer antallVentende,
                                   Integer antallOpprettet,
                                   Integer antallAvsluttet,
                                   InnslagType innslagType) {
        this.oppgaveFilterId = oppgaveFilterId;
        this.tidsstempel = tidsstempel;
        this.statistikkDato = statistikkDato;
        this.antallAktive = antallAktive;
        this.antallTilgjengelige = antallTilgjengelige;
        this.antallVentende = antallVentende;
        this.innslagType = innslagType;
        this.antallOpprettet = antallOpprettet;
        this.antallAvsluttet = antallAvsluttet;
    }

    public Long getTidsstempel() {
        return tidsstempel;
    }

    public Long getOppgaveFilterId() {
        return oppgaveFilterId;
    }

    public LocalDate getStatistikkDato() {
        return statistikkDato;
    }

    public Integer getAntallAktive() {
        return antallAktive;
    }

    public Integer getAntallTilgjengelige() {
        return antallTilgjengelige;
    }

    public  Integer getAntallVentende() {
        return Optional.ofNullable(antallVentende).orElse(0);
    }

    public InnslagType getInnslagType() {
        return innslagType;
    }

    public Integer getAntallOpprettet() {
        return antallOpprettet;
    }

    public Integer getAntallAvsluttet() {
        return Optional.ofNullable(antallAvsluttet).orElse(0);
    }

    @Override
    public String toString() {
        return "StatistikkOppgaveFilter{" + "oppgaveFilterId=" + oppgaveFilterId + ", tidsstempel=" + tidsstempel + ", statistikkDato="
            + statistikkDato + ", antallAktive=" + antallAktive + ", antallTilgjengelige=" + antallTilgjengelige + ", antallVentende="
            + antallVentende + ", antallOpprettet=" + antallOpprettet + ", antallAvsluttet=" + antallAvsluttet + ", innslagType=" + innslagType + '}';
    }
}
