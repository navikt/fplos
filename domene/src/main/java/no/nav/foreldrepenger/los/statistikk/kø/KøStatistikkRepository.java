package no.nav.foreldrepenger.los.statistikk.kø;

import static no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse.LUKKET_OPPGAVE;
import static no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse.OPPGAVE_SATT_PÅ_VENT;
import static no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse.UT_TIL_ANNEN_KØ;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.NyeOgFerdigstilteOppgaver;

@ApplicationScoped
public class KøStatistikkRepository {
    private static final Logger LOG = LoggerFactory.getLogger(KøStatistikkRepository.class);

    private EntityManager entityManager;

    @Inject
    public KøStatistikkRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    KøStatistikkRepository() {
        //CDI
    }

    public void lagre(Long oppgaveId, Long oppgaveFilterSettId, BehandlingType behandlingType, KøOppgaveHendelse køOppgaveHendelse) {
        LOG.info("Lagrer knytning mellom oppgaveId {} og oppgaveFilterSettId {} for statistikk", oppgaveId, oppgaveFilterSettId);
        entityManager.createNativeQuery("INSERT INTO STATISTIKK_KO (ID, OPPGAVE_ID, OPPGAVE_FILTRERING_ID, BEHANDLING_TYPE, HENDELSE) " +
                "VALUES (SEQ_STATISTIKK_KO.nextval, :oppgaveId, :oppgaveFilterSettId, :behandlingType, :hendelse)")
                .setParameter("oppgaveId", oppgaveId)
                .setParameter("oppgaveFilterSettId", oppgaveFilterSettId)
                .setParameter("behandlingType", behandlingType.getKode())
                .setParameter("hendelse", køOppgaveHendelse.name())
                .executeUpdate();
    }

    public List<NyeOgFerdigstilteOppgaver> hentStatistikk(Long oppgaveFilterSettId) {
        final var tellesSomFerdigstilt = Stream.of(LUKKET_OPPGAVE, UT_TIL_ANNEN_KØ, OPPGAVE_SATT_PÅ_VENT)
                .map(KøOppgaveHendelse::name).collect(Collectors.toList());
        var query = entityManager.createNativeQuery(
                """
                        SELECT TRUNC(OPPRETTET_TID), BEHANDLING_TYPE, 0 as ANTALL_NYE, COUNT(1) AS ANTALL_FERDIGSTILTE 
                        FROM STATISTIKK_KO 
                        WHERE OPPGAVE_FILTRERING_ID = :oppgaveFilterSettId 
                            AND OPPRETTET_TID >= :fom
                            AND HENDELSE IN (:tellesSomFerdigstilt) 
                        GROUP BY TRUNC(OPPRETTET_TID), BEHANDLING_TYPE
                        UNION 
                        SELECT TRUNC(OPPRETTET_TID), BEHANDLING_TYPE, COUNT(1) as ANTALL_NYE, 0 AS ANTALL_FERDIGSTILTE 
                        FROM STATISTIKK_KO 
                        WHERE OPPGAVE_FILTRERING_ID = :oppgaveFilterSettId 
                            AND OPPRETTET_TID >= :fom
                            AND HENDELSE NOT IN (:tellesSomFerdigstilt) 
                        GROUP BY TRUNC(OPPRETTET_TID), BEHANDLING_TYPE
                        """)
                .setParameter("oppgaveFilterSettId", oppgaveFilterSettId)
                .setParameter("tellesSomFerdigstilt", tellesSomFerdigstilt)
                .setParameter("fom", LocalDate.now().minusDays(7).atStartOfDay());
        @SuppressWarnings("unchecked")
        var result = (List<Object[]>) query.getResultList();
        var stats = result.stream().map(KøStatistikkRepository::map).collect(Collectors.toList());
        return slåSammen(stats);
    }

    private List<NyeOgFerdigstilteOppgaver> slåSammen(List<NyeOgFerdigstilteOppgaver> stats) {
        var map = new HashMap<Key, NyeOgFerdigstilteOppgaver>();
        for (var nfo : stats) {
            var key = new Key(nfo.dato(), nfo.behandlingType());
            var value = map.getOrDefault(key, new NyeOgFerdigstilteOppgaver(key.dato, key.behandlingType,
                    0L, 0L));
            map.put(key, new NyeOgFerdigstilteOppgaver(key.dato, key.behandlingType, value.antallNye() + nfo.antallNye(),
                    value.antallFerdigstilte() + nfo.antallFerdigstilte()));
        }
        return map.values().stream().toList();
    }

    private static record Key(LocalDate dato, BehandlingType behandlingType) { }

    private static NyeOgFerdigstilteOppgaver map(Object record) {
        var objects = (Object[]) record;
        var datoFra = localDate(objects[0]);
        var behandlingType = BehandlingType.fraKode((String)objects[1]);
        var antallNyeFra = ((BigDecimal)objects[2]).longValue();
        var antallFerdigstilte = ((BigDecimal)objects[3]).longValue();
        return new NyeOgFerdigstilteOppgaver(datoFra, behandlingType, antallNyeFra, antallFerdigstilte);
    }

    private static LocalDate localDate(Object sqlTimestamp) {
        if (sqlTimestamp == null) {
            return null;
        }
        return ((Timestamp) sqlTimestamp).toLocalDateTime().toLocalDate();
    }
}
