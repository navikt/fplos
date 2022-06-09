package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.*;

import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;

public final class TransisjonUtleder {
    private static final Logger LOG = LoggerFactory.getLogger(TransisjonUtleder.class);

    private TransisjonUtleder() {
    }

    static Oppgavetransisjon utledAktuellTransisjon(BehandlingFpsak behandlingFpsak, OppgaveHistorikk oppgaveHistorikk) {
        LOG.info("Utleder aktuell oppgavetransisjon for behandlingId {}, oppgavehistorikk {}", behandlingFpsak.getBehandlingId(),
                oppgaveHistorikk);
        var aksjonspunkter = behandlingFpsak.getAksjonspunkter();

        if (erIngenÅpne(aksjonspunkter)) {
            if (oppgaveHistorikk.erUtenHistorikk() || oppgaveHistorikk.erIngenÅpenOppgave()) {
                return IKKE_RELEVANT;
            }
            return LUKK_OPPGAVE;
        }

        if (finn(Aksjonspunkt::erPåVent, aksjonspunkter)) {
            if (oppgaveHistorikk.erPåVent()) {
                return IKKE_RELEVANT;
            }
            return SETT_PÅ_VENT;
        }

        if (finn(Aksjonspunkt::erTilBeslutter, aksjonspunkter)) {
            return oppgaveHistorikk.erÅpenOppgave() && oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()
                    ? OPPDATER_OPPGAVE
                    : OPPRETT_BESLUTTEROPPGAVE;
        }

        if (finn(Aksjonspunkt::erRegistrerPapirSøknad, aksjonspunkter)) {
            return oppgaveHistorikk.erÅpenOppgave() && oppgaveHistorikk.erSisteOpprettedeOppgavePapirsøknad()
                    ? OPPDATER_OPPGAVE
                    : OPPRETT_PAPIRSØKNADOPPGAVE;
        }

        if (oppgaveHistorikk.harEksistertOppgave()) {
            if (oppgaveHistorikk.erÅpenOppgave()) {
                return oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()
                        ? RETUR_FRA_BESLUTTER_OPPGAVE
                        : OPPDATER_OPPGAVE;
            }
            return GJENÅPNE_OPPGAVE;
        }

        return OPPRETT_OPPGAVE;
    }

    private static boolean finn(Predicate<Aksjonspunkt> predicate, List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream().filter(Aksjonspunkt::erAktiv).anyMatch(predicate);
    }

    private static boolean erIngenÅpne(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream().noneMatch(Aksjonspunkt::erAktiv);
    }

}
