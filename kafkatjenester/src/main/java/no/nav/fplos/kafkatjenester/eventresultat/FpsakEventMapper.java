package no.nav.fplos.kafkatjenester.eventresultat;

import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.GJENÅPNE_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE_MANUELT_VENT;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.LUKK_OPPGAVE_VENT;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_OPPGAVE;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.OPPRETT_PAPIRSØKNAD_OPPGAVE;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.kafkatjenester.OppgaveHistorikk;


public class FpsakEventMapper {

    public static EventResultat signifikantEventFra(List<Aksjonspunkt> aksjonspunktListe,
                                                    OppgaveHistorikk oppgaveHistorikk, String behandlendeEnhet) {
        return signifikantEventFra(aksjonspunktListe, oppgaveHistorikk, behandlendeEnhet, false);
    }

    private static EventResultat signifikantEventFra(List<Aksjonspunkt> aksjonspunktListe, OppgaveHistorikk oppgaveHistorikk,
                                                     String behandlendeEnhet, boolean fraAdmin) {
        Set<Aksjonspunkt> åpneAksjonspunkter = aksjonspunktListe.stream()
                .filter(Aksjonspunkt::erAktiv)
                .collect(Collectors.toSet());
        return signifikantEventFra(åpneAksjonspunkter, oppgaveHistorikk, behandlendeEnhet, fraAdmin);
    }

    private static EventResultat signifikantEventFra(Set<Aksjonspunkt> åpneAksjonspunkt, OppgaveHistorikk oppgaveHistorikk,
                                                     String behandlendeEnhet, boolean fraAdmin) {
        if (åpneAksjonspunkt.isEmpty()){
            return LUKK_OPPGAVE;
        }
        if (påVent(åpneAksjonspunkt)) {
            return manueltSattPåVent(åpneAksjonspunkt) ? LUKK_OPPGAVE_MANUELT_VENT : LUKK_OPPGAVE_VENT;
        }
        if (tilBeslutter(åpneAksjonspunkt)) {
            if (!fraAdmin && harKriterie(oppgaveHistorikk, AndreKriterierType.TIL_BESLUTTER)) {
                return erSammeEnhet(oppgaveHistorikk, behandlendeEnhet)
                        ? GJENÅPNE_OPPGAVE
                        : OPPRETT_BESLUTTER_OPPGAVE;
            }
            if (!fraAdmin) {
                return OPPRETT_BESLUTTER_OPPGAVE;
            }
        }
        if (erRegistrerPapirsøknad(åpneAksjonspunkt)) {
            if (!fraAdmin && harKriterie(oppgaveHistorikk, AndreKriterierType.PAPIRSØKNAD)) {
                return erSammeEnhet(oppgaveHistorikk, behandlendeEnhet)
                        ? GJENÅPNE_OPPGAVE
                        : OPPRETT_PAPIRSØKNAD_OPPGAVE;
            }
            return OPPRETT_PAPIRSØKNAD_OPPGAVE;
        }
        if (!fraAdmin && harKriterie(oppgaveHistorikk, AndreKriterierType.PAPIRSØKNAD)) {
            return OPPRETT_OPPGAVE;
        }
        if (!fraAdmin && harKriterie(oppgaveHistorikk, AndreKriterierType.TIL_BESLUTTER)) {
            return OPPRETT_OPPGAVE;
        }
        if (!fraAdmin && oppgaveHistorikk.getSisteÅpningsEvent() != null) {
            return erSammeEnhet(oppgaveHistorikk, behandlendeEnhet)
                    ? GJENÅPNE_OPPGAVE
                    : OPPRETT_OPPGAVE;
        }
        return OPPRETT_OPPGAVE;
    }

    private static boolean erSammeEnhet(OppgaveHistorikk oppgaveHistorikk, String nyEnhet) {
        return oppgaveHistorikk.getSisteÅpningsEvent() != null
                && nyEnhet.equals(oppgaveHistorikk.getSisteÅpningsEvent().getBehandlendeEnhet());
    }

    private static boolean harKriterie(OppgaveHistorikk oppgaveHistorikk, AndreKriterierType kriterie) {
        return oppgaveHistorikk.getSisteÅpningsEvent() != null
                && kriterie != null
                && oppgaveHistorikk.getSisteÅpningsEvent().getAndreKriterierType() != null
                && oppgaveHistorikk.getSisteÅpningsEvent().getAndreKriterierType().equals(kriterie);
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
