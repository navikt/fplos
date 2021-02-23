package no.nav.fplos.kafkatjenester.eventresultat;

import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.IKKE_RELEVANT;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.GJENÅPNE_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE_MANUELT_VENT;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE_VENT;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_PAPIRSØKNAD_OPPGAVE;

import java.util.List;

import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.kafkatjenester.OppgaveHistorikk;


public class ForeldrepengerEventMapper {

    public static EventResultat finnEventResultat(BehandlingFpsak behandling,
                                                  OppgaveHistorikk oppgaveHistorikk,
                                                  String gjeldendeEnhet) {
        var aksjonspunkter = behandling.getAksjonspunkter();

        if (erIngenÅpne(aksjonspunkter)) {
            if (oppgaveHistorikk.erUtenHistorikk() || oppgaveHistorikk.erSisteEventLukkeevent()) {
                return IKKE_RELEVANT;
            } else {
                return LUKK_OPPGAVE;
            }
        }
        if (påVent(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteVenteEvent()) {
                return IKKE_RELEVANT;
            }
            return manueltSattPåVent(aksjonspunkter) ? LUKK_OPPGAVE_MANUELT_VENT : LUKK_OPPGAVE_VENT;
        }
        if (tilBeslutter(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteOppgaveTilBeslutter()
                    && oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(gjeldendeEnhet)) {
                return GJENÅPNE_OPPGAVE; // TODO: hvis åpen oppgave - oppdater oppgave i stedet for gjenåpning
            }
            return OPPRETT_BESLUTTER_OPPGAVE;
        }
        if (erRegistrerPapirsøknad(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteOppgavePapirsøknad()
                    && oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(gjeldendeEnhet)) {
                // TODO: oppdater åpen oppgave
                return GJENÅPNE_OPPGAVE;
            }
            return OPPRETT_PAPIRSØKNAD_OPPGAVE;
        }
        if (!oppgaveHistorikk.erUtenHistorikk()) {
            if (oppgaveHistorikk.erSisteOppgaveTilBeslutter()) {
                // Ingen beslutteraksjonspunkt. Returnert fra beslutter, opprett ny oppgave
                // TODO: innføre nytt EventResultat som mapper til håndtering som setter til siste saksbehandler?
                return OPPRETT_OPPGAVE;
            }
            return oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(gjeldendeEnhet)
                    ? GJENÅPNE_OPPGAVE // TODO: oppdater åpen oppgave
                    : OPPRETT_OPPGAVE;
        }
        return OPPRETT_OPPGAVE;
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
