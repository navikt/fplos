package no.nav.fplos.domenetjenester.hendelsehåndterer.ny_fpsakhendelsehåndterer;

import no.nav.foreldrepenger.loslager.hendelse.Hendelse;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.domenetjenester.statistikk_ny.OppgaveStatistikk;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingKlient;
import no.nav.fplos.domenetjenester.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.fplos.domenetjenester.hendelsehåndterer.OppgaveHistorikk;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.List;
import java.util.function.Predicate;

@ApplicationScoped
public class OppgaveHendelseHåndtererFactory {

    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppgaveStatistikk oppgaveStatistikk;
    private ForeldrepengerBehandlingKlient foreldrePengerBehandlingKlient;


    @Inject
    public OppgaveHendelseHåndtererFactory(ForeldrepengerBehandlingKlient foreldrePengerBehandlingKlient,
                                           OppgaveRepository oppgaveRepository,
                                           OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                           OppgaveStatistikk oppgaveStatistikk) {
        this.foreldrePengerBehandlingKlient = foreldrePengerBehandlingKlient;
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.oppgaveStatistikk = oppgaveStatistikk;
    }

    public OppgaveHendelseHåndtererFactory() {
    }

    public FpsakHendelseHåndterer lagHåndterer(Hendelse hendelse) {
        var behandlingId = hendelse.getBehandlingId();
        var behandlingFpsak = foreldrePengerBehandlingKlient.getBehandling(behandlingId);
        behandlingFpsak.setYtelseType(hendelse.getYtelseType());
        behandlingFpsak.setSaksnummer(hendelse.getSaksnummer());
        behandlingFpsak.setAktørId(hendelse.getAktørId());
        var oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));
        return lagHåndterer(behandlingFpsak, oppgaveHistorikk);
    }

    protected FpsakHendelseHåndterer lagHåndterer(BehandlingFpsak behandling,
                                                  OppgaveHistorikk oppgaveHistorikk) {
        var aksjonspunkter = behandling.getAksjonspunkter();
        var gjeldendeEnhet = behandling.getBehandlendeEnhetId(); //TODO: test at dette er riktig enhet

        if (erIngenÅpne(aksjonspunkter)) {
            if (oppgaveHistorikk.erUtenHistorikk() || oppgaveHistorikk.erIngenÅpenOppgave()) {
                return new IkkeRelevantForOppgaveHendelseHåndterer();
            } else {
                return new LukkOppgaveHendelseHåndterer();
            }
        }
        if (erPåVentAksjonspunkt(aksjonspunkter)) {
            if (oppgaveHistorikk.erPåVent()) {
                return new IkkeRelevantForOppgaveHendelseHåndterer();
            }
            return new PåVentOppgaveHendelseHåndterer(oppgaveRepository, oppgaveStatistikk, behandling);
        }
        if (tilBeslutter(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()
                    && oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(gjeldendeEnhet)) {
                return null; //GJENÅPNE_OPPGAVE; // TODO: hvis åpen oppgave - oppdater oppgave i stedet for gjenåpning
            }
            return null; //OPPRETT_BESLUTTER_OPPGAVE;
        }
        if (erRegistrerPapirsøknad(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteOpprettedeOppgavePapirsøknad()
                    && oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(gjeldendeEnhet)) {
                // TODO: oppdater åpen oppgave
                return null; //GJENÅPNE_OPPGAVE;
            }
            return null; //OPPRETT_PAPIRSØKNAD_OPPGAVE;
        }
        if (!oppgaveHistorikk.erUtenHistorikk()) {
            if (oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()) {
                // Ingen beslutteraksjonspunkt. Returnert fra beslutter, opprett ny oppgave
                // TODO: innføre nytt EventResultat som mapper til håndtering som setter til siste saksbehandler?
                return null; //OPPRETT_OPPGAVE;
            }
            return null; //oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(gjeldendeEnhet)
                    //? GJENÅPNE_OPPGAVE // TODO: oppdater åpen oppgave
                    //: OPPRETT_OPPGAVE;
        }
        return new OpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandling); //OPPRETT_OPPGAVE;
    }

//                case OPPDATER_ÅPEN_OPPGAVE:
//    håndterOppfriskOppgave(hendelse, behandling);
//                break;
//}
//    }
//
//private void håndterOppfriskOppgave(Hendelse hendelse, BehandlingFpsak behandlingFpsak) {
//        LOG.info("Oppfrisker oppgave");
//        var behandlingId = behandlingFpsak.getBehandlingId();
//        var oppgave = oppgaveRepository.hentOppgaver(behandlingId)
//        .stream().filter(Oppgave::getAktiv).findFirst().orElseThrow();
//        oppdaterOppgaveInformasjon(oppgave, behandlingId, hendelse, behandlingFpsak);
//        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
//        oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
//        }

    private static boolean erAktivtAksjonspunkt(List<Aksjonspunkt> aksjonspunkter, Predicate<Aksjonspunkt> predicate) {
        return aksjonspunkter.stream()
                .filter(Aksjonspunkt::erAktiv)
                .anyMatch(predicate);
    }

    private static boolean erPåVentAksjonspunkt(List<Aksjonspunkt> åpneAksjonspunkt) {
        return åpneAksjonspunkt.stream()
                .filter(Aksjonspunkt::erAktiv)
                .anyMatch(Aksjonspunkt::erPåVent);
    }

    private static boolean tilBeslutter(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream()
                .filter(Aksjonspunkt::erAktiv)
                .anyMatch(Aksjonspunkt::tilBeslutter);
    }

    private static boolean erRegistrerPapirsøknad(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream()
                .filter(Aksjonspunkt::erAktiv)
                .anyMatch(Aksjonspunkt::erRegistrerPapirSøknad);
    }

    private static boolean erIngenÅpne(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream().noneMatch(Aksjonspunkt::erAktiv);
    }


}
