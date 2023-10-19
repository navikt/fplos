package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@ApplicationScoped
public class StatistikkRepository {

    private static final String AVDELING_ENHET = "avdelingEnhet";
    private static final String TIL_BESLUTTER = "tilBeslutter";

    private EntityManager entityManager;

    @Inject
    public StatistikkRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    StatistikkRepository() {
        //CDI
    }

    @SuppressWarnings("unchecked")
    public List<OppgaverForAvdeling> hentAlleOppgaverForAvdeling(String avdelingEnhet) {
        return entityManager.createNativeQuery("""
                Select o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE, nvl2(oe.ANDRE_KRITERIER_TYPE,'J','N') AS BESLUTTER_JN, Count(o.ID) AS ANTALL
                FROM OPPGAVE o INNER JOIN avdeling a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET
                LEFT JOIN OPPGAVE_EGENSKAP oe ON oe.OPPGAVE_ID = o.ID AND oe.ANDRE_KRITERIER_TYPE = :tilBeslutter
                WHERE a.AVDELING_ENHET =:avdelingEnhet AND o.AKTIV='J'
                GROUP BY o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE, oe.ANDRE_KRITERIER_TYPE
                ORDER BY o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE, oe.ANDRE_KRITERIER_TYPE
                """)
            .setParameter(AVDELING_ENHET, avdelingEnhet)
            .setParameter(TIL_BESLUTTER, AndreKriterierType.TIL_BESLUTTER.getKode())
            .getResultStream()
            .map(row -> mapOppgaverForAvdeling((Object[]) row))
            .toList();
    }

    @SuppressWarnings("unchecked")
    public List<OppgaverForAvdelingPerDato> hentAlleOppgaverForAvdelingPerDato(String avdelingEnhet) {
        return entityManager.createNativeQuery("""
            Select o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE, datoer.dato, Count(1) AS ANTALL
            FROM (select trunc(sysdate) + rownum -28 as dato from all_objects where rownum <= (trunc(sysdate) - trunc(sysdate-28) )) datoer,
            OPPGAVE o INNER JOIN avdeling a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET
            WHERE a.AVDELING_ENHET =:avdelingEnhet
            AND trunc(datoer.dato) >= trunc(o.OPPRETTET_TID)
            AND NOT (o.AKTIV='N' AND trunc(datoer.dato) > trunc(o.ENDRET_TID))
            GROUP BY datoer.dato, o.BEHANDLING_TYPE, o.FAGSAK_YTELSE_TYPE
            ORDER BY datoer.dato, o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE
            """).setParameter(AVDELING_ENHET, avdelingEnhet).getResultStream().map(row -> mapOppgaverForAvdelingPerDato((Object[]) row)).toList();
    }

    @SuppressWarnings("unchecked")
    public List<OppgaverForAvdelingSattManueltPåVent> hentAntallOppgaverForAvdelingSattManueltPåVent(String avdelingEnhet) {
        return entityManager.createNativeQuery("""
                select COALESCE(trunc(oel.FRIST_TID), trunc(oel.OPPRETTET_TID + 28)) ESTIMERT_FRIST, o.FAGSAK_YTELSE_TYPE, count(1) as ANTALL
                from oppgave_event_logg oel
                join (
                    select behandling_id, fagsak_ytelse_type
                    from oppgave
                    where BEHANDLENDE_ENHET = :behandlendeEnhet
                    group by behandling_id, fagsak_ytelse_type
                    ) o on o.behandling_id = oel.behandling_id
                where oel.event_type = :eventType
                and oel.opprettet_tid > systimestamp - 90
                and not exists (
                    select 1
                    from oppgave_event_logg oel_nyere
                    where oel_nyere.behandling_id = oel.behandling_id
                    and oel_nyere.opprettet_tid > oel.opprettet_tid
                    and oel_nyere.opprettet_tid > systimestamp - 90
                )
                group by COALESCE(trunc(oel.FRIST_TID), trunc(oel.OPPRETTET_TID + 28)), o.fagsak_ytelse_type
                """)
            .setParameter("behandlendeEnhet", avdelingEnhet)
            .setParameter("eventType", OppgaveEventType.MANU_VENT.name())
            .getResultStream()
            .map(row -> mapOppgaverForAvdelingSattManueltPåVent((Object[]) row))
            .toList();
    }

    @SuppressWarnings("unchecked")
    public List<OppgaverForFørsteStønadsdag> hentOppgaverPerFørsteStønadsdag(String avdeling) {
        return entityManager.createNativeQuery("""
            select ytre.DATO as DATO, sum(ytre.ANTALL) as ANTALL from (
               select case when indre.fstonad < sysdate - 180 then trunc(sysdate-180, 'IW') + 4
                           when indre.fstonad > sysdate + 300 then trunc(sysdate+300, 'IW') + 4
                           else indre.fstonad end as DATO, Count(1) AS ANTALL from (
                  select trunc(o.FORSTE_STONADSDAG, 'IW') + 4 as fstonad FROM OPPGAVE o INNER JOIN avdeling a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET
                  WHERE a.AVDELING_ENHET = :avdelingEnhet AND NOT o.AKTIV='N' AND o.FORSTE_STONADSDAG IS NOT NULL and o.behandling_type = :behandlingType
               ) indre GROUP BY indre.fstonad
            ) ytre group by dato order by dato
            """).setParameter(AVDELING_ENHET, avdeling)
            .setParameter("behandlingType", BehandlingType.FØRSTEGANGSSØKNAD.getKode())
            .getResultStream().map(row -> mapOppgaverForFørsteStønadsdag((Object[]) row)).toList();
    }

    private static OppgaverForFørsteStønadsdag mapOppgaverForFørsteStønadsdag(Object[] row) {
        var date = ((Date) row[0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        var resultat = ((BigDecimal) row[1]).longValue();
        return new OppgaverForFørsteStønadsdag(date, resultat);
    }

    private static OppgaverForAvdelingSattManueltPåVent mapOppgaverForAvdelingSattManueltPåVent(Object[] row) {
        var fagsakYtelseType = FagsakYtelseType.fraKode((String) row[1]);  // NOSONAR
        var estimertFrist = ((Date) row[0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  // NOSONAR
        var antall = ((BigDecimal) row[2]).longValue();  // NOSONAR
        return new OppgaverForAvdelingSattManueltPåVent(fagsakYtelseType, estimertFrist, antall);
    }

    private static OppgaverForAvdeling mapOppgaverForAvdeling(Object[] row) {
        var fagsakYtelseType = FagsakYtelseType.fraKode((String) row[0]); // NOSONAR
        var behandlingType = BehandlingType.fraKode((String) row[1]); // NOSONAR
        var tilBeslutter = new BooleanToStringConverter().convertToEntityAttribute(Character.toString((Character) row[2])); // NOSONAR
        var antall = ((BigDecimal) row[3]).longValue(); // NOSONAR
        return new OppgaverForAvdeling(fagsakYtelseType, behandlingType, !tilBeslutter, antall);
    }

    private static OppgaverForAvdelingPerDato mapOppgaverForAvdelingPerDato(Object[] row) {
        var fagsakYtelseType = FagsakYtelseType.fraKode((String) row[0]);  // NOSONAR
        var behandlingType = BehandlingType.fraKode((String) row[1]);  // NOSONAR
        var opprettetDato = ((Date) row[2]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  // NOSONAR
        var antall = ((BigDecimal) row[3]).longValue();  // NOSONAR
        return new OppgaverForAvdelingPerDato(fagsakYtelseType, behandlingType, opprettetDato, antall);
    }
}
