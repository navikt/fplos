package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.util.List;
import java.util.function.Predicate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GenerellOpprettOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GjenåpneOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.IkkeRelevantForOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.LukkOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.OppdaterOppgaveegenskaperHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.OpprettBeslutterOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.OpprettPapirsøknadOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.PåVentOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.ReturFraBeslutterHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.FPBehandling;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;

@ApplicationScoped
public class OppgaveHendelseHåndtererFactory {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveHendelseHåndtererFactory.class);
    private static final IkkeRelevantForOppgaveHendelseHåndterer IKKE_RELEVANT_FOR_OPPGAVE_HENDELSE_HÅNDTERER = new IkkeRelevantForOppgaveHendelseHåndterer();

    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppgaveStatistikk oppgaveStatistikk;
    private FPBehandling foreldrePengerBehandlingKlient;

    @Inject
    public OppgaveHendelseHåndtererFactory(FPBehandling foreldrePengerBehandlingKlient,
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

    public FpsakHendelseHåndterer lagHåndterer(Hendelse hendelse) {
        var behandlingId = hendelse.getBehandlingId();
        var behandlingFpsak = foreldrePengerBehandlingKlient.getBehandling(behandlingId);
        behandlingFpsak.setYtelseType(hendelse.getYtelseType());
        behandlingFpsak.setSaksnummer(new Saksnummer(hendelse.getSaksnummer()));
        behandlingFpsak.setAktørId(hendelse.getAktørId());
        var eventer = oppgaveRepository.hentOppgaveEventer(behandlingId);
        LOG.info("Henter tidigere oppgaveeventer for behandling {} {}", hendelse.getBehandlingId(), eventer);
        var oppgaveHistorikk = new OppgaveHistorikk(eventer);
        var hendelseHåndterer = lagHåndterer(behandlingFpsak, oppgaveHistorikk);
        LOG.info("Utledet hendelsehåndterer er av type {}", hendelseHåndterer.getClass().getSimpleName());
        return hendelseHåndterer;
    }

    protected FpsakHendelseHåndterer lagHåndterer(BehandlingFpsak behandlingFpsak, OppgaveHistorikk oppgaveHistorikk) {
        LOG.info("Utleder hendelsehåndterer for behandlingId {}, oppgavehistorikk {}", behandlingFpsak.getBehandlingId(),
                oppgaveHistorikk);
        var aksjonspunkter = behandlingFpsak.getAksjonspunkter();

        if (erIngenÅpne(aksjonspunkter)) {
            if (oppgaveHistorikk.erUtenHistorikk() || oppgaveHistorikk.erIngenÅpenOppgave()) {
                return IKKE_RELEVANT_FOR_OPPGAVE_HENDELSE_HÅNDTERER;
            }
            return new LukkOppgaveHendelseHåndterer(oppgaveRepository, oppgaveStatistikk, behandlingFpsak);
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

        if (oppgaveHistorikk.harEksistertOppgave()) {
            if (oppgaveHistorikk.erÅpenOppgave()) {
                return oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()
                        ? new ReturFraBeslutterHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak)
                        : new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk,
                                behandlingFpsak);
            }
            return new GjenåpneOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak);
        }

        return new GenerellOpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak);
    }

    private static boolean finn(Predicate<Aksjonspunkt> predicate, List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream().filter(Aksjonspunkt::erAktiv).anyMatch(predicate);
    }

    private static boolean erIngenÅpne(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream().noneMatch(Aksjonspunkt::erAktiv);
    }

}
