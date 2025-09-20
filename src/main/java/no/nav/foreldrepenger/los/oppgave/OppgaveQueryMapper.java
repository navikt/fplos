package no.nav.foreldrepenger.los.oppgave;

import static no.nav.foreldrepenger.los.oppgave.OppgaveRepository.COUNT_FRA_OPPGAVE;
import static no.nav.foreldrepenger.los.oppgave.OppgaveRepository.COUNT_FRA_TILBAKEKREVING_OPPGAVE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Objects;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import no.nav.foreldrepenger.los.felles.util.BrukerIdent;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

public class OppgaveQueryMapper {
    private static final String ORDER_BY_SQL = "ORDER BY ";
    private static final String DESC_SQL = " DESC";
    private static final String BEHANDLINGSFRIST_FELT_SQL = "o.behandlingsfrist";
    private static final String ORDER_BY_BEHANDLINGSFRIST_ASC = "ORDER BY o.behandlingsfrist ASC";
    private static final String BEHANDLINGOPPRETTET_FELT_SQL = "o.behandlingOpprettet";
    private static final String ORDER_BY_BEHANDLINGOPPRETTET_ASC = "ORDER BY o.behandlingOpprettet ASC";
    private static final String FØRSTE_STØNADSDAG_FELT_SQL = "o.førsteStønadsdag";
    private static final String ORDER_BY_FØRSTE_STØNADSDAG_ASC = "ORDER BY o.førsteStønadsdag ASC";
    private static final String BELØP_FELT_SQL = "o.belop";
    private static final String ORDER_BY_BELØP_DESC = "ORDER BY o.belop DESC";
    private static final String FEILUTBETALINGSTART_FELT_SQL = "o.feilutbetalingstart";
    private static final String ORDER_BY_FEILUTBETALINGSTART_ASC = "ORDER BY o.feilutbetalingstart ASC";

    private OppgaveQueryMapper() { }

    public static <T> TypedQuery<T> lagOppgavespørring(EntityManager entityManager, String selection, Class<T> resultClass, Oppgavespørring queryDto) {
        var parameters = new HashMap<String, Object>();
        parameters.put("enhetsnummer", queryDto.getEnhetsnummer());

        var sb = new StringBuilder();
        sb.append(selection);
        sb.append(" WHERE o.behandlendeEnhet = :enhetsnummer ");
        sb.append(filtrerBehandlingType(queryDto, parameters));
        sb.append(filtrerYtelseType(queryDto, parameters));
        sb.append(andreKriterierSubquery(queryDto, parameters));
        sb.append(reserverteSubquery(parameters));
        sb.append(tilBeslutter(queryDto, parameters));
        sb.append(" AND o.aktiv = true ");
        sb.append(beløpFilter(queryDto, parameters));
        sb.append(datoFilter(queryDto, parameters));
        sb.append(sortBy(selection, queryDto));

        var query = entityManager.createQuery(sb.toString(), resultClass);
        parameters.forEach(query::setParameter);

        return query;
    }

    private static String beløpFilter(Oppgavespørring oppgavespørring, HashMap<String, Object> parameters) {
        if (!KøSortering.BELØP.equals(oppgavespørring.getSortering())) {
            return "";
        }
        var fra = oppgavespørring.getFiltrerFra();
        var til = oppgavespørring.getFiltrerTil();
        // må kanskje gjøre en BigDecimal.valueOf() på filterverdiene?
        var numeriskFiltrering = "";
        if (fra != null) {
            numeriskFiltrering = "AND o.belop >= :filterFra ";
            parameters.put("filterFra", fra);
        }
        if (til != null) {
            numeriskFiltrering += "AND o.belop <= :filterTil ";
            parameters.put("filterTil", til);
        }
        return numeriskFiltrering;

    }

    private static String sortBy(String selection, Oppgavespørring queryDto) {
        if (selection.equals(COUNT_FRA_OPPGAVE) || selection.equals(COUNT_FRA_TILBAKEKREVING_OPPGAVE)) {
            return "";
        }
        return switch (queryDto.getSortering()) {
            case BEHANDLINGSFRIST -> ORDER_BY_BEHANDLINGSFRIST_ASC;
            case OPPRETT_BEHANDLING -> ORDER_BY_BEHANDLINGOPPRETTET_ASC;
            case FØRSTE_STØNADSDAG -> ORDER_BY_FØRSTE_STØNADSDAG_ASC;
            case BELØP -> ORDER_BY_BELØP_DESC;
            case FEILUTBETALINGSTART -> ORDER_BY_FEILUTBETALINGSTART_ASC;
        };
    }

    private static String andreKriterierSubquery(Oppgavespørring queryDto, HashMap<String, Object> parameters) {
        var inkluderAkt = queryDto.getInkluderAndreKriterierTyper();
        var ekskluderAkt = queryDto.getEkskluderAndreKriterierTyper();

        var sb = new StringBuilder();
        if (!inkluderAkt.isEmpty()) {
            parameters.put("inkluderAktKoder", inkluderAkt);
            parameters.put("inkluderAktAntall", inkluderAkt.size());
            sb.append(" AND :inkluderAktAntall = (")
                .append("   SELECT COUNT(oe.andreKriterierType) ")
                .append("   FROM OppgaveEgenskap oe ")
                .append("   WHERE oe.oppgave = o ")
                .append("     AND oe.andreKriterierType IN (:inkluderAktKoder)")
                .append(" ) ");
        }
        if (!ekskluderAkt.isEmpty()) {
            parameters.put("ekskluderAktKoder", ekskluderAkt);
            sb.append("AND NOT EXISTS ( ")
                .append("SELECT 1 FROM OppgaveEgenskap oe ")
                .append("WHERE oe.oppgave = o AND oe.andreKriterierType IN (:ekskluderAktKoder)")
                .append(") ");
        }

        return sb.toString();
    }

