package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.TransisjonUtleder.utledAktuellTransisjon;

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
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandling;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;

@ApplicationScoped
public class FpsakOppgaveHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(FpsakOppgaveHendelseHåndterer.class);
    private OppgaveRepository oppgaveRepository;
    private Instance<FpsakOppgavetransisjonHåndterer> håndterere;
    private ForeldrepengerBehandling foreldrePengerBehandlingKlient;

    @Inject
    public FpsakOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                         @Any Instance<FpsakOppgavetransisjonHåndterer> håndterere,
                                         ForeldrepengerBehandling foreldrePengerBehandlingKlient) {
        this.oppgaveRepository = oppgaveRepository;
        this.håndterere = håndterere;
        this.foreldrePengerBehandlingKlient = foreldrePengerBehandlingKlient;
    }

    public FpsakOppgaveHendelseHåndterer() {
    }

    public void håndter(Hendelse hendelse) {
        var behandlingId = hendelse.getBehandlingId();
        var behandlingFpsak = behandlingFpsak(hendelse);
        var oppgaveHistorikk = oppgavehistorikk(hendelse, behandlingId);
        var transisjonHåndterer = håndtererForTransisjon(utledAktuellTransisjon(behandlingFpsak, oppgaveHistorikk));
        LOG.info("Utledet hendelsehåndterer er av type {}", transisjonHåndterer.getClass().getSimpleName());
        transisjonHåndterer.håndter(behandlingFpsak);
    }

    private OppgaveHistorikk oppgavehistorikk(Hendelse hendelse, BehandlingId behandlingId) {
        var oppgaveEventer = oppgaveRepository.hentOppgaveEventer(behandlingId);
        LOG.info("Henter tidigere oppgaveeventer for behandling {} {}", hendelse.getBehandlingId(), inlinetEventHistorikk(oppgaveEventer));
        return new OppgaveHistorikk(oppgaveEventer);
    }

    private BehandlingFpsak behandlingFpsak(Hendelse hendelse) {
        var behandlingFpsak = foreldrePengerBehandlingKlient.getBehandling(hendelse.getBehandlingId());
        behandlingFpsak.setYtelseType(hendelse.getYtelseType());
        behandlingFpsak.setSaksnummer(new Saksnummer(hendelse.getSaksnummer()));
        behandlingFpsak.setAktørId(hendelse.getAktørId());
        return behandlingFpsak;
    }

    private FpsakOppgavetransisjonHåndterer håndtererForTransisjon(Oppgavetransisjon type) {
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

}
