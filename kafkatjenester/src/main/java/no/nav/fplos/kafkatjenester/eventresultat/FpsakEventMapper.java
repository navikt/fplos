package no.nav.fplos.kafkatjenester.eventresultat;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.GJENÅPNE_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE_MANUELT_VENT;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE_VENT;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_PAPIRSØKNAD_OPPGAVE;


public class FpsakEventMapper {

    public static final String AUTOMATISK_MARKERING_AV_UTENLANDSSAK_AKSJONSPUNKTSKODE = "5068";
    public static final String MANUELT_SATT_PÅ_VENT_AKSJONSPUNKTSKODE = "7001";
    private static final String BESLUTTER_AKSJONSPUNKTSKODE = "5016";
    private static final List<String> REGISTRER_PAPIRSØKNAD_AKSJONSPUNKTSKODE = asList("5012", "5040", "5057");
    private static final String PÅ_VENT_AKSJONSPUNKT_GRUPPE_STARTER_MED = "7";

    private static final List<String> aktiveAksjonspunktkoder = Collections.singletonList("OPPR");
    private static final List<String> avbruttAksjonspunktkoder = Collections.singletonList("AVBR");

    public static EventResultat signifikantEventForAdminFra(List<AksjonspunktDto> aksjonspunktListe) {
        return signifikantEventFra(aksjonspunktListe, null, null, true);
    }

    public static EventResultat signifikantEventFra(List<AksjonspunktDto> aksjonspunktListe,
                                                    List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        return signifikantEventFra(aksjonspunktListe, oppgaveEventLogger, behandlendeEnhet, false);
    }

    private static EventResultat signifikantEventFra(List<AksjonspunktDto> aksjonspunktListe, List<OppgaveEventLogg> oppgaveEventLogger,
                                                     String behandlendeEnhet, boolean fraAdmin) {
        Set<AksjonspunktDto> åpneAksjonspunkter = aksjonspunktListe.stream()
                .filter(entry -> aktiveAksjonspunktkoder.contains(entry.getStatus().getKode()))
                .collect(Collectors.toSet());
        return signifikantEventFra(åpneAksjonspunkter, sisteOpprettedeEvent(oppgaveEventLogger), behandlendeEnhet, fraAdmin);
    }

    private static EventResultat signifikantEventFra(Set<AksjonspunktDto> åpneAksjonspunkt, OppgaveEventLogg sisteEvent,
                                                     String behandlendeEnhet, boolean fraAdmin) {
        if (åpneAksjonspunkt.isEmpty()){
            return LUKK_OPPGAVE;
        }
        if (finnesPåVentAksjonspunktI(åpneAksjonspunkt)) {
            return aksjonspunktFinnes(åpneAksjonspunkt, MANUELT_SATT_PÅ_VENT_AKSJONSPUNKTSKODE)
                    ? LUKK_OPPGAVE_MANUELT_VENT
                    : LUKK_OPPGAVE_VENT;
        }
        if (aksjonspunktFinnes(åpneAksjonspunkt, BESLUTTER_AKSJONSPUNKTSKODE)) {
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
        if (aksjonspunktFinnes(åpneAksjonspunkt, REGISTRER_PAPIRSØKNAD_AKSJONSPUNKTSKODE)) {
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

    private static boolean finnesPåVentAksjonspunktI(Set<AksjonspunktDto> åpneAksjonspunkt) {
        return åpneAksjonspunkt.stream()
                .anyMatch(entry -> entry.getDefinisjon().getKode().startsWith(PÅ_VENT_AKSJONSPUNKT_GRUPPE_STARTER_MED));
    }

    private static boolean aksjonspunktFinnes(Set<AksjonspunktDto> aksjonspunkt, String target) {
        return aksjonspunkt.stream()
                .anyMatch(a -> a.getDefinisjon().getKode().equals(target));
    }

    private static boolean aksjonspunktFinnes(Set<AksjonspunktDto> aksjonspunkt, List<String> targets) {
        return aksjonspunkt.stream()
                .map(a -> a.getDefinisjon().getKode())
                .anyMatch(targets::contains);
    }

}
