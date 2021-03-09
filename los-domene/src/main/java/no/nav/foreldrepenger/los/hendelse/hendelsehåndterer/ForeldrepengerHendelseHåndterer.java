package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import static no.nav.foreldrepenger.los.felles.util.StreamUtil.safeStream;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.eventresultat.EventResultat;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.FpsakHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.GenerellOpprettOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.GjenåpneOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.IkkeRelevantForOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.LukkOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.OppgaveHendelseHåndtererFactory;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.OpprettBeslutterOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.OpprettPapirsøknadOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.PåVentOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.KøOppgaveHendelse;


import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandlingKlient;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.eventresultat.ForeldrepengerEventMapper;

@ApplicationScoped
public class ForeldrepengerHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerHendelseHåndterer.class);

    private ForeldrepengerBehandlingKlient foreldrePengerBehandlingKlient;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppgaveStatistikk oppgaveStatistikk;
    private OppgaveHendelseHåndtererFactory oppgaveHendelseHåndtererFactory;

    @Inject
    public ForeldrepengerHendelseHåndterer(ForeldrepengerBehandlingKlient foreldrePengerBehandlingKlient,
                                           OppgaveRepository oppgaveRepository,
                                           OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                           OppgaveStatistikk oppgaveStatistikk,
                                           OppgaveHendelseHåndtererFactory oppgaveHendelseHåndtererFactory) {
        this.foreldrePengerBehandlingKlient = foreldrePengerBehandlingKlient;
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.oppgaveStatistikk = oppgaveStatistikk;
        this.oppgaveHendelseHåndtererFactory = oppgaveHendelseHåndtererFactory;
    }

    ForeldrepengerHendelseHåndterer() {
        //CDI
    }

    public void håndter(Hendelse hendelse) {
        var behandlingId = hendelse.getBehandlingId();
        var behandling = foreldrePengerBehandlingKlient.getBehandling(behandlingId);
        behandling.setYtelseType(hendelse.getYtelseType());
        var oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));
        var eventResultat = ForeldrepengerEventMapper.finnEventResultat(behandling, oppgaveHistorikk, hendelse.getBehandlendeEnhet());

        // sammenlikne med ny håndterermekanisme
        // forventer at noen GJENÅPNE_OPPGAVE blir sendt til ny OppdaterOppgaveegenskaperHendelseHåndterer
        var map = new HashMap<EventResultat, Class<? extends FpsakHendelseHåndterer>>();
        map.put(EventResultat.IKKE_RELEVANT, IkkeRelevantForOppgaveHendelseHåndterer.class);
        map.put(EventResultat.LUKK_OPPGAVE, LukkOppgaveHendelseHåndterer.class);
        map.put(EventResultat.LUKK_OPPGAVE_MANUELT_VENT, PåVentOppgaveHendelseHåndterer.class);
        map.put(EventResultat.LUKK_OPPGAVE_VENT, PåVentOppgaveHendelseHåndterer.class);
        map.put(EventResultat.OPPRETT_OPPGAVE, GenerellOpprettOppgaveHendelseHåndterer.class);
        map.put(EventResultat.OPPRETT_BESLUTTER_OPPGAVE, OpprettBeslutterOppgaveHendelseHåndterer.class);
        map.put(EventResultat.OPPRETT_PAPIRSØKNAD_OPPGAVE, OpprettPapirsøknadOppgaveHendelseHåndterer.class);
        map.put(EventResultat.GJENÅPNE_OPPGAVE, GjenåpneOppgaveHendelseHåndterer.class);
        var nyOppgaveHåndtererKandidat = oppgaveHendelseHåndtererFactory.lagHåndterer(hendelse, behandling);
        var forventet = map.get(eventResultat);
        if (!nyOppgaveHåndtererKandidat.getClass().equals(forventet)) {
            LOG.info("Eventresultat {}, kandidat: {}, saksnr {}", eventResultat, nyOppgaveHåndtererKandidat.getClass().getSimpleName(), hendelse.getSaksnummer());
        }

        switch (eventResultat) {
            case IKKE_RELEVANT:
                LOG.info("Ikke relevant for oppgaver");
                break;
            case LUKK_OPPGAVE:
                LOG.info("Lukker oppgave");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, OppgaveEventType.LUKKET, null, hendelse.getBehandlendeEnhet());
                break;
            case LUKK_OPPGAVE_VENT:
                LOG.info("Behandling satt automatisk på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, OppgaveEventType.VENT,
                        finnVentAksjonspunktFrist(behandling.getAksjonspunkter()), hendelse.getBehandlendeEnhet());
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                LOG.info("Behandling satt manuelt på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, OppgaveEventType.MANU_VENT,
                        finnManuellAksjonspunktFrist(behandling.getAksjonspunkter()), hendelse.getBehandlendeEnhet());
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, hendelse.getBehandlendeEnhet());
                behandling.setSaksnummer(hendelse.getSaksnummer());
                behandling.setAktørId(hendelse.getAktørId());
                var håndterer = new GenerellOpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer,
                        oppgaveStatistikk, behandling);
                håndterer.håndter();
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                håndterOpprettetBeslutterOppgave(hendelse, behandling, oppgaveHistorikk);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                håndterOpprettetPapirsøknadOppgave(hendelse, behandling, oppgaveHistorikk);
                break;
            case GJENÅPNE_OPPGAVE:
                håndterGjenåpneOppgave(hendelse, behandling);
                break;
        }
    }

    private void håndterGjenåpneOppgave(Hendelse hendelse, BehandlingFpsak behandling) {
        LOG.info("Gjenåpner oppgave");
        var behandlingId = behandling.getBehandlingId();
        Oppgave gjenåpnetOppgave = gjenåpneOppgave(behandlingId);
        oppdaterOppgaveInformasjon(gjenåpnetOppgave, behandlingId, hendelse, behandling);
        loggEvent(gjenåpnetOppgave.getBehandlingId(), OppgaveEventType.GJENAPNET, null, hendelse.getBehandlendeEnhet());
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandling);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
    }

    private void håndterOpprettetPapirsøknadOppgave(Hendelse hendelse, BehandlingFpsak behandling,
                                                    OppgaveHistorikk oppgaveHistorikk) {
        LOG.info("Oppretter papirsøknadoppgave");
        var behandlingId = behandling.getBehandlingId();
        avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, hendelse.getBehandlendeEnhet());
        Oppgave papirsøknadOppgave = nyOppgave(behandlingId, hendelse, behandling);
        loggEvent(papirsøknadOppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, hendelse.getBehandlendeEnhet());
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandling);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(papirsøknadOppgave, egenskapFinner);
        oppgaveStatistikk.lagre(papirsøknadOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    private void håndterOpprettetBeslutterOppgave(Hendelse hendelse, BehandlingFpsak behandling,
                                                  OppgaveHistorikk oppgaveHistorikk) {
        LOG.info("Oppretter beslutteroppgave");
        var behandlingId = behandling.getBehandlingId();
        var enhet = hendelse.getBehandlendeEnhet();
        avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, enhet);
        Oppgave beslutterOppgave = nyOppgave(behandlingId, hendelse, behandling);
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandling);
        loggEvent(beslutterOppgave, egenskapFinner);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
        oppgaveStatistikk.lagre(beslutterOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    private void loggEvent(Oppgave oppgave, OppgaveEgenskapFinner egenskapFinner) {
        List<AndreKriterierType> kriterier = egenskapFinner.getAndreKriterier();
        if (kriterier.contains(AndreKriterierType.TIL_BESLUTTER)) {
            loggEventType(oppgave, AndreKriterierType.TIL_BESLUTTER);
        } else if (kriterier.contains(AndreKriterierType.PAPIRSØKNAD)) {
            loggEventType(oppgave, AndreKriterierType.PAPIRSØKNAD);
        } else {
            loggEventType(oppgave, null);
        }
    }

    private void loggEventType(Oppgave oppgave, AndreKriterierType kriterie) {
        OppgaveEventLogg eventLogg = new OppgaveEventLogg(oppgave.getBehandlingId(), OppgaveEventType.OPPRETTET,
                kriterie, oppgave.getBehandlendeEnhet());
        oppgaveRepository.lagre(eventLogg);
    }

    private void avsluttFpsakOppgaveOgLoggEvent(BehandlingId behandlingId, OppgaveEventType eventType, LocalDateTime frist, String behandlendeEnhet) {
        oppgaveStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        avsluttOppgaveForBehandling(behandlingId);
        loggEvent(behandlingId, eventType, null, behandlendeEnhet, frist);
    }

    private static LocalDateTime finnVentAksjonspunktFrist(List<Aksjonspunkt> aksjonspunktListe) {
        return safeStream(aksjonspunktListe)
                .filter(Aksjonspunkt::erPåVent)
                .map(Aksjonspunkt::getFristTid)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime finnManuellAksjonspunktFrist(List<Aksjonspunkt> aksjonspunktListe) {
        return safeStream(aksjonspunktListe)
                .filter(Aksjonspunkt::erManueltPåVent)
                .map(Aksjonspunkt::getFristTid)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private void avsluttOppgaveHvisÅpen(BehandlingId behandlingId, OppgaveHistorikk oppgaveHistorikk, String behandlendeEnhet) {
        if (oppgaveHistorikk.erÅpenOppgave()) {
            if (behandlingId != null) {
                loggEvent(behandlingId, OppgaveEventType.LUKKET, null, behandlendeEnhet);
            }
            oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
        }
    }

    private void loggEvent(BehandlingId behandlingId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet));
    }

    protected void loggEvent(BehandlingId behandlingId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime frist) {
        oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet, frist));
    }

    private void oppdaterOppgaveInformasjon(Oppgave oppgave, BehandlingId behandlingId, Hendelse hendelse, BehandlingFpsak behandling) {
        Oppgave tmp = oppgaveFra(behandlingId, hendelse, behandling);
        oppgave.avstemMed(tmp);
        oppgaveRepository.lagre(oppgave);
    }

    private Oppgave nyOppgave(BehandlingId behandlingId, Hendelse hendelse, BehandlingFpsak behandling) {
        Oppgave oppgave = oppgaveFra(behandlingId, hendelse, behandling);
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }

    private Oppgave oppgaveFra(BehandlingId behandlingId, Hendelse hendelse, BehandlingFpsak behandling) {
        return Oppgave.builder()
                .medSystem(hendelse.getFagsystem().name())
                .medFagsakSaksnummer(Long.valueOf(hendelse.getSaksnummer()))
                .medAktorId(Long.valueOf(hendelse.getAktørId()))
                .medBehandlendeEnhet(hendelse.getBehandlendeEnhet())
                .medBehandlingType(hendelse.getBehandlingType())
                .medFagsakYtelseType(hendelse.getYtelseType())
                .medAktiv(true)
                .medBehandlingOpprettet(hendelse.getBehandlingOpprettetTidspunkt())
                .medUtfortFraAdmin(false)
                .medBehandlingStatus(BehandlingStatus.fraKode(behandling.getStatus()))
                .medBehandlingId(behandlingId)
                .medForsteStonadsdag(behandling.getFørsteUttaksdag())
                .medBehandlingsfrist(behandling.getBehandlingstidFrist())
                .build();
    }

    private Oppgave gjenåpneOppgave(BehandlingId behandlingId) {
        var oppgave = oppgaveRepository.gjenåpneOppgaveForBehandling(behandlingId);
        oppgave.map(Oppgave::getReservasjon)
                .map(Reservasjon::getReservertTil)
                .ifPresent(reservertTil -> {
                            var nå = LocalDateTime.now();
                            var duration = Duration.between(reservertTil, nå);
                            if (reservertTil.isAfter(nå)) {
                                LOG.info("Gjenåpnet oppgave har aktiv reservasjon {} " +
                                        "minutter frem i tid", duration.abs().toMinutes());
                            } else {
                                LOG.info("Gjenåpnet oppgave er tilknyttet inaktiv reservasjon " +
                                        "lukket for {} minutter siden", duration.toMinutes());
                            }
                        }
                );
        return oppgave.orElseThrow(() -> new IllegalStateException(String.format("Finner ikke oppgave for gjenåpning, behandlingId %s", behandlingId)));
    }

    private void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
    }
}
