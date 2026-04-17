package no.nav.foreldrepenger.los.migrering.fss;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.jpa.HibernateHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import no.nav.foreldrepenger.los.migrering.dto.BulkDataWrapper;
import no.nav.foreldrepenger.los.migrering.dto.OppgaveDataDto;
import no.nav.foreldrepenger.los.migrering.dto.OppgaveFiltreringDataDto;
import no.nav.foreldrepenger.los.migrering.dto.OrgDataDto;
import no.nav.foreldrepenger.los.oppgave.Behandling;
import no.nav.foreldrepenger.los.oppgave.BehandlingEgenskap;
import no.nav.foreldrepenger.los.oppgave.BehandlingTilstand;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringSaksbehandlerRelasjon;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.AvdelingSaksbehandlerRelasjon;
import no.nav.foreldrepenger.los.organisasjon.GruppeTilknytningRelasjon;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.organisasjon.SaksbehandlerGruppe;
import no.nav.foreldrepenger.los.statistikk.StatistikkEnhetYtelseBehandling;
import no.nav.foreldrepenger.los.statistikk.kø.StatistikkOppgaveFilter;

/**
 * Eksport fra FSS-los. Beholder PK.
 */
@ApplicationScoped
@Transactional
public class FssEksportRepository {

    private static final Logger LOG = LoggerFactory.getLogger(FssEksportRepository.class);

    private EntityManager entityManager;
    private OrganisasjonRepository organisasjonRepository;

    @Inject
    public FssEksportRepository(EntityManager entityManager, OrganisasjonRepository organisasjonRepository) {
        this.entityManager = entityManager;
        this.organisasjonRepository = organisasjonRepository;
    }

    FssEksportRepository() {
        // For CDI
    }

    public BulkDataWrapper hentOrganisasjonOgKøer() {
        var orgData = hentOrganisasjonData();
        var oppgaveKøer = hentOppgaveKøData();
        var dto = BulkDataWrapper.organisasjonOgKøOppset(orgData, oppgaveKøer);
        logg(dto);
        return dto;
    }

    public BulkDataWrapper hentAktiveOppgaverOgReservasjoner(int startPosisjon, int batchSize) {
        var oppgaver = entityManager.createQuery("""
                FROM Oppgave
                WHERE aktiv = true
                ORDER BY id ASC
            """, Oppgave.class)
            .setHint(HibernateHints.HINT_READ_ONLY, true)
            .setFirstResult(startPosisjon)
            .setMaxResults(batchSize)
            .getResultList()
            .stream()
            .map(FssExportMapper::mapToOppgaveDataDto)
            .toList();

        var dto = BulkDataWrapper.aktiveOppgaver(oppgaver);
        logg(dto);
        return dto;
    }

    public BulkDataWrapper hentInaktiveOppgaverOgReservasjoner(int startPosisjon, int batchSize) {
        var oppgaver = entityManager.createQuery("""
                FROM Oppgave o
                JOIN o.reservasjon r
                WHERE o.aktiv = false
                AND coalesce(r.endretTidspunkt, r.opprettetTidspunkt) > :fra
                AND EXISTS (SELECT 1 FROM Behandling b WHERE b.id = o.behandlingId.value AND (b.behandlingTilstand != :avsluttet OR b.avsluttet > :avsluttetTid))
                ORDER BY r.id ASC
            """, Oppgave.class)
            .setHint(HibernateHints.HINT_READ_ONLY, true)
            .setParameter("fra", LocalDate.now().minusDays(21).atStartOfDay())
            .setParameter("avsluttet", BehandlingTilstand.AVSLUTTET)
            .setParameter("avsluttetTid", LocalDate.now().minusDays(1).atStartOfDay())
            .setFirstResult(startPosisjon)
            .setMaxResults(batchSize)
            .getResultList()
            .stream()
            .map(FssExportMapper::mapToOppgaveDataDto)
            .toList();
        var dto = BulkDataWrapper.inaktiveOppgaver(oppgaver);
        logg(dto);
        return dto;
    }

    public BulkDataWrapper hentBehandlinger(int startPosisjon, int batchSize) {
        var behandlinger = entityManager.createQuery("""
                FROM Behandling
                WHERE (behandlingTilstand != :avsluttet OR avsluttet > :avsluttetTid)
                ORDER BY id ASC
            """, Behandling.class)
            .setHint(HibernateHints.HINT_READ_ONLY, true)
            .setParameter("avsluttet", BehandlingTilstand.AVSLUTTET)
            .setParameter("avsluttetTid", LocalDate.now().minusDays(1).atStartOfDay())
            .setFirstResult(startPosisjon)
            .setMaxResults(batchSize)
            .getResultList();

        if (behandlinger.isEmpty()) {
            return BulkDataWrapper.behandlinger(List.of());
        }

        var behandlingIds = behandlinger.stream().map(Behandling::getId).toList();
        var egenskaperMap = entityManager.createQuery(
                "FROM BehandlingEgenskap WHERE behandlingId IN :ids", BehandlingEgenskap.class)
            .setHint(HibernateHints.HINT_READ_ONLY, true)
            .setParameter("ids", behandlingIds)
            .getResultList()
            .stream()
            .collect(groupingBy(BehandlingEgenskap::getBehandlingId,
                mapping(BehandlingEgenskap::getAndreKriterierType, toSet())));

        var dtos = behandlinger.stream()
            .map(b -> FssExportMapper.mapToBehandlingDataDto(b, egenskaperMap.getOrDefault(b.getId(), Set.of())))
            .toList();

        var dto = BulkDataWrapper.behandlinger(dtos);
        logg(dto);
        return dto;
    }

