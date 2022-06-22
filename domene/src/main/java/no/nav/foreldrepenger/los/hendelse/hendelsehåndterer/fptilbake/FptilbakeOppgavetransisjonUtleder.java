package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

import java.util.List;
import java.util.Set;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon.LUKK_OPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon.OPPDATER_OPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon.OPPGAVE_TIL_NY_ENHET;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon.OPPRETT_BESLUTTEROPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon.OPPRETT_OPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon.RETUR_FRA_BESLUTTER_OPPGAVE;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon.SETT_PÅ_VENT;

public class FptilbakeOppgavetransisjonUtleder {

    private FptilbakeOppgavetransisjonUtleder() {
    }

    public static Oppgavetransisjon utledOppgavetransisjon(OppgaveHistorikk oppgaveHistorikk,
                                                           OppgaveEgenskapFinner egenskaper,
                                                           List<Aksjonspunkt> aksjonspunkter,
                                                           String behandlendeEnhet) {

        if (aktivManuellVent(aksjonspunkter)) {
            return SETT_PÅ_VENT;
        }
        if (harAktiveAksjonspunkt(aksjonspunkter) && oppgaveHistorikk.erÅpenOppgave()) {
            if (erTilBeslutter(egenskaper) && !oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()) {
                return OPPRETT_BESLUTTEROPPGAVE;
            }
            if (!erTilBeslutter(egenskaper) && oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()) {
                // ikke til beslutter pt, dermed retur fra beslutter
                return RETUR_FRA_BESLUTTER_OPPGAVE;
            }
            return oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(behandlendeEnhet)
                    ? OPPDATER_OPPGAVE
                    : OPPGAVE_TIL_NY_ENHET;
        }
        if (harAktiveAksjonspunkt(aksjonspunkter)) {
            return OPPRETT_OPPGAVE;
        }
        return LUKK_OPPGAVE;
    }

    private static boolean harAktiveAksjonspunkt(List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream().anyMatch(Aksjonspunkt::erOpprettet);
    }

    private static boolean aktivManuellVent(List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream().anyMatch(a -> Set.of("7001", "7002").contains(a.getKode()) && a.erOpprettet());
    }

    private static boolean erTilBeslutter(OppgaveEgenskapFinner egenskapFinner) {
        return egenskapFinner.getAndreKriterier().contains(AndreKriterierType.TIL_BESLUTTER);
    }
}