    private static String filtrerBehandlingType(Oppgavespørring queryDto, HashMap<String, Object> parameters) {
        if (queryDto.getBehandlingTyper().isEmpty()) {
            return "";
        }
        parameters.put("behtyper", queryDto.getBehandlingTyper());
        return "AND o.behandlingType in :behtyper ";
    }

    private static String filtrerYtelseType(Oppgavespørring queryDto, HashMap<String, Object> parameters) {
        if (queryDto.getYtelseTyper().isEmpty()) {
            return "";
        }
        parameters.put("fagsakYtelseType", queryDto.getYtelseTyper());
        return "AND o.fagsakYtelseType in :fagsakYtelseType ";
    }

    private static String reserverteSubquery(HashMap<String, Object> parameters) {
        parameters.put("nå", LocalDateTime.now());
        return "AND NOT EXISTS (select r from Reservasjon r where r.oppgave = o and r.reservertTil > :nå) ";
    }

    private static String tilBeslutter(Oppgavespørring dto, HashMap<String, Object> parameters) {
        var tilBeslutterKø = dto.getInkluderAndreKriterierTyper().contains(AndreKriterierType.TIL_BESLUTTER);
        if (dto.getForAvdelingsleder() || !tilBeslutterKø) {
            return "";
        }
        parameters.put("tilbeslutter", AndreKriterierType.TIL_BESLUTTER);
        parameters.put("uid", BrukerIdent.brukerIdent());
        return """
            AND NOT EXISTS (
                select oetilbesl.oppgave from OppgaveEgenskap oetilbesl
                where oetilbesl.oppgave = o
                    AND oetilbesl.andreKriterierType = :tilbeslutter
                    AND upper(oetilbesl.sisteSaksbehandlerForTotrinn) = :uid
            )""";
    }

    private static String datoFilter(Oppgavespørring oppgavespørring, HashMap<String, Object> parameters) {
        var sortering = oppgavespørring.getSortering();
        // Filteret kan enten være feilutbetalt beløp eller dato (x antall dager relativt til dd eller absolutte datoer)
        // Spesielt for dynamisk filter: x kan også være negativ for å filtrere tilbake i tid.

        if (KøSortering.BELØP.equals(sortering)) {
            // håndteres i beløpFilter()
            return "";
        }

        var feltLiteral = switch (sortering) {
            case BEHANDLINGSFRIST -> BEHANDLINGSFRIST_FELT_SQL;
            case OPPRETT_BEHANDLING -> BEHANDLINGOPPRETTET_FELT_SQL;
            case FØRSTE_STØNADSDAG -> FØRSTE_STØNADSDAG_FELT_SQL;
            case FEILUTBETALINGSTART -> FEILUTBETALINGSTART_FELT_SQL;
            case BELØP -> throw new IllegalArgumentException("Skal ikke komme hit med beløpssortering");
        };

        var filter = "";
        if (oppgavespørring.isErDynamiskPeriode()) {
            // Filtrerer på antall dager relativt til i dag.
            // Perioden man ser på kan være i fortid eller fremtid, avhengig av positiv/negativ verdi på filtrerFra/Til
            var fomAntallDagerFrem = oppgavespørring.getFiltrerFra();
            if (fomAntallDagerFrem != null) {
                var aktuellFomDato = LocalDate.now().plusDays(fomAntallDagerFrem);
                if (Objects.equals(KøSortering.FØRSTE_STØNADSDAG, oppgavespørring.getSortering())) {
                    // Første stønadsdag er en LocalDate i entiteten, mens øvrige er LocalDateTime
                    parameters.put("filterFom", aktuellFomDato);
                } else {
                    parameters.put("filterFom", aktuellFomDato.atTime(LocalTime.MIN));
                }
                filter = "AND " + feltLiteral + " >= :filterFom ";
            }
            var tomAntallDagerFrem = oppgavespørring.getFiltrerTil();
            if (tomAntallDagerFrem != null) {
                var aktuellTomDato = LocalDate.now().plusDays(tomAntallDagerFrem);
                if (Objects.equals(KøSortering.FØRSTE_STØNADSDAG, oppgavespørring.getSortering())) {
                    parameters.put("filterTom", aktuellTomDato);
                } else {
                    parameters.put("filterTom", aktuellTomDato.atTime(LocalTime.MAX));
                }
                filter += "AND " + feltLiteral + " <= :filterTom ";
            }
        } else {
            // Filtrerer på absolutte datoer
            var fomDato = oppgavespørring.getFiltrerFomDato();
            var tomDato = oppgavespørring.getFiltrerTomDato();
            if (fomDato != null) {
                if (Objects.equals(KøSortering.FØRSTE_STØNADSDAG, oppgavespørring.getSortering())) {
                    parameters.put("filterFom", fomDato);
                } else {
                    parameters.put("filterFom", fomDato.atTime(LocalTime.MIN));
                }
                filter = "AND " + feltLiteral + " >= :filterFom ";
            }
            if (tomDato != null) {
                if (Objects.equals(KøSortering.FØRSTE_STØNADSDAG, oppgavespørring.getSortering())) {
                    parameters.put("filterTom", tomDato);
                } else {
                    parameters.put("filterTom", tomDato.atTime(LocalTime.MAX));
                }
                filter += "AND " + feltLiteral + " <= :filterTom ";
            }
        }
        return filter;
    }

}
