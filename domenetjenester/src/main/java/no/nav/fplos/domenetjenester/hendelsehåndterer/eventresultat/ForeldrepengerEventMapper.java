package no.nav.fplos.domenetjenester.hendelsehåndterer.eventresultat;

import java.util.List;

import no.nav.fplos.domenetjenester.hendelsehåndterer.OppgaveHistorikk;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;


public class ForeldrepengerEventMapper {

    public static EventResultat finnEventResultat(BehandlingFpsak behandling,
                                                  OppgaveHistorikk oppgaveHistorikk,
                                                  String gjeldendeEnhet) {
        var aksjonspunkter = behandling.getAksjonspunkter();

        if (erIngenÅpne(aksjonspunkter)) {
            if (oppgaveHistorikk.erUtenHistorikk() || oppgaveHistorikk.erIngenÅpenOppgave()) {
                return EventResultat.IKKE_RELEVANT;
            } else {
                return EventResultat.LUKK_OPPGAVE;
            }
        }
        if (påVent(aksjonspunkter)) {
            if (oppgaveHistorikk.erPåVent()) {
                return EventResultat.IKKE_RELEVANT;
            }
            return manueltSattPåVent(aksjonspunkter) ? EventResultat.LUKK_OPPGAVE_MANUELT_VENT : EventResultat.LUKK_OPPGAVE_VENT;
        }
        if (tilBeslutter(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()
                    && oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(gjeldendeEnhet)) {
                return EventResultat.GJENÅPNE_OPPGAVE; // TODO: hvis åpen oppgave - oppdater oppgave i stedet for gjenåpning
            }
            return EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
        }
        if (erRegistrerPapirsøknad(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteOpprettedeOppgavePapirsøknad()
                    && oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(gjeldendeEnhet)) {
                // TODO: oppdater åpen oppgave
                return EventResultat.GJENÅPNE_OPPGAVE;
            }
            return EventResultat.OPPRETT_PAPIRSØKNAD_OPPGAVE;
        }
        if (!oppgaveHistorikk.erUtenHistorikk()) {
            if (oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()) {
                // Ingen beslutteraksjonspunkt. Returnert fra beslutter, opprett ny oppgave
                // TODO: innføre nytt EventResultat som mapper til håndtering som setter til siste saksbehandler?
                return EventResultat.OPPRETT_OPPGAVE;
            }
            return oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(gjeldendeEnhet)
                    ? EventResultat.GJENÅPNE_OPPGAVE // TODO: oppdater åpen oppgave
                    : EventResultat.OPPRETT_OPPGAVE;
        }
        return EventResultat.OPPRETT_OPPGAVE;
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

    private static boolean påVent(List<Aksjonspunkt> åpneAksjonspunkt) {
        return åpneAksjonspunkt.stream()
                .filter(Aksjonspunkt::erAktiv)
                .anyMatch(Aksjonspunkt::erPåVent);
    }

    private static boolean tilBeslutter(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream()
                .filter(Aksjonspunkt::erAktiv)
                .anyMatch(Aksjonspunkt::tilBeslutter);
    }

    private static boolean manueltSattPåVent(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream()
                .filter(Aksjonspunkt::erAktiv)
                .anyMatch(Aksjonspunkt::erManueltPåVent);
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
