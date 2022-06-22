package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonUtleder.utledOppgavetransisjon;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;


@ApplicationScoped
public class FptilbakeOppgavehendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveHendelseHåndterer.class);
    private OppgaveRepository oppgaveRepository;
    private Instance<FptilbakeOppgavetransisjonHåndterer> håndterere;

    @Inject
    public FptilbakeOppgavehendelseHåndterer(OppgaveRepository oppgaveRepository,
                                             @Any Instance<FptilbakeOppgavetransisjonHåndterer> håndterere) {
        this.oppgaveRepository = oppgaveRepository;
        this.håndterere = håndterere;
    }

    public FptilbakeOppgavehendelseHåndterer() {
    }


    public void håndter(TilbakekrevingHendelse hendelse) {
        var behandlingId = hendelse.getBehandlingId();
        var oppgaveHistorikk = oppgavehistorikk(hendelse, behandlingId);
        var aktuellTransisjon = utledOppgavetransisjon(oppgaveHistorikk,
                new FptilbakeOppgaveEgenskapFinner(hendelse.getAksjonspunkter(), hendelse.getAnsvarligSaksbehandler()),
                hendelse.getAksjonspunkter(), hendelse.getBehandlendeEnhet());
        var transisjonHåndterer = håndtererForTransisjon(aktuellTransisjon);
        LOG.info("Utledet hendelsehåndterer er av type {}", transisjonHåndterer.getClass().getSimpleName());
        transisjonHåndterer.håndter(new FptilbakeData(hendelse, oppgaveHistorikk));
    }

    private OppgaveHistorikk oppgavehistorikk(Hendelse hendelse, BehandlingId behandlingId) {
        var oppgaveEventer = oppgaveRepository.hentOppgaveEventer(behandlingId);
        LOG.info("Tidligere oppgaveeventer for TBK-behandling {} {}", hendelse.getBehandlingId(), inlinetEventHistorikk(oppgaveEventer));
        return new OppgaveHistorikk(oppgaveEventer);
    }

    private FptilbakeOppgavetransisjonHåndterer håndtererForTransisjon(Oppgavetransisjon type) {
        return håndterere.stream()
                .filter(th -> th.kanHåndtere() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Fant ingen håndterer for type" + type));
    }

    public static String inlinetEventHistorikk(List<OppgaveEventLogg> eventer) {
        return eventer.stream()
                .sorted(Comparator.comparing(OppgaveEventLogg::getOpprettetTidspunkt))
                .map(e -> e.getAndreKriterierType() == null
                        ? e.getEventType().name()
                        : e.getEventType().name() + " (" + e.getAndreKriterierType() + ")")
                .collect(Collectors.joining(", "));
    }

    public record FptilbakeData(TilbakekrevingHendelse hendelse, OppgaveHistorikk oppgaveHistorikk) {
        public FptilbakeOppgaveEgenskapFinner egenskapFinner() {
            return new FptilbakeOppgaveEgenskapFinner(hendelse.getAksjonspunkter(), hendelse.getAnsvarligSaksbehandler());
        }
    }
}