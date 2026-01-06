package no.nav.foreldrepenger.los.statistikk.kø;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    public StatistikkOppgaveFilter() {
        // for hibernate
    }

    public StatistikkOppgaveFilter(Long oppgaveFilterId, Long tidsstempel, LocalDate statistikkDato, Integer antallAktive, Integer antallTilgjengelige) {
        this.oppgaveFilterId = oppgaveFilterId;
        this.tidsstempel = tidsstempel;
        this.statistikkDato = statistikkDato;
        this.antallAktive = antallAktive;
        this.antallTilgjengelige = antallTilgjengelige;
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

    @Override
    public String toString() {
        return "StatistikkPerKø{" +
            "antallAktive=" + antallAktive +
            ", antallTilgjengelige=" + antallTilgjengelige +
            ", koeId=" + oppgaveFilterId +
            ", statistikkDato=" + statistikkDato +
            ", tidsstempel=" + tidsstempel + '}';
    }
}
