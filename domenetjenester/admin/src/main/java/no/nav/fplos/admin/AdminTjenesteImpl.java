package no.nav.fplos.admin;

import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.repository.AdminRepository;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.Fagsystem;
import no.nav.fplos.kafkatjenester.FpsakEventHandler;
import no.nav.fplos.kafkatjenester.KafkaReader;
import no.nav.fplos.kafkatjenester.TilbakekrevingEventHandler;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AdminTjenesteImpl implements AdminTjeneste {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminTjenesteImpl.class);
    private static final String AVSLUTTET_STATUS = "AVSLU";

    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient;
    private AdminRepository adminRepository;
    private FpsakEventHandler fpsakEventHandler;
    private TilbakekrevingEventHandler tilbakekrevingEventHandler;
    private KafkaReader kafaReader;

    public AdminTjenesteImpl(){
        //For automatiks laging
    }

    @Inject
    public AdminTjenesteImpl(AdminRepository adminRepository,
                             ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient,
                             FpsakEventHandler fpsakEventHandler,
                             TilbakekrevingEventHandler tilbakekrevingEventHandler,
                             KafkaReader kafaReader) {
        this.adminRepository = adminRepository;
        this.foreldrepengerBehandlingRestKlient = foreldrepengerBehandlingRestKlient;
        this.fpsakEventHandler = fpsakEventHandler;
        this.tilbakekrevingEventHandler = tilbakekrevingEventHandler;
        this.kafaReader = kafaReader;
    }

    @Override
    public Oppgave synkroniserOppgave(Long behandlingId) {
        BehandlingFpsak behandlingDto = foreldrepengerBehandlingRestKlient.getBehandling(behandlingId);
        if (AVSLUTTET_STATUS.equals(behandlingDto.getStatus())) {
            adminRepository.deaktiverSisteOppgave(behandlingId);
        }
        return adminRepository.hentSisteOppgave(behandlingId);
    }

    @Override
    public Oppgave hentOppgave(Long behandlingId) {
        return adminRepository.hentSisteOppgave(behandlingId);
    }

    @Override
    public List<OppgaveEventLogg> hentEventer(Long behandlingId) {
        return adminRepository.hentEventer(behandlingId);
    }

    @Override
    public void oppdaterOppgave(Long behandlingId) {
        LOGGER.info("Starter oppdatering av oppgave tilhørende behandling {}", behandlingId);
        fpsakEventHandler.prosesser(mapTilBehandlingProsessEventDto(behandlingId));
        LOGGER.info("Oppdatering av oppgave tilhørende behandling {} er fullført", behandlingId);
    }

    @Override
    public int oppdaterAktiveOppgaver() {
        List<Oppgave> aktiveOppgaver = adminRepository.hentAlleAktiveOppgaver();
        aktiveOppgaver.stream().forEach(oppgave -> {
            switch(Fagsystem.valueOf(oppgave.getSystem())){
                case FPSAK :
                    fpsakEventHandler.prosesserFraAdmin(mapTilBehandlingProsessEventDto(oppgave.getBehandlingId()), oppgave.getReservasjon());
                    break;
                case FPTILBAKE :
                    tilbakekrevingEventHandler.prosesserFraAdmin(mapTilBehandlingProsessEventDto(oppgave.getBehandlingId()), oppgave.getReservasjon());
                    break;
            }
        });
        return aktiveOppgaver.size();
    }

    @Override
    public int prosesserAlleMeldingerFraFeillogg() {
        List<EventmottakFeillogg> feillogg = adminRepository.hentAlleMeldingerFraFeillogg();
        for (EventmottakFeillogg innslag : feillogg) {
            kafaReader.prosesser(innslag.getMelding());
            adminRepository.markerFerdig(innslag.getId());
        }
        return feillogg.size();
    }

    @Override
    public List<Oppgave> hentAlleOppgaverForBehandling(Long behandlingId) {
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

    private BehandlingProsessEventDto mapTilBehandlingProsessEventDto(Long behandlingId) {
        Oppgave eksisterendeOppgave = hentOppgave(behandlingId);
        BehandlingFpsak fraFpsak = foreldrepengerBehandlingRestKlient.getBehandling(behandlingId);

        Map<String, String> aksjonspunktKoderMedStatusListe = new HashMap<>();
        fraFpsak.getAksjonspunkter()
                .forEach(aksjonspunkt -> aksjonspunktKoderMedStatusListe.put(aksjonspunkt.getDefinisjonKode(), aksjonspunkt.getStatusKode()));

        return BehandlingProsessEventDto.builder()
                .medFagsystem(eksisterendeOppgave.getSystem())
                .medBehandlingId(behandlingId)
                .medSaksnummer(eksisterendeOppgave.getFagsakSaksnummer().toString())
                .medAktørId(eksisterendeOppgave.getAktorId().toString())
                .medBehandlinStatus(fraFpsak.getStatus())
                .medBehandlendeEnhet(fraFpsak.getBehandlendeEnhet())
                .medYtelseTypeKode(eksisterendeOppgave.getFagsakYtelseType().getKode())
                .medBehandlingTypeKode(eksisterendeOppgave.getBehandlingType().getKode())
                .medOpprettetBehandling(eksisterendeOppgave.getBehandlingOpprettet())
                .medAksjonspunktKoderMedStatusListe(aksjonspunktKoderMedStatusListe)
                .build();
    }
}
