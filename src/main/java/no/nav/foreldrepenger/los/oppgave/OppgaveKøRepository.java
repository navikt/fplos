package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.jpa.HibernateHints;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import no.nav.foreldrepenger.los.felles.util.BrukerIdent;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

@ApplicationScoped
public class OppgaveKøRepository {

    private static final String SELECT_FROM_OPPGAVE = "from Oppgave o ";
    private static final String SELECT_COUNT_FROM_OPPGAVE = "SELECT count(1) from Oppgave o ";

    private static final String BEHANDLINGSFRIST_FELT_SQL = "o.behandlingsfrist";
    private static final String BEHANDLINGOPPRETTET_FELT_SQL = "o.behandlingOpprettet";
    private static final String FØRSTE_STØNADSDAG_FELT_SQL = "o.førsteStønadsdag";
    private static final String FEILUTBETALINGSTART_FELT_SQL = "o.feilutbetalingStart";
    private static final String FEILUTBETALINGBELOP_FELT_SQL = "o.feilutbetalingBelop";

    private static final String ORDER_BY_BEHANDLINGSFRIST_ASC = "ORDER BY o.behandlingsfrist ASC";
    private static final String ORDER_BY_BEHANDLINGOPPRETTET_ASC = "ORDER BY o.behandlingOpprettet ASC";
    private static final String ORDER_BY_FØRSTE_STØNADSDAG_ASC = "ORDER BY o.førsteStønadsdag ASC";
    private static final String ORDER_BY_FØRSTE_STØNADSDAG_DESC = "ORDER BY o.førsteStønadsdag DESC NULLS LAST";
    private static final String ORDER_BY_FEILUTBETALINGSTART_ASC = "ORDER BY o.feilutbetalingStart ASC";
    private static final String ORDER_BY_FEILUTBETALINGBELOP_DESC = "ORDER BY o.feilutbetalingBelop DESC";
    private static final String ORDER_BY_OPPGAVE_OPPRETTET_ASC = "ORDER BY o.opprettetTidspunkt ASC";

    private static final Map<KøSortering, Boolean> SORTERING_ER_DATE_FELT = Map.of(
        KøSortering.BEHANDLINGSFRIST, false,
        KøSortering.OPPRETT_BEHANDLING, false,
        KøSortering.FØRSTE_STØNADSDAG, true,
        KøSortering.FØRSTE_STØNADSDAG_SYNKENDE, true,
        KøSortering.FEILUTBETALINGSTART, false
    );

    private EntityManager entityManager;

    OppgaveKøRepository() { }

    @Inject
    public OppgaveKøRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public int hentAntallOppgaver(Oppgavespørring oppgavespørring) {
        return lagTypedQuery(oppgavespørring, true, Long.class).getSingleResult().intValue();
    }

    public int hentAntallOppgaverForAvdeling(String enhetsNummer) {
        var oppgavespørring = new Oppgavespørring(
            enhetsNummer,
            KøSortering.BEHANDLINGSFRIST,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Periodefilter.FAST_PERIODE,
            null,
            null,
            null,
            null,
            Filtreringstype.ALLE,
            null,
            null);
        return hentAntallOppgaver(oppgavespørring);
    }

    public List<Oppgave> hentOppgaver(Oppgavespørring oppgavespørring) {
        var query = lagTypedQuery(oppgavespørring,false, Oppgave.class);
        return query.getResultList();
    }

