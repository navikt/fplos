package no.nav.foreldrepenger.los.oppgave;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

import org.hibernate.jpa.HibernateHints;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class BehandlingKøRepository {

    private static final String SELECT_COUNT_FROM_BEHANDLING = "SELECT count(1) from Behandling o ";

    private static final String BEHANDLINGOPPRETTET_FELT_SQL = "o.opprettet";

    private static final Map<KøSortering, Boolean> SORTERING_ER_DATE_FELT = Map.of(
        KøSortering.BEHANDLINGSFRIST, true,
        KøSortering.OPPRETT_BEHANDLING, false,
        KøSortering.FØRSTE_STØNADSDAG, true,
        KøSortering.FØRSTE_STØNADSDAG_SYNKENDE, true,
        KøSortering.FEILUTBETALINGSTART, true,
        KøSortering.BELØP, false
    );

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
            .append(SELECT_COUNT_FROM_BEHANDLING)
            .append(" WHERE o.behandlendeEnhet = :enhetsnummer ")
            .append(" AND o.behandlingTilstand in (:ventTilstand) ")
            .append(OppgaveKøRepository.filtrerBehandlingType(oppgavespørring, parameters))
            .append(OppgaveKøRepository.filtrerYtelseType(oppgavespørring, parameters))
            .append(andreKriterierSubquery(oppgavespørring, parameters))
            .append(OppgaveKøRepository.beløpFilter(oppgavespørring, parameters));

        if (!oppgavespørring.getSortering().getFeltkategori().equals(KøSortering.FeltKategori.OPPGAVE_OPPRETTET)) {
            qlStringBuilder.append(OppgaveKøRepository.datoFilter(oppgavespørring, parameters, SORTERING_ER_DATE_FELT, BEHANDLINGOPPRETTET_FELT_SQL));
        }

        var query = entityManager.createQuery(qlStringBuilder.toString(), Long.class);
        parameters.forEach(query::setParameter);

        query.setHint(HibernateHints.HINT_READ_ONLY, "true");
        oppgavespørring.getMaxAntallOppgaver().ifPresent(max -> query.setMaxResults(max.intValue()));
        return query;
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

}