    private void logg(BulkDataWrapper dto) {
        var antallOppgaver = dto.aktiveOppgaver().size() + dto.inaktiveOppgaver().size();
        var antallReservasjoner = dto.aktiveOppgaver().stream().map(OppgaveDataDto::reservasjonDataDto).filter(Objects::nonNull).count()
            + dto.inaktiveOppgaver().stream().map(OppgaveDataDto::reservasjonDataDto).filter(Objects::nonNull).count();
        LOG.info("MIGRERING: Ekstrahert {} behandlinger, {} oppgaver, {} reservasjoner",
            dto.behandlinger().size(), antallOppgaver, antallReservasjoner);
    }

    private OrgDataDto hentOrganisasjonData() {
        var avdelinger = organisasjonRepository.hentAktiveAvdelinger().stream().map(FssExportMapper::mapToAvdelingDataDto).toList();

        var saksbehandlere = entityManager.createQuery("FROM saksbehandler", Saksbehandler.class)
                .setHint(HibernateHints.HINT_READ_ONLY, true)
                .getResultList()
                .stream()
                .map(FssExportMapper::mapToSaksbehandlerDataDto)
                .toList();

        var avdelingSaksbehandlere = entityManager.createQuery("FROM AvdelingSaksbehandlerRelasjon", AvdelingSaksbehandlerRelasjon.class)
                .setHint(HibernateHints.HINT_READ_ONLY, true)
                .getResultList()
                .stream()
                .map(FssExportMapper::mapToAvdelingSaksbehandlerDataDto)
                .toList();

        var saksbehandlerGrupper = entityManager.createQuery("FROM saksbehandlerGruppe", SaksbehandlerGruppe.class)
                .setHint(HibernateHints.HINT_READ_ONLY, true)
                .getResultList()
                .stream()
                .map(FssExportMapper::mapToSaksbehandlerGruppeDataDto)
                .toList();

        var gruppeTilknytninger = entityManager.createQuery("FROM GruppeTilknytningRelasjon", GruppeTilknytningRelasjon.class)
                .setHint(HibernateHints.HINT_READ_ONLY, true)
                .getResultList()
                .stream()
                .map(FssExportMapper::mapToGruppeTilknytningDataDto)
                .toList();

        return new OrgDataDto(avdelinger, saksbehandlere, avdelingSaksbehandlere, saksbehandlerGrupper, gruppeTilknytninger);
    }

    public BulkDataWrapper hentStatistikkEnhetYtelseBehandling(int startPosisjon, int batchSize) {
        var enhetYtelseBehandling = entityManager.createQuery("FROM StatistikkEnhetYtelseBehandling ORDER BY tidsstempel ASC", StatistikkEnhetYtelseBehandling.class)
                .setHint(HibernateHints.HINT_READ_ONLY, true)
                .setFirstResult(startPosisjon)
                .setMaxResults(batchSize)
                .getResultList()
                .stream()
                .map(FssExportMapper::mapToStatEnhetYtelseBehandlingDataDto)
                .toList();

        return BulkDataWrapper.statistikkEnhetYtelseBehandling(enhetYtelseBehandling);
    }

    public BulkDataWrapper hentStatistikkOppgaveFilter(int startPosisjon, int batchSize) {
        var oppgaveFilter = entityManager.createQuery("FROM StatistikkOppgaveFilter WHERE statistikkDato >= :fra ORDER BY tidsstempel ASC", StatistikkOppgaveFilter.class)
            .setHint(HibernateHints.HINT_READ_ONLY, true)
            .setParameter("fra", LocalDate.now().minusWeeks(4))
            .setFirstResult(startPosisjon)
            .setMaxResults(batchSize)
            .getResultList()
            .stream()
            .map(FssExportMapper::mapToStatOppgaveFilterDataDto)
            .toList();
        return BulkDataWrapper.statistikkOppgaveFilter(oppgaveFilter);
    }

    private List<OppgaveFiltreringDataDto> hentOppgaveKøData() {
        var oppgaveFiltreringSaksbehandleridenter = entityManager.createQuery("FROM FiltreringSaksbehandlerRelasjon",
                FiltreringSaksbehandlerRelasjon.class)
            .setHint(HibernateHints.HINT_READ_ONLY, true)
            .getResultList()
            .stream()
            .collect(Collectors.groupingBy(fsr -> fsr.getOppgaveFiltrering().getId(),
                mapping(fsr -> fsr.getSaksbehandler().getSaksbehandlerIdent(), toSet())));

        return entityManager.createQuery("FROM OppgaveFiltrering", OppgaveFiltrering.class)
            .setHint(HibernateHints.HINT_READ_ONLY, true)
            .getResultList()
            .stream()
            .map(of -> FssExportMapper.mapToOppgaveFiltreringDataDto(of, oppgaveFiltreringSaksbehandleridenter))
            .toList();
    }

}
