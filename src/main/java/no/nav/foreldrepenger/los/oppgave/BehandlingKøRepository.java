package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.jpa.HibernateHints;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

@ApplicationScoped
public class BehandlingKøRepository {

    private static final String SELECT_COUNT_FROM_OPPGAVE = "SELECT count(1) from Behandling o ";

    private static final String BEHANDLINGSFRIST_FELT_SQL = "o.behandlingsfrist";
    private static final String BEHANDLINGOPPRETTET_FELT_SQL = "o.opprettet";
    private static final String FØRSTE_STØNADSDAG_FELT_SQL = "o.førsteStønadsdag";
    private static final String FEILUTBETALINGSTART_FELT_SQL = "o.feilutbetalingStart";
    private static final String FEILUTBETALINGBELOP_FELT_SQL = "o.feilutbetalingBelop";


    private EntityManager entityManager;

    BehandlingKøRepository() { }

    @Inject
    public BehandlingKøRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public int hentAntallBehandlingerPåVent(Oppgavespørring oppgavespørring) {
        return lagAntallVentTypedQuery(oppgavespørring).getSingleResult().intValue();
    }


    private TypedQuery<Long> lagAntallVentTypedQuery(Oppgavespørring oppgavespørring) {
        var parameters = new HashMap<String, Object>();
        parameters.put("enhetsnummer", oppgavespørring.getEnhetsnummer());
        // Alle ventetilstander utenom manglende søknad (har ikke datoer) eller Klageinstans
        parameters.put("ventTilstand", List.of(BehandlingTilstand.VENT_TIDLIG, BehandlingTilstand.VENT_KOMPLETT,
            BehandlingTilstand.VENT_REGISTERDATA, BehandlingTilstand.VENT_KØ, BehandlingTilstand.VENT_MANUELL));


        var qlStringBuilder = new StringBuilder()
            .append(SELECT_COUNT_FROM_OPPGAVE)
            .append(" WHERE o.behandlendeEnhet = :enhetsnummer ")
            .append(" AND o.behandlingTilstand in (:ventTilstand) ")
            .append(filtrerBehandlingType(oppgavespørring, parameters))
            .append(filtrerYtelseType(oppgavespørring, parameters))
            .append(andreKriterierSubquery(oppgavespørring, parameters))
            .append(beløpFilter(oppgavespørring, parameters))
            .append(datoFilter(oppgavespørring, parameters));

        var query = entityManager.createQuery(qlStringBuilder.toString(), Long.class);
        parameters.forEach(query::setParameter);

        query.setHint(HibernateHints.HINT_READ_ONLY, "true");
        oppgavespørring.getMaxAntallOppgaver().ifPresent(max -> query.setMaxResults(max.intValue()));
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
            numeriskFiltrering = "AND " + FEILUTBETALINGBELOP_FELT_SQL + " >= :filterFra ";
            parameters.put("filterFra", fra);
        }
        if (til != null) {
            numeriskFiltrering += "AND "+ FEILUTBETALINGBELOP_FELT_SQL + " <= :filterTil ";
            parameters.put("filterTil", til);
        }
        return numeriskFiltrering;

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
                .append("   FROM BehandlingEgenskap oe ")
                .append("   WHERE oe.behandlingId = o.id ")
                .append("     AND oe.andreKriterierType IN (:inkluderAktKoder)")
                .append(" ) ");
        }
        if (!ekskluderAkt.isEmpty()) {
            parameters.put("ekskluderAktKoder", ekskluderAkt);
            sb.append("AND NOT EXISTS ( ")
                .append("SELECT 1 FROM BehandlingEgenskap oe ")
                .append("WHERE oe.behandlingId = o.id AND oe.andreKriterierType IN (:ekskluderAktKoder)")
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

    private static String datoFilter(Oppgavespørring oppgavespørring, Map<String, Object> parameters) {
        var sortering = oppgavespørring.getSortering();

        if (KøSortering.BELØP.equals(sortering)) {
            // håndteres i beløpFilter-metoden
            return "";
        }

        var feltLiteral = switch (sortering) {
            case BEHANDLINGSFRIST -> BEHANDLINGSFRIST_FELT_SQL;
            case OPPRETT_BEHANDLING -> BEHANDLINGOPPRETTET_FELT_SQL;
            case FØRSTE_STØNADSDAG, FØRSTE_STØNADSDAG_SYNKENDE -> FØRSTE_STØNADSDAG_FELT_SQL;
            case FEILUTBETALINGSTART -> FEILUTBETALINGSTART_FELT_SQL;
            case BELØP -> throw new IllegalArgumentException("Utviklerfeil: beløpsfilter håndteres i annen metode");
        };

        var gjelderKunDatoFelt =
            Objects.equals(KøSortering.FØRSTE_STØNADSDAG, sortering) ||
            Objects.equals(KøSortering.FØRSTE_STØNADSDAG_SYNKENDE, sortering); // Første stønadsdag er LocalDate i entiteten, øvrige LocalDateTime

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
