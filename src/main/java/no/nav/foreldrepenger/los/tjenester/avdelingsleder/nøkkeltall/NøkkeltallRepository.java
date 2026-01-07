package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdeling;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForFørsteStønadsdagUkeMåned;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@ApplicationScoped
public class NøkkeltallRepository {

    private static final String AVDELING_ENHET = "avdelingEnhet";
    private static final String TIL_BESLUTTER = "tilBeslutter";

    private EntityManager entityManager;

    @Inject
    public NøkkeltallRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    NøkkeltallRepository() {
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
    public List<OppgaverForFørsteStønadsdagUkeMåned> hentOppgaverPerFørsteStønadsdagMåned(String avdeling) {
        return entityManager.createNativeQuery("""
            select trunc(ytre.DATO, 'MM') as DATO, YTELSE, sum(ytre.ANTALL) as ANTALL from (
               select case when indre.fstonad < sysdate - 245 then trunc(sysdate-245)
                           when indre.fstonad > sysdate + 126 then trunc(sysdate+126)
                           else indre.fstonad end as DATO, YTELSE, Count(1) AS ANTALL from (
                  select trunc(o.FORSTE_STONADSDAG) as fstonad, o.FAGSAK_YTELSE_TYPE as YTELSE
                  FROM OPPGAVE o INNER JOIN avdeling a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET
                  WHERE a.AVDELING_ENHET = :avdelingEnhet AND o.AKTIV='J' AND o.FORSTE_STONADSDAG IS NOT NULL and o.behandling_type = :behandlingType
               ) indre GROUP BY indre.fstonad, YTELSE
            ) ytre
            group by trunc(ytre.DATO, 'MM'), YTELSE
            order by trunc(ytre.DATO, 'MM'), YTELSE
            """).setParameter(AVDELING_ENHET, avdeling)
            .setParameter("behandlingType", BehandlingType.FØRSTEGANGSSØKNAD.getKode())
            .getResultStream().map(row -> mapOppgaverForFørsteStønadsdagUkeMåned(avdeling, (Object[]) row)).toList();
    }

    private static OppgaverForFørsteStønadsdagUkeMåned mapOppgaverForFørsteStønadsdagUkeMåned(String avdeling, Object[] row) {
        var date = ((LocalDateTime) row[0]).toLocalDate();
        var ytelse = FagsakYtelseType.fraKode((String) row[1]); // NOSONAR
        var resultat = ((BigDecimal) row[2]).longValue();
        return new OppgaverForFørsteStønadsdagUkeMåned(avdeling, ytelse, date, resultat);
    }

    private static OppgaverForAvdeling mapOppgaverForAvdeling(Object[] row) {
        var fagsakYtelseType = FagsakYtelseType.fraKode((String) row[0]); // NOSONAR
        var behandlingType = BehandlingType.fraKode((String) row[1]); // NOSONAR
        var tilBeslutter = new BooleanToStringConverter().convertToEntityAttribute(Character.toString((Character) row[2])); // NOSONAR
        var antall = ((BigDecimal) row[3]).longValue(); // NOSONAR
        return new OppgaverForAvdeling(fagsakYtelseType, behandlingType, !tilBeslutter, antall);
    }
}
