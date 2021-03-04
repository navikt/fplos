package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import static no.nav.foreldrepenger.los.felles.util.StreamUtil.safeStream;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.oppgaveegenskap.AktuelleOppgaveEgenskaperData;
import no.nav.foreldrepenger.los.oppgave.oppgaveegenskap.AktuelleOppgaveEgenskaperTjeneste;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.OpprettOppgaveHendelseHåndterer;


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
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.eventresultat.EventResultat;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.eventresultat.ForeldrepengerEventMapper;

@ApplicationScoped
public class ForeldrepengerHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerHendelseHåndterer.class);

    private ForeldrepengerBehandlingKlient foreldrePengerBehandlingKlient;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppgaveStatistikk oppgaveStatistikk;
    private AktuelleOppgaveEgenskaperTjeneste aktuelleOppgaveEgenskapTjeneste;

    @Inject
    public ForeldrepengerHendelseHåndterer(ForeldrepengerBehandlingKlient foreldrePengerBehandlingKlient,
                                           OppgaveRepository oppgaveRepository,
                                           OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                           OppgaveStatistikk oppgaveStatistikk,
                                           AktuelleOppgaveEgenskaperTjeneste aktuelleOppgaveEgenskapTjeneste) {
        this.foreldrePengerBehandlingKlient = foreldrePengerBehandlingKlient;
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.oppgaveStatistikk = oppgaveStatistikk;
        this.aktuelleOppgaveEgenskapTjeneste = aktuelleOppgaveEgenskapTjeneste;
    }

    ForeldrepengerHendelseHåndterer() {
        //CDI
    }

    public void håndter(Hendelse hendelse) {
        var behandlingId = hendelse.getBehandlingId();
        var behandlingFpsak = foreldrePengerBehandlingKlient.getBehandling(behandlingId);
        behandlingFpsak.setYtelseType(hendelse.getYtelseType());
        var oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));
        var eventResultat = ForeldrepengerEventMapper.finnEventResultat(behandlingFpsak, oppgaveHistorikk, hendelse.getBehandlendeEnhet());

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
                        finnVentAksjonspunktFrist(behandlingFpsak.getAksjonspunkter()), hendelse.getBehandlendeEnhet());
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                LOG.info("Behandling satt manuelt på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, OppgaveEventType.MANU_VENT,
                        finnManuellAksjonspunktFrist(behandlingFpsak.getAksjonspunkter()), hendelse.getBehandlendeEnhet());
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, hendelse.getBehandlendeEnhet());
                behandlingFpsak.setSaksnummer(hendelse.getSaksnummer());
                behandlingFpsak.setAktørId(hendelse.getAktørId());
                var håndterer = new OpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer,
                        oppgaveStatistikk, aktuelleOppgaveEgenskapTjeneste, behandlingFpsak);
                håndterer.håndter();
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                håndterOpprettetBeslutterOppgave(hendelse, behandlingFpsak, oppgaveHistorikk);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                håndterOpprettetPapirsøknadOppgave(hendelse, behandlingFpsak, oppgaveHistorikk);
                break;
            case GJENÅPNE_OPPGAVE:
                håndterGjenåpneOppgave(hendelse, behandlingFpsak);
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
        var nyEgenskapData = aktuelleOppgaveEgenskapTjeneste.egenskaperForFpsak(behandling);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(gjenåpnetOppgave, nyEgenskapData);
    }

    private void håndterOpprettetPapirsøknadOppgave(Hendelse hendelse, BehandlingFpsak behandling,
                                                    OppgaveHistorikk oppgaveHistorikk) {
        LOG.info("Oppretter papirsøknadoppgave");
        var behandlingId = behandling.getBehandlingId();
        avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, hendelse.getBehandlendeEnhet());
        Oppgave papirsøknadOppgave = nyOppgave(behandlingId, hendelse, behandling);
        loggEvent(papirsøknadOppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, hendelse.getBehandlendeEnhet());
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandling);
        var nyEgenskapData = aktuelleOppgaveEgenskapTjeneste.egenskaperForFpsak(behandling);

        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(papirsøknadOppgave, nyEgenskapData);
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
        var aktuelleOppgaveEgenskaperData = aktuelleOppgaveEgenskapTjeneste.egenskaperForFpsak(behandling);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(beslutterOppgave, aktuelleOppgaveEgenskaperData);
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
        Optional.ofNullable(oppgave.getReservasjon())
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
        return oppgave;
    }

    private void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
    }
}
