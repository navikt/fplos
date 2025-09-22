package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import no.nav.foreldrepenger.los.felles.util.BrukerIdent;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

import org.hibernate.jpa.HibernateHints;

public class OppgavespørringMapper {

    private static final String BEHANDLINGSFRIST_FELT_SQL = "o.behandlingsfrist";
    private static final String ORDER_BY_BEHANDLINGSFRIST_ASC = "ORDER BY o.behandlingsfrist ASC";
    private static final String BEHANDLINGOPPRETTET_FELT_SQL = "o.behandlingOpprettet";
    private static final String ORDER_BY_BEHANDLINGOPPRETTET_ASC = "ORDER BY o.behandlingOpprettet ASC";
    private static final String FØRSTE_STØNADSDAG_FELT_SQL = "o.førsteStønadsdag";
    private static final String ORDER_BY_FØRSTE_STØNADSDAG_ASC = "ORDER BY o.førsteStønadsdag ASC";
    private static final String ORDER_BY_BELØP_DESC = "ORDER BY o.belop DESC";
    private static final String FEILUTBETALINGSTART_FELT_SQL = "o.feilutbetalingstart";
    private static final String ORDER_BY_FEILUTBETALINGSTART_ASC = "ORDER BY o.feilutbetalingstart ASC";

    private OppgavespørringMapper() { }

    public static <T> TypedQuery<T> lagOppgavespørring(EntityManager entityManager, String selection, Class<T> resultClass, Oppgavespørring queryDto) {
        var parameters = new HashMap<String, Object>();
        parameters.put("enhetsnummer", queryDto.getEnhetsnummer());


        var qlStringBuilder = new StringBuilder();
        qlStringBuilder.append(selection);
        qlStringBuilder.append(" WHERE o.behandlendeEnhet = :enhetsnummer ");
        qlStringBuilder.append(filtrerBehandlingType(queryDto, parameters));
        qlStringBuilder.append(filtrerYtelseType(queryDto, parameters));
        qlStringBuilder.append(andreKriterierSubquery(queryDto, parameters));
        qlStringBuilder.append(reserverteSubquery(parameters));
        qlStringBuilder.append(tilBeslutter(queryDto, parameters));
        qlStringBuilder.append(" AND o.aktiv = true ");
        qlStringBuilder.append(beløpFilter(queryDto, parameters));
        qlStringBuilder.append(datoFilter(queryDto, parameters));

        boolean erSelectCountSpørring = resultClass.equals(Long.class);
        if (!erSelectCountSpørring) {
            qlStringBuilder.append(orderBy(queryDto));
        }

        var query = entityManager.createQuery(qlStringBuilder.toString(), resultClass);
        parameters.forEach(query::setParameter);

        query.setHint(HibernateHints.HINT_READ_ONLY, "true");
        return query;
    }

