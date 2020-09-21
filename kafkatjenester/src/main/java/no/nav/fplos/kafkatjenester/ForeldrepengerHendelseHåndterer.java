package no.nav.fplos.kafkatjenester;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.hendelse.Hendelse;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.fplos.kafkatjenester.eventresultat.ForeldrepengerEventMapper;

@ApplicationScoped
public class ForeldrepengerHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerHendelseHåndterer.class);

    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHandler oppgaveEgenskapHandler;

    @Inject
    public ForeldrepengerHendelseHåndterer(ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient,
                                           OppgaveRepository oppgaveRepository,
                                           OppgaveEgenskapHandler oppgaveEgenskapHandler) {
        this.foreldrePengerBehandlingRestKlient = foreldrePengerBehandlingRestKlient;
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHandler = oppgaveEgenskapHandler;
    }

    ForeldrepengerHendelseHåndterer() {
        //CDI
    }

    public void håndter(Hendelse hendelse) {
        var behandlingId = hendelse.getBehandlingId();
        BehandlingFpsak behandling = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);
        behandling.setYtelseType(hendelse.getYtelseType());
        OppgaveHistorikk oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));
        OppgaveEgenskapFinner egenskapFinner = new FpsakOppgaveEgenskapFinner(behandling);
        var enhet = hendelse.getBehandlendeEnhet();
        EventResultat eventResultat = ForeldrepengerEventMapper.signifikantEventFra(behandling.getAksjonspunkter(), oppgaveHistorikk, enhet);

        switch (eventResultat) {
            case FERDIG:
                LOG.info("Ikke relevant for oppgaver");
                break;
            case LUKK_OPPGAVE:
                LOG.info("Lukker oppgave");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, OppgaveEventType.LUKKET, null, enhet);
                break;
            case LUKK_OPPGAVE_VENT:
                LOG.info("Behandling satt automatisk på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, OppgaveEventType.VENT, finnVentAksjonspunktFrist(behandling.getAksjonspunkter()), enhet);
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                LOG.info("Behandling satt manuelt på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, OppgaveEventType.MANU_VENT, finnManuellAksjonspunktFrist(behandling.getAksjonspunkter()), enhet);
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, enhet);
                Oppgave oppgave = nyOppgave(behandlingId, hendelse, behandling);
                LOG.info("Oppretter oppgave");
                loggEvent(oppgave, egenskapFinner);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, enhet);
                Oppgave beslutterOppgave = nyOppgave(behandlingId, hendelse, behandling);
                loggEvent(beslutterOppgave, egenskapFinner);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, enhet);
                Oppgave papirsøknadOppgave = nyOppgave(behandlingId, hendelse, behandling);
                loggEvent(papirsøknadOppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, enhet);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(papirsøknadOppgave, egenskapFinner);
                break;
            case GJENÅPNE_OPPGAVE:
                LOG.info("Gjenåpner oppgave");
                Oppgave gjenåpnetOppgave = gjenåpneOppgave(behandlingId);
                oppdaterOppgaveInformasjon(gjenåpnetOppgave, behandlingId, hendelse, behandling);
                loggEvent(gjenåpnetOppgave.getBehandlingId(), OppgaveEventType.GJENAPNET, null, enhet);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
                break;
        }
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
        OppgaveEventLogg eventLogg = new OppgaveEventLogg(oppgave.getBehandlingId(),
                OppgaveEventType.OPPRETTET,
                kriterie,
                oppgave.getBehandlendeEnhet());
        oppgaveRepository.lagre(eventLogg);
    }

    private void avsluttFpsakOppgaveOgLoggEvent(BehandlingId behandlingId, OppgaveEventType eventType, LocalDateTime frist, String behandlendeEnhet) {
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
        if (oppgaveHistorikk.erSisteEventÅpningsevent()){
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
        return oppgaveRepository.gjenåpneOppgaveForBehandling(behandlingId);
    }

    private void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
    }
}
