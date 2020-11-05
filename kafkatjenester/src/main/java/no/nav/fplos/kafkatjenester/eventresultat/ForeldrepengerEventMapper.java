package no.nav.fplos.kafkatjenester.eventresultat;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.PAPIRSØKNAD;
import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.TIL_BESLUTTER;
import static no.nav.fplos.kafkatjenester.eventresultat.EventResultat.FERDIG;
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

    public static EventResultat signifikantEventFra(BehandlingFpsak behandling,
                                                    OppgaveHistorikk oppgaveHistorikk,
                                                    String gjeldendeEnhet) {
        var aksjonspunkter = behandling.getAksjonspunkter();

        if (erIngenÅpne(aksjonspunkter)) {
            if (oppgaveHistorikk.erUtenHistorikk() || oppgaveHistorikk.erSisteEventLukkeevent()) {
                return FERDIG;
            } else {
                return LUKK_OPPGAVE;
            }
        }
        if (påVent(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteVenteEvent()) {
                return FERDIG;
            }
            return manueltSattPåVent(aksjonspunkter) ? LUKK_OPPGAVE_MANUELT_VENT : LUKK_OPPGAVE_VENT;
        }
        if (tilBeslutter(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteÅpningsEventKriterie(TIL_BESLUTTER) //siste oppgave er beslutteroppgave
                    && oppgaveHistorikk.erSammeEnhet(gjeldendeEnhet)) {
                return GJENÅPNE_OPPGAVE;
            }
            return OPPRETT_BESLUTTER_OPPGAVE;
        }
        if (erRegistrerPapirsøknad(aksjonspunkter)) {
            if (oppgaveHistorikk.erSisteÅpningsEventKriterie(PAPIRSØKNAD) //siste oppgave er papirsøknad
                    && oppgaveHistorikk.erSammeEnhet(gjeldendeEnhet)) {
                return GJENÅPNE_OPPGAVE;
            }
            return OPPRETT_PAPIRSØKNAD_OPPGAVE;
        }
        if (!oppgaveHistorikk.erUtenHistorikk()) {
            if (oppgaveHistorikk.erSisteÅpningsEventKriterie(TIL_BESLUTTER)) {
                return OPPRETT_OPPGAVE; //returnert fra beslutter, opprett ny oppgave
            }
            return oppgaveHistorikk.erSammeEnhet(gjeldendeEnhet)
                    ? GJENÅPNE_OPPGAVE
                    : OPPRETT_OPPGAVE;
        }
        return OPPRETT_OPPGAVE;
    }

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