    private static String beløpFilter(Oppgavespørring oppgavespørring, Map<String, Object> parameters) {
        if (!KøSortering.BELØP.equals(oppgavespørring.getSortering())) {
            return "";
        }
        var fra = oppgavespørring.getFiltrerFra();
        var til = oppgavespørring.getFiltrerTil();
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

    private static String orderBy(Oppgavespørring queryDto) {
        return switch (queryDto.getSortering()) {
            case BEHANDLINGSFRIST -> ORDER_BY_BEHANDLINGSFRIST_ASC;
            case OPPRETT_BEHANDLING -> ORDER_BY_BEHANDLINGOPPRETTET_ASC;
            case FØRSTE_STØNADSDAG -> ORDER_BY_FØRSTE_STØNADSDAG_ASC;
            case BELØP -> ORDER_BY_BELØP_DESC;
            case FEILUTBETALINGSTART -> ORDER_BY_FEILUTBETALINGSTART_ASC;
        };
    }

    private static String andreKriterierSubquery(Oppgavespørring queryDto, Map<String, Object> parameters) {
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

    private static String filtrerBehandlingType(Oppgavespørring queryDto, Map<String, Object> parameters) {
        if (queryDto.getBehandlingTyper().isEmpty()) {
            return "";
        }
        parameters.put("behtyper", queryDto.getBehandlingTyper());
        return "AND o.behandlingType in :behtyper ";
    }

    private static String filtrerYtelseType(Oppgavespørring queryDto, Map<String, Object> parameters) {
        if (queryDto.getYtelseTyper().isEmpty()) {
            return "";
        }
        parameters.put("fagsakYtelseType", queryDto.getYtelseTyper());
        return "AND o.fagsakYtelseType in :fagsakYtelseType ";
    }

    private static String reserverteSubquery(Map<String, Object> parameters) {
        parameters.put("nå", LocalDateTime.now());
        return "AND NOT EXISTS (select 1 from Reservasjon r where r.oppgave = o and r.reservertTil > :nå) ";
    }

    private static String tilBeslutter(Oppgavespørring dto, Map<String, Object> parameters) {
        var tilBeslutterKø = dto.getInkluderAndreKriterierTyper().contains(AndreKriterierType.TIL_BESLUTTER);
        if (dto.getForAvdelingsleder() || !tilBeslutterKø) {
            return "";
        }
        parameters.put("tilbeslutter", AndreKriterierType.TIL_BESLUTTER);
        parameters.put("uid", BrukerIdent.brukerIdent().toUpperCase());
        return """
            AND NOT EXISTS (
                select oetilbesl.oppgave from OppgaveEgenskap oetilbesl
                where oetilbesl.oppgave = o
                    AND oetilbesl.andreKriterierType = :tilbeslutter
                    AND upper(oetilbesl.sisteSaksbehandlerForTotrinn) = :uid
            )""";
    }

    private static String datoFilter(Oppgavespørring oppgavespørring, Map<String, Object> parameters) {
        var sortering = oppgavespørring.getSortering();

        if (KøSortering.BELØP.equals(sortering)) {
            // håndteres i beløpFilter-metoden
            return "";
        }

        var feltLiteral = switch (sortering) {
            case BEHANDLINGSFRIST -> BEHANDLINGSFRIST_FELT_SQL;
            case OPPRETT_BEHANDLING -> BEHANDLINGOPPRETTET_FELT_SQL;
            case FØRSTE_STØNADSDAG -> FØRSTE_STØNADSDAG_FELT_SQL;
            case FEILUTBETALINGSTART -> FEILUTBETALINGSTART_FELT_SQL;
            case BELØP -> throw new IllegalArgumentException("Utviklerfeil: beløpsfilter håndteres i annen metode");
        };

        var gjelderKunDatoFelt = Objects.equals(KøSortering.FØRSTE_STØNADSDAG, sortering); // Første stønadsdag er LocalDate i entiteten, øvrige LocalDateTime
        var sbuilder = new StringBuilder();
        if (oppgavespørring.isErDynamiskPeriode()) {
            // Filtrerer på antall dager relativt til i dag.
            // Perioden man ser på kan være i fortid eller fremtid, avhengig av positiv/negativ verdi på filtrerFra/Til
            var fomAntallDagerFrem = oppgavespørring.getFiltrerFra();
            if (fomAntallDagerFrem != null) {
                var aktuellFomDato = LocalDate.now().plusDays(fomAntallDagerFrem);
                putDatoParam(parameters, "filterFom", aktuellFomDato, gjelderKunDatoFelt, true);
                sbuilder.append("AND ").append(feltLiteral).append(" >= :filterFom ");
            }
            var tomAntallDagerFrem = oppgavespørring.getFiltrerTil();
            if (tomAntallDagerFrem != null) {
                var aktuellTomDato = LocalDate.now().plusDays(tomAntallDagerFrem);
                putDatoParam(parameters, "filterTom", aktuellTomDato, gjelderKunDatoFelt, false);
                sbuilder.append("AND ").append(feltLiteral).append(" <= :filterTom ");
            }
        } else {
            // Filtrerer på absolutte datoer
            var fomDato = oppgavespørring.getFiltrerFomDato();
            var tomDato = oppgavespørring.getFiltrerTomDato();
            if (fomDato != null) {
                putDatoParam(parameters, "filterFom", fomDato, gjelderKunDatoFelt, true);
                sbuilder.append("AND ").append(feltLiteral).append(" >= :filterFom ");
            }
            if (tomDato != null) {
                putDatoParam(parameters, "filterTom", tomDato, gjelderKunDatoFelt, false);
                sbuilder.append("AND ").append(feltLiteral).append(" <= :filterTom ");
            }
        }
        return sbuilder.toString();
    }

    private static void putDatoParam(Map<String, Object> p, String parameterNavn, LocalDate d, boolean erKunDato, boolean erStartPåDag) {
        if (erKunDato) {
            p.put(parameterNavn, d);
        } else {
            p.put(parameterNavn, erStartPåDag ? d.atTime(LocalTime.MIN) : d.atTime(LocalTime.MAX));
        }
    }

}
