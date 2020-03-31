package no.nav.fplos.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.repository.AdminRepository;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.ForeldrepengerEventHåndterer;
import no.nav.fplos.kafkatjenester.FpsakBehandlingProsessEventDto;
import no.nav.fplos.kafkatjenester.KafkaConsumer;
import no.nav.fplos.kafkatjenester.TilbakekrevingEventHåndterer;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;

@ApplicationScoped
public class AdminTjenesteImpl implements AdminTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(AdminTjenesteImpl.class);
    private static final String AVSLUTTET_STATUS = "AVSLU";

    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient;
    private AdminRepository adminRepository;
    private ForeldrepengerEventHåndterer foreldrepengerEventHåndterer;
    private TilbakekrevingEventHåndterer tilbakekrevingEventHåndterer;

    @Inject
    public AdminTjenesteImpl(AdminRepository adminRepository,
                             ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient,
                             ForeldrepengerEventHåndterer foreldrepengerEventHåndterer,
                             TilbakekrevingEventHåndterer tilbakekrevingEventHåndterer) {
        this.adminRepository = adminRepository;
        this.foreldrepengerBehandlingRestKlient = foreldrepengerBehandlingRestKlient;
        this.foreldrepengerEventHåndterer = foreldrepengerEventHåndterer;
        this.tilbakekrevingEventHåndterer = tilbakekrevingEventHåndterer;
    }

    AdminTjenesteImpl(){
        //For automatisk laging
    }

    @Override
    public Oppgave synkroniserOppgave(BehandlingId behandlingId) {
        BehandlingFpsak behandlingDto = foreldrepengerBehandlingRestKlient.getBehandling(behandlingId);
        if (AVSLUTTET_STATUS.equals(behandlingDto.getStatus())) {
            adminRepository.deaktiverSisteOppgave(behandlingId);
        }
        return adminRepository.hentSisteOppgave(behandlingId);
    }

    @Override
    public Oppgave hentOppgave(BehandlingId behandlingId) {
        return adminRepository.hentSisteOppgave(behandlingId);
    }

    @Override
    public List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId) {
        return adminRepository.hentEventer(behandlingId);
    }

    @Override
    public void oppdaterOppgave(BehandlingId behandlingId) {
        LOG.info("Starter oppdatering av oppgave tilhørende behandlingId {}", behandlingId);
        foreldrepengerEventHåndterer.håndterEvent(mapTilBehandlingProsessEventDto(behandlingId));
        LOG.info("Oppdatering av oppgave tilhørende behandlingId {} er fullført", behandlingId);
    }

    @Override
    public int prosesserAlleMeldingerFraFeillogg() {
        List<EventmottakFeillogg> feillogg = adminRepository.hentAlleMeldingerFraFeillogg();
        for (EventmottakFeillogg innslag : feillogg) {
            prosesserFeil(innslag);
        }
        return feillogg.size();
    }

    private void prosesserFeil(EventmottakFeillogg innslag) {
        try {
            var dto = KafkaConsumer.deserialiser(innslag.getMelding());
            if (dto instanceof FpsakBehandlingProsessEventDto) {
                foreldrepengerEventHåndterer.håndterEvent((FpsakBehandlingProsessEventDto) dto);
            } else if (dto instanceof TilbakebetalingBehandlingProsessEventDto) {
                tilbakekrevingEventHåndterer.håndterEvent((TilbakebetalingBehandlingProsessEventDto) dto);
            } else {
                LOG.error("Feil ved prosessering av feillogg. InnslagId={}. Ukjent klasse for dto {}", innslag.getId(), dto.getClass());
                return;
            }
            adminRepository.markerFerdig(innslag.getId());
        } catch (Exception e) {
            LOG.error("Feil ved prosessering av feillogg. InnslagId={}", innslag.getId(), e);
        }
    }

    @Override
    public Optional<EventmottakFeillogg> ferdigmarkerOgHentOppgaveEvent(Long eventId) {
        try {
            adminRepository.markerFerdig(eventId);
        } catch (NullPointerException e) {
            return Optional.empty(); // ingen event funnet
        }
        return Optional.of(adminRepository.hentEvent(eventId));
    }

    @Override
    public List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId) {
        return adminRepository.hentAlleOppgaverForBehandling(behandlingId);
    }

    @Override
    public Oppgave deaktiverOppgave(Long oppgaveId) {
        return adminRepository.deaktiverOppgave(oppgaveId);
    }

    @Override
    public Oppgave aktiverOppgave(Long oppgaveId) {
        return adminRepository.aktiverOppgave(oppgaveId);
    }

    private FpsakBehandlingProsessEventDto mapTilBehandlingProsessEventDto(BehandlingId behandlingId) {
        Oppgave eksisterendeOppgave = hentOppgave(behandlingId);
        BehandlingFpsak fraFpsak = foreldrepengerBehandlingRestKlient.getBehandling(behandlingId);

        Map<String, String> aksjonspunktKoderMedStatusListe = new HashMap<>();
        fraFpsak.getAksjonspunkter()
                .forEach(aksjonspunkt -> aksjonspunktKoderMedStatusListe.put(aksjonspunkt.getDefinisjonKode(), aksjonspunkt.getStatusKode()));

        return FpsakBehandlingProsessEventDto.builder()
                .medEksternId(behandlingId.toUUID())
                .medSaksnummer(eksisterendeOppgave.getFagsakSaksnummer().toString())
                .medAktørId(eksisterendeOppgave.getAktorId().toString())
                .medBehandlingStatus(fraFpsak.getStatus())
                .medYtelseTypeKode(eksisterendeOppgave.getFagsakYtelseType().getKode())
                .medBehandlingTypeKode(eksisterendeOppgave.getBehandlingType().getKode())
                .medOpprettetBehandling(eksisterendeOppgave.getBehandlingOpprettet())
                .medAksjonspunktKoderMedStatusListe(aksjonspunktKoderMedStatusListe)
                .build();
    }
}