    private <T> TypedQuery<T> lagTypedQuery(Oppgavespørring oppgavespørring, boolean kunCountQuery, Class<T> resultClass) {
        var parameters = new HashMap<String, Object>();
        parameters.put("enhetsnummer", oppgavespørring.getEnhetsnummer());

        var selection = kunCountQuery ? SELECT_COUNT_FROM_OPPGAVE : SELECT_FROM_OPPGAVE;

        var qlStringBuilder = new StringBuilder();
        qlStringBuilder.append(selection);
        qlStringBuilder.append(" WHERE o.behandlendeEnhet = :enhetsnummer ");
        qlStringBuilder.append(filtrerBehandlingType(oppgavespørring, parameters));
        qlStringBuilder.append(filtrerYtelseType(oppgavespørring, parameters));
        qlStringBuilder.append(andreKriterierSubquery(oppgavespørring, parameters));
        qlStringBuilder.append(reserverteSubquery(oppgavespørring, parameters));
        qlStringBuilder.append(filtrerBortEgneBeslutterOppgaver(oppgavespørring, parameters));
        qlStringBuilder.append(aktivFilter(oppgavespørring));
        qlStringBuilder.append(beløpFilter(oppgavespørring, parameters));
        qlStringBuilder.append(datoFilter(oppgavespørring, parameters, SORTERING_ER_DATE_FELT, BEHANDLINGOPPRETTET_FELT_SQL));
        qlStringBuilder.append(opprettetEtterFilter(oppgavespørring, parameters));
        qlStringBuilder.append(avsluttetEtterFilter(oppgavespørring, parameters));

        if (!kunCountQuery) {
            qlStringBuilder.append(orderBy(oppgavespørring));
        }
        var query = entityManager.createQuery(qlStringBuilder.toString(), resultClass);
        parameters.forEach(query::setParameter);

        query.setHint(HibernateHints.HINT_READ_ONLY, "true");
        oppgavespørring.getMaxAntallOppgaver().ifPresent(max -> query.setMaxResults(max.intValue()));
        return query;
    }

    private String avsluttetEtterFilter(Oppgavespørring oppgavespørring, HashMap<String, Object> parameters) {
        return oppgavespørring.getAvsluttetEtter().map(tidspunkt -> {
            parameters.put("tidspunkt", tidspunkt);
            return "AND o.oppgaveAvsluttet > :tidspunkt ";
        }).orElse("");
    }

    private String opprettetEtterFilter(Oppgavespørring oppgavespørring, HashMap<String, Object> parameters) {
        return oppgavespørring.getOpprettetEtter().map(tidspunkt -> {
            parameters.put("tidspunkt", tidspunkt);
            return "AND o.opprettetTidspunkt > :tidspunkt ";
        }).orElse("");
    }

    private static String aktivFilter(Oppgavespørring oppgavespørring) {
        return oppgavespørring.skalBareTelleAktive() ? " AND o.aktiv = true " : "";
    }

    static String beløpFilter(Oppgavespørring oppgavespørring, Map<String, Object> parameters) {
        if (!KøSortering.BELØP.equals(oppgavespørring.getSortering())) {
            return "";
        }
        var fra = oppgavespørring.getFiltrerFra();
        var til = oppgavespørring.getFiltrerTil();
        var numeriskFiltrering = "";
        if (fra != null) {
            numeriskFiltrering = "AND " + FEILUTBETALINGBELOP_FELT_SQL + " >= :filterFra ";
            parameters.put("filterFra", fra);
        }
        if (til != null) {
            numeriskFiltrering += "AND "+ FEILUTBETALINGBELOP_FELT_SQL + " <= :filterTil ";
            parameters.put("filterTil", til);
        }
        return numeriskFiltrering;

    }

