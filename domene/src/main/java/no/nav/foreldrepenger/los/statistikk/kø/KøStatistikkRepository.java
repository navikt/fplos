package no.nav.foreldrepenger.los.statistikk.kø;

import static no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse.LUKKET_OPPGAVE;
import static no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse.OPPGAVE_SATT_PÅ_VENT;
import static no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse.UT_TIL_ANNEN_KØ;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
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
        var query = entityManager.createNativeQuery("""
                with cte as (
                    select distinct
                    trunc(kø.opprettet_tid) as dato,
                    kø.behandling_type,
                    case when kø.hendelse in :tellesSomFerdigstilt
                        then 'ferdigstilte'
                        else 'nye'
                    end as res,
                    o.behandling_id as behandling_id
                    FROM STATISTIKK_KO kø
                    join oppgave o on o.id = kø.oppgave_id
                    WHERE kø.OPPGAVE_FILTRERING_ID = :oppgaveFilterSettId
                    AND kø.OPPRETTET_TID >= to_timestamp(current_date - interval '7' day)
                )
                select dato, behandling_type,
                count(case when res = 'nye' then 1 end) nye,
                count(case when res = 'ferdigstilte' then 1 end) ferdigstilte
                from cte
                group by dato, behandling_type
                """)
                .setParameter("oppgaveFilterSettId", oppgaveFilterSettId)
                .setParameter("tellesSomFerdigstilt", tellesSomFerdigstilt);
        @SuppressWarnings("unchecked")
        var result = (List<NyeOgFerdigstilteOppgaver>) query.getResultStream()
                .map(KøStatistikkRepository::map)
                .collect(Collectors.toList());
        return result;
    }

    private static NyeOgFerdigstilteOppgaver map(Object record) {
        var objects = (Object[]) record;
        var datoFra = localDate(objects[0]);
        var behandlingType = BehandlingType.fraKode((String)objects[1]);
        var antallNye = ((BigDecimal)objects[2]).longValue();
        var antallFerdigstilte = ((BigDecimal)objects[3]).longValue();
        return new NyeOgFerdigstilteOppgaver(datoFra, behandlingType, antallNye, antallFerdigstilte);
    }

    private static LocalDate localDate(Object sqlTimestamp) {
        if (sqlTimestamp == null) {
            return null;
        }
        return ((Timestamp) sqlTimestamp).toLocalDateTime().toLocalDate();
    }
}
