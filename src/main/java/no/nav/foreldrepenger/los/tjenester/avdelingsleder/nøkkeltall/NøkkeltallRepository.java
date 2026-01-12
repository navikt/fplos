package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingTilstand;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.BehandlingVenteStatus;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingFørsteUttakDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingVentefristUtløperDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdeling;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForFørsteStønadsdagUkeMåned;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@ApplicationScoped
public class NøkkeltallRepository {

    private static final String AVDELING_ENHET = "avdelingEnhet";
    private static final String TIL_BESLUTTER = "tilBeslutter";

    private static final List<String> AKTUELL_VENT_FØRSTEGANG = Stream.of(BehandlingTilstand.VENT_TIDLIG, BehandlingTilstand.VENT_KOMPLETT,
            BehandlingTilstand.VENT_REGISTERDATA,BehandlingTilstand.VENT_MANUELL)
        .map(BehandlingTilstand::getKode)
        .toList();

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

    private static OppgaverForAvdeling mapOppgaverForAvdeling(Object[] row) {
        var fagsakYtelseType = FagsakYtelseType.fraKode((String) row[0]); // NOSONAR
        var behandlingType = BehandlingType.fraKode((String) row[1]); // NOSONAR
        var tilBeslutter = new BooleanToStringConverter().convertToEntityAttribute(Character.toString((Character) row[2])); // NOSONAR
        var antall = ((BigDecimal) row[3]).longValue(); // NOSONAR
        return new OppgaverForAvdeling(fagsakYtelseType, behandlingType, !tilBeslutter, antall);
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
            .getResultStream().map(row -> mapOppgaverForFørsteStønadsdagUkeMåned((Object[]) row)).toList();
    }

    private static OppgaverForFørsteStønadsdagUkeMåned mapOppgaverForFørsteStønadsdagUkeMåned(Object[] row) {
        var date = ((LocalDateTime) row[0]).toLocalDate();
        var ytelse = FagsakYtelseType.fraKode((String) row[1]); // NOSONAR
        var resultat = ((BigDecimal) row[2]).longValue();
        return new OppgaverForFørsteStønadsdagUkeMåned(ytelse, date, resultat);
    }

    @SuppressWarnings("unchecked")
    public List<NøkkeltallBehandlingVentefristUtløperDto> hentVentefristUkefordelt(String avdeling) {
        return entityManager.createNativeQuery("""
            select yt, to_char(frist, 'IYYY-IW'), count(1) as ant from (
                select yt, case when fristi <= sysdate then trunc(sysdate + 1, 'IW')
                                when fristi > sysdate + 245 then trunc(sysdate+245, 'IW')
                                else trunc(fristi, 'IW') end as frist
                from (
                    select FAGSAK_YTELSE_TYPE as yt, trunc(VENTEFRIST) as fristi
                    from behandling b
                    where b.behandling_tilstand in (:ventetilstander)
                      and b.behandling_type = :behandlingType
                      and b.behandlende_enhet = :avdelingEnhet
                )
            )
            group by yt, to_char(frist, 'IYYY-IW')
            """)
            .setParameter(AVDELING_ENHET, avdeling)
            .setParameter("behandlingType", BehandlingType.FØRSTEGANGSSØKNAD.getKode())
            .setParameter("ventetilstander", AKTUELL_VENT_FØRSTEGANG)
            .getResultStream().map(row -> mapVentefristUkefordelt((Object[]) row)).toList();
    }

    private static NøkkeltallBehandlingVentefristUtløperDto mapVentefristUkefordelt(Object[] queryResultat) {
        var ytelseType = FagsakYtelseType.fraKode((String) queryResultat[0]);
        var fristUke = (String) queryResultat[1];
        var antall = (BigDecimal) queryResultat[2];
        return new NøkkeltallBehandlingVentefristUtløperDto(ytelseType, fristUke, antall.longValue());
    }

    public List<NøkkeltallBehandlingFørsteUttakDto> hentBehandlingMånedsfordeltStønadsdato(String avdeling) {
        return entityManager.createNativeQuery("""
            select beh.behandling_type as btype, beh.ventestatus as på_vent, beh.tidligste_fom as dato, count(1) as antall
            from (
                select b.id, b.behandling_type,
                    case when FORSTE_STONADSDAG < sysdate - 180 then trunc(sysdate - 180, 'MM')
                         when FORSTE_STONADSDAG > sysdate + 300 then trunc(sysdate + 300, 'MM')
                         else trunc(FORSTE_STONADSDAG, 'MM') end as tidligste_fom,
                    case when b.behandling_tilstand like 'VENT%' then 'PÅ_VENT' else 'IKKE_PÅ_VENT' end as ventestatus
                from behandling b
                where b.behandling_tilstand not in (:utelatteTilstander)
                  and b.FAGSAK_YTELSE_TYPE = :ytelseType
                  and b.behandlende_enhet = :avdelingEnhet
            ) beh
            group by beh.behandling_type, beh.ventestatus, beh.tidligste_fom
            """)
            .setParameter(AVDELING_ENHET, avdeling)
            .setParameter("ytelseType", FagsakYtelseType.FORELDREPENGER.getKode())
            .setParameter("utelatteTilstander", List.of(BehandlingTilstand.AVSLUTTET.getKode(), BehandlingTilstand.VENT_SØKNAD.getKode()))
            .getResultStream().map(row -> mapBehandlingMånedsfordeltStønadsdato((Object[]) row)).toList();
    }

    private static NøkkeltallBehandlingFørsteUttakDto mapBehandlingMånedsfordeltStønadsdato(Object[] queryResultat) {
        var behandlingType = BehandlingType.fraKode((String) queryResultat[0]);
        var påVent = BehandlingVenteStatus.PÅ_VENT.getKode().equals(queryResultat[1]) ? BehandlingVenteStatus.PÅ_VENT : BehandlingVenteStatus.IKKE_PÅ_VENT;
        var førsteUttaksMåned = localDate(queryResultat[2]);
        var antall = (BigDecimal) queryResultat[3];
        return new NøkkeltallBehandlingFørsteUttakDto(behandlingType, påVent, førsteUttaksMåned, antall.intValue());
    }

    private static LocalDate localDate(Object sqlTimestamp) {
        return switch (sqlTimestamp) {
            case null -> null;
            case java.time.LocalDateTime localDateTime -> localDateTime.toLocalDate();
            case java.time.LocalDate localDate -> localDate;
            default -> throw new IllegalArgumentException("Unsupported SQL timestamp: " + sqlTimestamp.getClass().getSimpleName());
        };
    }

}
