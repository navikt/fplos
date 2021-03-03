package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import no.nav.foreldrepenger.los.oppgave.Oppgavespørring;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;

@ApplicationScoped
public class StatistikkRepositoryImpl implements StatistikkRepository {

    private static final String AVDELING_ENHET = "avdelingEnhet";
    private static final String TIL_BESLUTTER = "tilBeslutter";

    private EntityManager entityManager;

    @Inject
    public StatistikkRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    StatistikkRepositoryImpl(){
        //CDI
    }

    @Override
    public List<Object[]> hentAlleOppgaverForAvdeling(String avdelingEnhet) {
        return entityManager.createNativeQuery(
                "Select o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE, nvl2(oe.ANDRE_KRITERIER_TYPE,'J','N') AS BESLUTTER_JN, Count(o.ID) AS ANTALL " +
                         "FROM OPPGAVE o INNER JOIN avdeling a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET  " +
                         "LEFT JOIN OPPGAVE_EGENSKAP oe ON oe.OPPGAVE_ID = o.ID AND oe.ANDRE_KRITERIER_TYPE = :tilBeslutter " +
                         "WHERE a.AVDELING_ENHET =:avdelingEnhet AND o.AKTIV='J' " +
                         "GROUP BY o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE, oe.ANDRE_KRITERIER_TYPE " +
                         "ORDER BY o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE, oe.ANDRE_KRITERIER_TYPE")
                .setParameter(AVDELING_ENHET, avdelingEnhet)
                .setParameter(TIL_BESLUTTER, AndreKriterierType.TIL_BESLUTTER.getKode()).getResultList();
    }

    @Override
    public List hentAlleOppgaverForAvdelingPerDato(String avdelingEnhet) {
        return entityManager.createNativeQuery(
                "Select o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE, datoer.dato, Count(1) AS ANTALL " +
                        "FROM (select trunc(sysdate) + rownum -28 as dato from all_objects where rownum <= (trunc(sysdate) - trunc(sysdate-28) )) datoer, " +
                        "OPPGAVE o INNER JOIN avdeling a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET " +
                        "WHERE a.AVDELING_ENHET =:avdelingEnhet " +
                        "AND trunc(datoer.dato) >= trunc(o.OPPRETTET_TID) " +
                        "AND NOT (o.AKTIV='N' AND trunc(datoer.dato) > trunc(o.ENDRET_TID)) " +
                        "GROUP BY datoer.dato, o.BEHANDLING_TYPE, o.FAGSAK_YTELSE_TYPE " +
                        "ORDER BY datoer.dato, o.FAGSAK_YTELSE_TYPE, o.BEHANDLING_TYPE ")
                .setParameter(AVDELING_ENHET, avdelingEnhet).getResultList();
    }

    @Override
    public List hentAntallOppgaverForAvdelingSattManueltPåVent(String avdelingEnhet) {
        Query query = entityManager.createNativeQuery(
                "SELECT COALESCE(trunc(oel.FRIST_TID), trunc(oel.OPPRETTET_TID + 28)) ESTIMERT_FRIST, o.FAGSAK_YTELSE_TYPE, Count(distinct oel.BEHANDLING_ID) as ANTALL " + //,
                        "FROM OPPGAVE_EVENT_LOGG oel " +
                        "INNER JOIN OPPGAVE o ON o.BEHANDLING_ID = oel.BEHANDLING_ID AND o.behandlende_enhet = :behandlendeEnhet " +
                        "WHERE oel.EVENT_TYPE = :eventType " +
                        "AND oel.OPPRETTET_TID = (SELECT MAX(oel2.OPPRETTET_TID) " +
                        "                 FROM OPPGAVE_EVENT_LOGG oel2 " +
                        "                 WHERE oel2.BEHANDLING_ID = oel.BEHANDLING_ID) " +
                        "GROUP BY COALESCE(trunc(oel.FRIST_TID), trunc(oel.OPPRETTET_TID + 28)),o.FAGSAK_YTELSE_TYPE " +
                        "ORDER BY COALESCE(trunc(oel.FRIST_TID), trunc(oel.OPPRETTET_TID + 28)),o.FAGSAK_YTELSE_TYPE ")
                .setParameter("behandlendeEnhet", avdelingEnhet)
                .setParameter("eventType", OppgaveEventType.MANU_VENT.name());
        return query.getResultList();
    }