    private static String orderBy(Oppgavespørring queryDto) {
        return switch (queryDto.getSortering()) {
            case BEHANDLINGSFRIST -> ORDER_BY_BEHANDLINGSFRIST_ASC;
            case OPPRETT_BEHANDLING -> ORDER_BY_BEHANDLINGOPPRETTET_ASC;
            case FØRSTE_STØNADSDAG -> ORDER_BY_FØRSTE_STØNADSDAG_ASC;
            case FØRSTE_STØNADSDAG_SYNKENDE -> ORDER_BY_FØRSTE_STØNADSDAG_DESC;
            case BELØP -> ORDER_BY_FEILUTBETALINGBELOP_DESC;
            case FEILUTBETALINGSTART -> ORDER_BY_FEILUTBETALINGSTART_ASC;
            case OPPGAVE_OPPRETTET -> ORDER_BY_OPPGAVE_OPPRETTET_ASC;
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

    static String filtrerBehandlingType(Oppgavespørring queryDto, Map<String, Object> parameters) {
        if (queryDto.getBehandlingTyper().isEmpty()) {
            return "";
        }
        parameters.put("behtyper", queryDto.getBehandlingTyper());
        return "AND o.behandlingType in :behtyper ";
    }

    static String filtrerYtelseType(Oppgavespørring queryDto, Map<String, Object> parameters) {
        if (queryDto.getYtelseTyper().isEmpty()) {
            return "";
        }
        parameters.put("fagsakYtelseType", queryDto.getYtelseTyper());
        return "AND o.fagsakYtelseType in :fagsakYtelseType ";
    }

    private static String reserverteSubquery(Oppgavespørring oppgavespørring, Map<String, Object> parameters) {
        if (Filtreringstype.ALLE.equals(oppgavespørring.getFiltreringstype())) {
            return "";
        }
        parameters.put("nå", LocalDateTime.now());
        return "AND NOT EXISTS (select 1 from Reservasjon r where r.oppgave = o and r.reservertTil > :nå) ";
    }

    private static String filtrerBortEgneBeslutterOppgaver(Oppgavespørring dto, Map<String, Object> parameters) {
        var tilBeslutterKø = dto.getInkluderAndreKriterierTyper().contains(AndreKriterierType.TIL_BESLUTTER);
        if (!tilBeslutterKø || Filtreringstype.ALLE.equals(dto.getFiltreringstype())) {
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

    static String datoFilter(Oppgavespørring oppgavespørring, Map<String, Object> parameters,
                             Map<KøSortering, Boolean> datemap, String behandlingOpprettetSQL) {
        var sortering = oppgavespørring.getSortering();

        if (KøSortering.BELØP.equals(sortering) || KøSortering.OPPGAVE_OPPRETTET.equals(sortering)) {
            // KøSortering.BELØP håndteres i beløpFilter-metoden, KøSortering.OPPRETT_BEHANDLING brukes til beslutterkøer uten filter
            return "";
        }

        var feltLiteral = switch (sortering) {
            case BEHANDLINGSFRIST -> BEHANDLINGSFRIST_FELT_SQL;
            case OPPRETT_BEHANDLING -> behandlingOpprettetSQL;
            case FØRSTE_STØNADSDAG, FØRSTE_STØNADSDAG_SYNKENDE -> FØRSTE_STØNADSDAG_FELT_SQL;
            case FEILUTBETALINGSTART -> FEILUTBETALINGSTART_FELT_SQL;
            case BELØP, OPPGAVE_OPPRETTET -> throw new IllegalArgumentException("Utviklerfeil: beløpsfilter håndteres i annen metode");
        };

        var gjelderKunDatoFelt = datemap.getOrDefault(sortering, Boolean.FALSE);

        var sbuilder = new StringBuilder();


        if (oppgavespørring.getPeriodefilter() == Periodefilter.RELATIV_PERIODE_DAGER) {
            // Filtrerer på antall dager relativt til i dag.
            // Perioden man ser på kan være i fortid eller fremtid, avhengig av positiv/negativ verdi på filtrerFra/Til
            if (oppgavespørring.getFiltrerFra() != null) {
                var fomDato = LocalDate.now().plusDays(oppgavespørring.getFiltrerFra());
                putDatoParam(parameters, "filterFom", fomDato, gjelderKunDatoFelt, true);
                sbuilder.append("AND ").append(feltLiteral).append(" >= :filterFom ");
            }
            if (oppgavespørring.getFiltrerTil() != null) {
                var tomDato = LocalDate.now().plusDays(oppgavespørring.getFiltrerTil());
                putDatoParam(parameters, "filterTom", tomDato, gjelderKunDatoFelt, false);
                sbuilder.append("AND ").append(feltLiteral).append(" <= :filterTom ");
            }

        } else if (oppgavespørring.getPeriodefilter() == Periodefilter.RELATIV_PERIODE_MÅNEDER) {
            // Filtrerer på antall måneder relativt til inneværende måned.
            // Perioden man ser på kan være i fortid eller fremtid, avhengig av positiv/negativ verdi på filtrerFra/Til
            if (oppgavespørring.getFiltrerFra() != null) {
                var fomDato = YearMonth.now().plusMonths(oppgavespørring.getFiltrerFra()).atDay(1);
                putDatoParam(parameters, "filterFom", fomDato, gjelderKunDatoFelt, true);
                sbuilder.append("AND ").append(feltLiteral).append(" >= :filterFom ");
            }
            if (oppgavespørring.getFiltrerTil() != null) {
                var tomDato = YearMonth.now().plusMonths(oppgavespørring.getFiltrerTil()).atEndOfMonth();
                putDatoParam(parameters, "filterTom", tomDato, gjelderKunDatoFelt, false);
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
