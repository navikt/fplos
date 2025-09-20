package no.nav.foreldrepenger.los.oppgave;

import static no.nav.foreldrepenger.los.oppgave.OppgaveRepository.COUNT_FRA_OPPGAVE;
import static no.nav.foreldrepenger.los.oppgave.OppgaveRepository.COUNT_FRA_TILBAKEKREVING_OPPGAVE;

import java.math.BigDecimal;
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
    private static final String BEHANDLINGOPPRETTET_FELT_SQL = "o.behandlingOpprettet";
    private static final String FØRSTE_STØNADSDAG_FELT_SQL = "o.førsteStønadsdag";
    private static final String BELØP_FELT_SQL = "o.belop";
    private static final String FEILUTBETALINGSTART_FELT_SQL = "o.feilutbetalingstart";

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
        sb.append(sortering(selection, queryDto, parameters)); // TODO: skille order by og filter, hvis ikke blir det feil i antall query!

        var query = entityManager.createQuery(sb.toString(), resultClass);
        parameters.forEach(query::setParameter);

        return query;
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

    private static String sortering(String selection, Oppgavespørring oppgavespørring, HashMap<String, Object> parameters) {
        if (selection.equals(COUNT_FRA_OPPGAVE) || selection.equals(COUNT_FRA_TILBAKEKREVING_OPPGAVE)) {
            return "";
        }
        var sortering = oppgavespørring.getSortering();

        // Oppsettet for sortering av køen inneholder også et de facto filter.
        // Filteret kan enten være feilutbetalt beløp eller dato (x antall dager relativt til dd eller absolutte datoer)
        // Spesielt for dynamisk filter: x kan også være negativ for å filtrere tilbake i tid.
        if (KøSortering.FT_HELTALL.equalsIgnoreCase(oppgavespørring.getSortering().getFelttype())) {
            // her er vi i beløpsvarianten
            if (oppgavespørring.getFiltrerFra() != null) {
                parameters.put("filterFra", BigDecimal.valueOf(oppgavespørring.getFiltrerFra()));
            }
            if (oppgavespørring.getFiltrerTil() != null) {
                parameters.put("filterTil", BigDecimal.valueOf(oppgavespørring.getFiltrerTil()));
            }
        } else if (KøSortering.FT_DATO.equalsIgnoreCase(oppgavespørring.getSortering().getFelttype())) {
            // her er vi i dato-varianten, først ut dynamisk variant relativt til i dag
            if (oppgavespørring.getFiltrerFra() != null) {
                // Hvorfor sjekk mot KøSortering.FØRSTE_STØNADSDAG? Første_stønadsdag er LocalDate, øvrige LocalDateTime.
                // TODO: Vurdere å migrere feltet til LocalDateTime for å unngå slalåm, komplisert nok dette.
                var filterFomDager = LocalDate.now().plusDays(oppgavespørring.getFiltrerFra());
                if (Objects.equals(KøSortering.FØRSTE_STØNADSDAG, oppgavespørring.getSortering())) {
                    parameters.put("filterFomDager", filterFomDager);
                } else {
                    // vi inkluderer hele dagen når vi er på LocalDateTime-varianten (dvs øvrige KøSorteringer)
                    parameters.put("filterFomDager", filterFomDager.atStartOfDay());
                }
            }
            if (oppgavespørring.getFiltrerTil() != null) {
                var filterTomDager = LocalDate.now().plusDays(oppgavespørring.getFiltrerTil());
                if (Objects.equals(KøSortering.FØRSTE_STØNADSDAG, oppgavespørring.getSortering())) {
                    parameters.put("filterTomDager", filterTomDager);
                } else {
                    // det er til og med
                    parameters.put("filterTomDager", filterTomDager.atTime(LocalTime.MAX));
                }
            }

            // så den statiske varianten med absolutte datoer
            if (oppgavespørring.getFiltrerFomDato() != null) {
                var filterFomDato = oppgavespørring.getFiltrerFomDato();
                if (Objects.equals(KøSortering.FØRSTE_STØNADSDAG, oppgavespørring.getSortering())) {
                    parameters.put("filterFomDato", filterFomDato);
                } else {
                    parameters.put("filterFomDato", filterFomDato.atTime(LocalTime.MIN));
                }
            }
            if (oppgavespørring.getFiltrerTomDato() != null) {
                var filterTomDato = oppgavespørring.getFiltrerTomDato();
                if (Objects.equals(KøSortering.FØRSTE_STØNADSDAG, oppgavespørring.getSortering())) {
                    parameters.put("filterTomDato", filterTomDato);
                } else {
                    parameters.put("filterTomDato", filterTomDato.atTime(LocalTime.MIN));
                }
            }
        }

        // TODO: slå sammen oppsett her med parameteroppsett over
        if (KøSortering.BEHANDLINGSFRIST.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(BEHANDLINGSFRIST_FELT_SQL, oppgavespørring.getFiltrerFra(),
                oppgavespørring.getFiltrerTil()) : filtrerStatisk(BEHANDLINGSFRIST_FELT_SQL, oppgavespørring.getFiltrerFomDato(),
                oppgavespørring.getFiltrerTomDato());
        }
        if (KøSortering.OPPRETT_BEHANDLING.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(BEHANDLINGOPPRETTET_FELT_SQL, oppgavespørring.getFiltrerFra(),
                oppgavespørring.getFiltrerTil()) : filtrerStatisk(BEHANDLINGOPPRETTET_FELT_SQL, oppgavespørring.getFiltrerFomDato(),
                oppgavespørring.getFiltrerTomDato());
        }
        if (KøSortering.FØRSTE_STØNADSDAG.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(FØRSTE_STØNADSDAG_FELT_SQL, oppgavespørring.getFiltrerFra(),
                oppgavespørring.getFiltrerTil()) : filtrerStatisk(FØRSTE_STØNADSDAG_FELT_SQL, oppgavespørring.getFiltrerFomDato(),
                oppgavespørring.getFiltrerTomDato());
        }
        if (KøSortering.BELØP.equals(sortering)) {
            return filtrerNumerisk(BELØP_FELT_SQL, oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil());
        }
        if (KøSortering.FEILUTBETALINGSTART.equals(sortering)) {
            if (oppgavespørring.isErDynamiskPeriode()) {
                return filtrerDynamisk(FEILUTBETALINGSTART_FELT_SQL, oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil());
            } else {
                return filtrerStatisk(FEILUTBETALINGSTART_FELT_SQL, oppgavespørring.getFiltrerFomDato(), oppgavespørring.getFiltrerTomDato());
            }
        }
        return ORDER_BY_SQL + BEHANDLINGOPPRETTET_FELT_SQL;
    }

    private static String filtrerNumerisk(String felt, Long fra, Long til) {
        var numeriskFiltrering = "";
        if (fra != null && til != null) {
            numeriskFiltrering = "AND " + felt + " BETWEEN :filterFra AND :filterTil ";
        } else if (fra != null) {
            numeriskFiltrering = "AND " + felt + " >= :filterFra ";
        } else if (til != null) {
            numeriskFiltrering = "AND " + felt + " <= :filterTil ";
        }
        return numeriskFiltrering + ORDER_BY_SQL + felt + DESC_SQL;
    }

    private static String filtrerDynamisk(String felt, Long fomDager, Long tomDager) {
        var datoFiltrering = "";
        if (fomDager != null && tomDager != null) {
            datoFiltrering = "AND " + felt + " BETWEEN :filterFomDager AND :filterTomDager ";
        } else if (fomDager != null) {
            datoFiltrering = "AND " + felt + " > :filterFomDager ";
        } else if (tomDager != null) {
            datoFiltrering = "AND " + felt + " < :filterTomDager ";
        }
        return datoFiltrering + ORDER_BY_SQL + felt;
    }

    private static String filtrerStatisk(String felt, LocalDate fomDato, LocalDate tomDato) {
        var datoFiltrering = "";
        if (fomDato != null && tomDato != null) {
            datoFiltrering = "AND " + felt + " BETWEEN :filterFomDato AND :filterTomDato ";
        }  if (fomDato != null) {
            datoFiltrering += "AND " + felt + " >= :filterFomDato ";
        }  if (tomDato != null) {
            datoFiltrering += "AND " + felt + " <= :filterTomDato ";
        }
        return datoFiltrering + ORDER_BY_SQL + felt;
    }
}
