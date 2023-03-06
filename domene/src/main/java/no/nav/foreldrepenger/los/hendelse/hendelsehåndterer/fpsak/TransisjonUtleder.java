package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.GJENÅPNE_OPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.IKKE_RELEVANT;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.LUKK_OPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.OPPDATER_OPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.OPPRETT_BESLUTTEROPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.OPPRETT_OPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.OPPRETT_PAPIRSØKNADOPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.RETUR_FRA_BESLUTTER_OPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon.SETT_PÅ_VENT;

import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public final class TransisjonUtleder {
    private static final Logger LOG = LoggerFactory.getLogger(TransisjonUtleder.class);

    private TransisjonUtleder() {
    }

    static Oppgavetransisjon utledAktuellTransisjon(BehandlingId behandlingId, LosBehandlingDto behandlingFpsak, OppgaveHistorikk oppgaveHistorikk) {
        LOG.info("Utleder aktuell oppgavetransisjon for behandlingId {}, oppgavehistorikk {}", behandlingId, oppgaveHistorikk);
        var aksjonspunkter = behandlingFpsak.aksjonspunkt().stream().map(Aksjonspunkt::aksjonspunktFra).toList();

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
