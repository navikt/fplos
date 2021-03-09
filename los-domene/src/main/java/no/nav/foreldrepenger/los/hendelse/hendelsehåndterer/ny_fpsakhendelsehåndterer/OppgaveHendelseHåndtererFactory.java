package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandlingKlient;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.List;
import java.util.function.Predicate;

@ApplicationScoped
public class OppgaveHendelseHåndtererFactory {

    private static final IkkeRelevantForOppgaveHendelseHåndterer IKKE_RELEVANT_FOR_OPPGAVE_HENDELSE_HÅNDTERER = new IkkeRelevantForOppgaveHendelseHåndterer();

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

    public FpsakHendelseHåndterer lagHåndterer(Hendelse hendelse, BehandlingFpsak tmpFpsak) {
        var behandlingId = hendelse.getBehandlingId();
        BehandlingFpsak behandlingFpsak;
        if (tmpFpsak != null) { // tmpFpsak er parameter for sammenlikning i prod før klassen tas i bruk. Bruker samme BehandlingFpsak for å sammenlikne epler med epler.
            behandlingFpsak = tmpFpsak;
        } else {
            behandlingFpsak = foreldrePengerBehandlingKlient.getBehandling(behandlingId);
        }
        behandlingFpsak.setYtelseType(hendelse.getYtelseType());
        behandlingFpsak.setSaksnummer(hendelse.getSaksnummer());
        behandlingFpsak.setAktørId(hendelse.getAktørId());
        var oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));
        return lagHåndterer(behandlingFpsak, oppgaveHistorikk);
    }

    protected FpsakHendelseHåndterer lagHåndterer(BehandlingFpsak behandlingFpsak,
                                                  OppgaveHistorikk oppgaveHistorikk) {
        var aksjonspunkter = behandlingFpsak.getAksjonspunkter();

        if (erIngenÅpne(aksjonspunkter)) {
            if (oppgaveHistorikk.erUtenHistorikk() || oppgaveHistorikk.erIngenÅpenOppgave()) {
                return IKKE_RELEVANT_FOR_OPPGAVE_HENDELSE_HÅNDTERER;
            } else {
                return new LukkOppgaveHendelseHåndterer(oppgaveRepository, oppgaveStatistikk, behandlingFpsak);
            }
        }

        if (finn(Aksjonspunkt::erPåVent, aksjonspunkter)) {
            if (oppgaveHistorikk.erPåVent()) {
                return IKKE_RELEVANT_FOR_OPPGAVE_HENDELSE_HÅNDTERER;
            }
            return new PåVentOppgaveHendelseHåndterer(oppgaveRepository, oppgaveStatistikk, behandlingFpsak);
        }

        if (finn(Aksjonspunkt::erTilBeslutter, aksjonspunkter)) {
            return oppgaveHistorikk.erÅpenOppgave() && oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()
                    ? new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak)
                    : new OpprettBeslutterOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak);
        }

        if (finn(Aksjonspunkt::erRegistrerPapirSøknad, aksjonspunkter)) {
            return oppgaveHistorikk.erÅpenOppgave() && oppgaveHistorikk.erSisteOpprettedeOppgavePapirsøknad()
                    ? new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak)
                    : new OpprettPapirsøknadOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak);
        }

        if (!oppgaveHistorikk.erUtenHistorikk()) {
            if (oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()) {
                // Ingen beslutteraksjonspunkt. Returnert fra beslutter, opprett ny oppgave
                return new ReturFraBeslutterHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak);
            }
            return oppgaveHistorikk.erIngenÅpenOppgave()
                    ? new GjenåpneOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak)
                    : new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak);
        }

        return new GenerellOpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak); //OPPRETT_OPPGAVE;
    }

    private static boolean finn(Predicate<Aksjonspunkt> predicate, List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream().filter(Aksjonspunkt::erAktiv).anyMatch(predicate);
    }

    private static boolean erIngenÅpne(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream().noneMatch(Aksjonspunkt::erAktiv);
    }

}
