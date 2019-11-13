package no.nav.fplos.kafkatjenester.eventresultat;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.GJENÅPNE_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE_MANUELT_VENT;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE_VENT;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_PAPIRSØKNAD_OPPGAVE;


public class FpsakEventMapper {

    public static EventResultat signifikantEventForAdminFra(List<Aksjonspunkt> aksjonspunktListe) {
        return signifikantEventFra(aksjonspunktListe, null, null, true);
    }

    public static EventResultat signifikantEventFra(List<Aksjonspunkt> aksjonspunktListe,
                                                    List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        return signifikantEventFra(aksjonspunktListe, oppgaveEventLogger, behandlendeEnhet, false);
    }

    private static EventResultat signifikantEventFra(List<Aksjonspunkt> aksjonspunktListe, List<OppgaveEventLogg> oppgaveEventLogger,
                                                     String behandlendeEnhet, boolean fraAdmin) {
        Set<Aksjonspunkt> åpneAksjonspunkter = aksjonspunktListe.stream()
                .filter(Aksjonspunkt::erAktiv)
                .collect(Collectors.toSet());
        return signifikantEventFra(åpneAksjonspunkter, sisteOpprettedeEvent(oppgaveEventLogger), behandlendeEnhet, fraAdmin);
    }

    private static EventResultat signifikantEventFra(Set<Aksjonspunkt> åpneAksjonspunkt, OppgaveEventLogg sisteEvent,
                                                     String behandlendeEnhet, boolean fraAdmin) {
        if (åpneAksjonspunkt.isEmpty()){
            return LUKK_OPPGAVE;
        }
        if (påVent(åpneAksjonspunkt)) {
            return manueltSattPåVent(åpneAksjonspunkt) ? LUKK_OPPGAVE_MANUELT_VENT : LUKK_OPPGAVE_VENT;
        }
        if (tilBeslutter(åpneAksjonspunkt)) {
            if (!fraAdmin && harKriterie(sisteEvent, AndreKriterierType.TIL_BESLUTTER)) {
                return erSammeEnhet(sisteEvent.getBehandlendeEnhet(), behandlendeEnhet)
                        ? GJENÅPNE_OPPGAVE
                        : OPPRETT_BESLUTTER_OPPGAVE;
            }
            if (!fraAdmin && harKriterie(sisteEvent, AndreKriterierType.TIL_BESLUTTER)
                    && erSammeEnhet(sisteEvent.getBehandlendeEnhet(), behandlendeEnhet)) {
                return GJENÅPNE_OPPGAVE;
            }
            return OPPRETT_BESLUTTER_OPPGAVE;
        }
        if (erRegistrerPapirsøknad(åpneAksjonspunkt)) {
            if (!fraAdmin && harKriterie(sisteEvent, AndreKriterierType.PAPIRSØKNAD)) {
                return erSammeEnhet(sisteEvent.getBehandlendeEnhet(), behandlendeEnhet)
                        ? GJENÅPNE_OPPGAVE
                        : OPPRETT_PAPIRSØKNAD_OPPGAVE;
            }
            return OPPRETT_PAPIRSØKNAD_OPPGAVE;
        }
        if (!fraAdmin && harKriterie(sisteEvent, AndreKriterierType.PAPIRSØKNAD)) {
            return OPPRETT_OPPGAVE;
        }
        if (!fraAdmin && harKriterie(sisteEvent, AndreKriterierType.TIL_BESLUTTER)) {
            return OPPRETT_OPPGAVE;
        }
        if (!fraAdmin && sisteEvent != null) {
            return erSammeEnhet(sisteEvent.getBehandlendeEnhet(), behandlendeEnhet)
                    ? GJENÅPNE_OPPGAVE
                    : OPPRETT_OPPGAVE;
        }
        return OPPRETT_OPPGAVE;
    }

    private static OppgaveEventLogg sisteOpprettedeEvent(List<OppgaveEventLogg> oppgaveEventLogger) {
        return oppgaveEventLogger.stream()
                .filter(e -> e.getEventType().equals(OppgaveEventType.OPPRETTET))
                .findFirst()
                .orElse(null);
    }

    private static boolean erSammeEnhet(String initiellEnhet, String nyEnhet) {
        return initiellEnhet != null & nyEnhet != null && nyEnhet.equals(initiellEnhet);
    }

    private static boolean harKriterie(OppgaveEventLogg sisteEvent, AndreKriterierType kriterie) {
        if (sisteEvent == null) return false;
        return (sisteEvent.getAndreKriterierType() != null && sisteEvent.getAndreKriterierType().equals(kriterie));
    }

    private static boolean påVent(Set<Aksjonspunkt> åpneAksjonspunkt) {
        return åpneAksjonspunkt.stream()
                .anyMatch(Aksjonspunkt::erPåVent);
    }

    private static boolean tilBeslutter(Set<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream()
                .anyMatch(Aksjonspunkt::tilBeslutter);
    }

    private static boolean manueltSattPåVent(Set<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream()
                .anyMatch(Aksjonspunkt::erManueltPåVent);
    }

    private static boolean erRegistrerPapirsøknad(Set<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream()
                .anyMatch(Aksjonspunkt::erRegistrerPapirSøknad);
    }
}
