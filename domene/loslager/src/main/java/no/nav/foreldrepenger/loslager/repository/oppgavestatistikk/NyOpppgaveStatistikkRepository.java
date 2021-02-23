package no.nav.foreldrepenger.loslager.repository.oppgavestatistikk;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class NyOpppgaveStatistikkRepository {
    private static final Logger LOG = LoggerFactory.getLogger(NyOpppgaveStatistikkRepository.class);

    private EntityManager entityManager;

    @Inject
    public NyOpppgaveStatistikkRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public NyOpppgaveStatistikkRepository() {
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

    public List<KøStatistikk> hentStatistikk(Long oppgaveFilterSettId) {
        var query = entityManager.createNativeQuery(
                "SELECT TRUNC(OPPRETTET_TID), BEHANDLING_TYPE, HENDELSE, COUNT(1) AS ANTALL " +
                "FROM STATISTIKK_KO " +
                "WHERE OPPGAVE_FILTRERING_ID = :oppgaveFilterSettId " +
                        "AND OPPRETTET_TID >= :fom " +
                        "GROUP BY TRUNC(OPPRETTET_TID), BEHANDLING_TYPE, HENDELSE")
                .setParameter("oppgaveFilterSettId", oppgaveFilterSettId)
                .setParameter("fom", LocalDate.now().minusDays(7).atStartOfDay());
        @SuppressWarnings("unchecked")
        var result = (List<Object[]>) query.getResultList();
        return result.stream()
                .map(NyOpppgaveStatistikkRepository::map)
                .collect(Collectors.toList());
    }

    private static KøStatistikk map(Object record) {
        Object[] feltArray = (Object[]) record;
        var dato = localDate(feltArray[0]);
        var behandlingType = BehandlingType.fraKode((String) feltArray[1]);
        var hendelse = KøOppgaveHendelse.valueOf((String) feltArray[2]);
        var antall = ((BigDecimal) feltArray[3]).longValue();
        return new KøStatistikk(dato, behandlingType, hendelse, antall);
    }

    private static LocalDate localDate(Object sqlTimestamp) {
        if (sqlTimestamp == null) {
            return null;
        }
        return ((Timestamp) sqlTimestamp).toLocalDateTime().toLocalDate();
    }
}
