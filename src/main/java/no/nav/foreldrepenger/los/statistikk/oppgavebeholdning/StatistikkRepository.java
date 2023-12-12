package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
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
    public List<OppgaverForFørsteStønadsdag> hentOppgaverPerFørsteStønadsdag(String avdeling) {
        // Tilpass til tidligste dato før termin - 18u = 1296. Vurder trunc('IW') + 4 (=fredag) for evt ukesvisning
        return entityManager.createNativeQuery("""
            select ytre.DATO as DATO, sum(ytre.ANTALL) as ANTALL from (
               select case when indre.fstonad < sysdate - 120 then trunc(sysdate-120)
                           when indre.fstonad > sysdate + 126 then trunc(sysdate+126)
                           else indre.fstonad end as DATO, Count(1) AS ANTALL from (
                  select trunc(o.FORSTE_STONADSDAG) as fstonad FROM OPPGAVE o INNER JOIN avdeling a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET
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