    @Override
    public List hentNyeOgFerdigstilteOppgaver(Long sakslisteId) {
        OppgaveFiltrering oppgaveFiltrering = entityManager.find(OppgaveFiltrering.class, sakslisteId);
        Oppgavespørring oppgavespørring = new Oppgavespørring(oppgaveFiltrering);

        String filtrerBehandlingType = oppgavespørring.getBehandlingTyper().isEmpty() ? "" : " o.BEHANDLING_TYPE in ( :behtyper  ) AND ";
        String filtrerYtelseType = oppgavespørring.getYtelseTyper().isEmpty() ? "" : " o.FAGSAK_YTELSE_TYPE in ( :fagsakytelsetype ) AND ";

        StringBuilder filtrerInkluderAndreKriterier = new StringBuilder();
        for (AndreKriterierType andreKriterierType : oppgavespørring.getInkluderAndreKriterierTyper()) {
            filtrerInkluderAndreKriterier.append("EXISTS ( SELECT  oe.OPPGAVE_ID FROM OPPGAVE_EGENSKAP oe WHERE o.ID = oe.OPPGAVE_ID AND oe.aktiv = 'J' AND oe.ANDRE_KRITERIER_TYPE = '" + andreKriterierType.getKode() + "' ) AND ");
        }

        StringBuilder filtrerEkskluderAndreKriterier = new StringBuilder();
        for (AndreKriterierType andreKriterierType : oppgavespørring.getEkskluderAndreKriterierTyper()) {
            filtrerEkskluderAndreKriterier.append("NOT EXISTS (select 1 from OPPGAVE_EGENSKAP oen WHERE o.ID = oen.OPPGAVE_ID AND oen.aktiv = 'J' AND oen.ANDRE_KRITERIER_TYPE = '").append(andreKriterierType.getKode()).append("' ) AND ");
        }

        if (!oppgavespørring.getBehandlingTyper().isEmpty()) {
            filtrerBehandlingType = filtrerBehandlingType
                    .replace(":behtyper", oppgavespørring.getBehandlingTyper().stream()
                            .map(BehandlingType::getKode)
                            .collect(Collectors.joining("','", "'", "'")));
        }

        if (!oppgavespørring.getYtelseTyper().isEmpty()) {
            filtrerYtelseType = filtrerYtelseType
                    .replace(":fagsakytelsetype",  oppgavespørring.getYtelseTyper().stream()
                            .map(FagsakYtelseType::getKode)
                            .collect(Collectors.joining("','", "'", "'")));
        }

        Query query = entityManager.createNativeQuery("SELECT " +
                "o.BEHANDLING_TYPE, " +
                "0 AS ANTALL_NYE, " +
                "count (1) as ANTALL_FERDIGSTILTE, " +
                "COALESCE (trunc(o.ENDRET_TID), " +
                "trunc(o.OPPRETTET_TID)) AS dato " +
                "FROM OPPGAVE o " +
                "INNER JOIN AVDELING a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET " +
                "INNER JOIN RESERVASJON r ON r.OPPGAVE_ID = o.ID " +
                "WHERE " + filtrerBehandlingType + filtrerYtelseType + filtrerInkluderAndreKriterier + filtrerEkskluderAndreKriterier +
                "    o.BEHANDLENDE_ENHET = :avdelingEnhet " +
                "    AND ((o.ENDRET_TID IS NOT NULL AND trunc(o.ENDRET_TID) >= trunc(sysdate-7)) " +
                "    OR (o.ENDRET_TID IS NULL AND trunc(o.OPPRETTET_TID) >= trunc(sysdate-7))) AND o.AKTIV = 'N' " +
                "GROUP BY o.BEHANDLING_TYPE, COALESCE (trunc(o.ENDRET_TID), trunc(o.OPPRETTET_TID)) " +
                "UNION " +
                "SELECT o.BEHANDLING_TYPE, count (*) AS ANTALL_NYE, 0 AS ANTALL_FERDIGSTILTE, trunc(o.OPPRETTET_TID) AS dato " +
                "FROM OPPGAVE o " +
                "INNER JOIN AVDELING a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET " +
                "WHERE " + filtrerBehandlingType + filtrerYtelseType + filtrerInkluderAndreKriterier + filtrerEkskluderAndreKriterier +
                "    o.BEHANDLENDE_ENHET = :avdelingEnhet " +
                "AND trunc(o.OPPRETTET_TID) >= trunc(sysdate-7) " +
                "GROUP BY o.BEHANDLING_TYPE, trunc(o.OPPRETTET_TID)")
                .setParameter(AVDELING_ENHET, oppgaveFiltrering.getAvdeling().getAvdelingEnhet());

        return query.getResultList();
    }

    @Override
    public List hentOppgaverPerFørsteStønadsdag(String avdeling) {
        return entityManager.createNativeQuery(
                "Select trunc(o.FORSTE_STONADSDAG) as DATO, Count(o.FORSTE_STONADSDAG) AS ANTALL " +
                        "FROM OPPGAVE o INNER JOIN avdeling a ON a.AVDELING_ENHET = o.BEHANDLENDE_ENHET " +
                        "WHERE a.AVDELING_ENHET =:avdelingEnhet AND NOT o.AKTIV='N' AND o.FORSTE_STONADSDAG IS NOT NULL " +
                        "GROUP BY trunc(o.FORSTE_STONADSDAG) " +
                        "ORDER BY trunc(o.FORSTE_STONADSDAG)")
                .setParameter(AVDELING_ENHET, avdeling).getResultList();
    }
}
