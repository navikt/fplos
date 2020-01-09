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
import java.util.UUID;

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
        //For automatisk laging
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

    public Oppgave synkroniserOppgave(UUID uuid) {
        BehandlingFpsak behandlingDto = foreldrepengerBehandlingRestKlient.getBehandling(uuid);
        if (AVSLUTTET_STATUS.equals(behandlingDto.getStatus())) {
            adminRepository.deaktiverSisteOppgave(uuid);
        }
        return adminRepository.hentSisteOppgave(uuid);
    }

    public Oppgave hentOppgave(UUID uuid) {
        return adminRepository.hentSisteOppgave(uuid);
    }

    public List<OppgaveEventLogg> hentEventer(UUID uuid) {
        return adminRepository.hentEventer(uuid);
    }

    public void oppdaterOppgave(UUID uuid) {
        LOGGER.info("Starter oppdatering av oppgave tilhørende uuid {}", uuid);
        fpsakEventHandler.prosesser(mapTilBehandlingProsessEventDto(uuid));
        LOGGER.info("Oppdatering av oppgave tilhørende uuid {} er fullført", uuid);
    }

    @Override
    public int oppdaterAktiveOppgaver() {
        List<Oppgave> aktiveOppgaver = adminRepository.hentAlleAktiveOppgaver();
        aktiveOppgaver.stream().forEach(oppgave -> {
            switch(Fagsystem.valueOf(oppgave.getSystem())){
                case FPSAK :
                    fpsakEventHandler.prosesserFraAdmin(mapTilBehandlingProsessEventDto(oppgave.getEksternId()), oppgave.getReservasjon());
                    break;
                case FPTILBAKE :
                    tilbakekrevingEventHandler.prosesserFraAdmin(mapTilBehandlingProsessEventDto(oppgave.getEksternId()), oppgave.getReservasjon());
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
    public int oppdaterAktiveOppgaverMedInformasjonOmRefusjonskrav() {
        List<Oppgave> aktiveOppgaver = adminRepository.hentAlleAktiveOppgaver();
        aktiveOppgaver.forEach(this::leggTilOppgaveEgenskapHvisUtbetalingTilBruker);
        return aktiveOppgaver.size();
    }

    @Override
    public int oppdaterAktiveOppgaverMedInformasjonHvisUtlandssak() {
        List<Oppgave> aktiveOppgaver = adminRepository.hentAlleAktiveOppgaver();
        aktiveOppgaver.forEach(this::leggTilOppgaveEgenskapHvisUtlandssak);
        return aktiveOppgaver.size();
    }

    @Override
    public int oppdaterAktiveOppgaverMedInformasjonHvisGradering() {
        List<Oppgave> aktiveOppgaver = adminRepository.hentAlleAktiveOppgaver();
        aktiveOppgaver.forEach(this::leggTilOppgaveEgenskapHvisGradering);
        return aktiveOppgaver.size();
    }

    public List<Oppgave> hentAlleOppgaverForBehandling(UUID uuid) {
        return adminRepository.hentAlleOppgaverForBehandling(uuid);
    }

    @Override
    public Oppgave deaktiverOppgave(Long oppgaveId) {
        return adminRepository.deaktiverOppgave(oppgaveId);
    }

    @Override
    public Oppgave aktiverOppgave(Long oppgaveId) {
        return adminRepository.aktiverOppgave(oppgaveId);
    }

    private void leggTilOppgaveEgenskapHvisUtbetalingTilBruker(Oppgave oppgave) {
        //BehandlingFpsak behandlingFpsak = foreldrepengerBehandlingRestKlient.getBehandling(oppgave.getBehandlingId());
        BehandlingFpsak behandlingFpsak = foreldrepengerBehandlingRestKlient.getBehandling(oppgave.getEksternId());
        fpsakEventHandler.håndterOppgaveEgenskapUtbetalingTilBruker(behandlingFpsak.getHarRefusjonskravFraArbeidsgiver(), oppgave);
    }

    private void leggTilOppgaveEgenskapHvisUtlandssak(Oppgave oppgave) {
        //BehandlingFpsak behandlingFpsak = foreldrepengerBehandlingRestKlient.getBehandling(oppgave.getBehandlingId());
        BehandlingFpsak behandlingFpsak = foreldrepengerBehandlingRestKlient.getBehandling(oppgave.getEksternId());
        fpsakEventHandler.håndterOppgaveEgenskapUtlandssak(behandlingFpsak.getErUtlandssak(), oppgave);
    }

    private void leggTilOppgaveEgenskapHvisGradering(Oppgave oppgave) {
        //BehandlingFpsak behandlingFpsak = foreldrepengerBehandlingRestKlient.getBehandling(oppgave.getBehandlingId());
        BehandlingFpsak behandlingFpsak = foreldrepengerBehandlingRestKlient.getBehandling(oppgave.getEksternId());
        fpsakEventHandler.håndterOppgaveEgenskapGradering(behandlingFpsak.getHarGradering(), oppgave);
    }

    /*TODO: BehandlingProsessEventDto må få uuid så snart felt er tilgjengelig */
    private BehandlingProsessEventDto mapTilBehandlingProsessEventDto(UUID uuid) {
        Oppgave eksisterendeOppgave = hentOppgave(uuid);
        BehandlingFpsak fraFpsak = foreldrepengerBehandlingRestKlient.getBehandling(uuid);

        Map<String, String> aksjonspunktKoderMedStatusListe = new HashMap<>();
        fraFpsak.getAksjonspunkter()
                .forEach(aksjonspunkt -> aksjonspunktKoderMedStatusListe.put(aksjonspunkt.getDefinisjonKode(), aksjonspunkt.getStatusKode()));

        return BehandlingProsessEventDto.builder()
                .medFagsystem(eksisterendeOppgave.getSystem())
                .medBehandlingId(eksisterendeOppgave.getBehandlingId())
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
