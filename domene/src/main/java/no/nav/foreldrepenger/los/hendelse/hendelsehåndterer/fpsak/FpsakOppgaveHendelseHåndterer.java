package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

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
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer.Oppgavetransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class FpsakOppgaveHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(FpsakOppgaveHendelseHåndterer.class);
    private OppgaveRepository oppgaveRepository;
    private Instance<FpsakOppgavetransisjonHåndterer> håndterere;

    @Inject
    public FpsakOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository, @Any Instance<FpsakOppgavetransisjonHåndterer> håndterere) {
        this.oppgaveRepository = oppgaveRepository;
        this.håndterere = håndterere;
    }

    public FpsakOppgaveHendelseHåndterer() {
    }

    public void håndterBehandling(LosBehandlingDto behandlingDto) {
        var behandlingId = BehandlingId.fromUUID(behandlingDto.behandlingUuid());
        var oppgaveHistorikk = oppgavehistorikk(behandlingId);
        var transisjonHåndterer = håndtererForTransisjon(TransisjonUtleder.utledAktuellTransisjon(behandlingId, behandlingDto, oppgaveHistorikk));
        LOG.info("Utledet hendelsehåndterer er av type {}", transisjonHåndterer.getClass().getSimpleName());
        transisjonHåndterer.håndter(behandlingId, behandlingDto, oppgaveHistorikk);
    }

    private OppgaveHistorikk oppgavehistorikk(BehandlingId behandlingId) {
        var oppgaveEventer = oppgaveRepository.hentOppgaveEventer(behandlingId);
        if (LOG.isInfoEnabled()) {
            LOG.info("Henter tidigere oppgaveeventer for behandling {} {}", behandlingId, inlinetEventHistorikk(oppgaveEventer));
        }
        return new OppgaveHistorikk(oppgaveEventer);
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
            .map(e -> e.getAndreKriterierType() == null ? e.getEventType().name() : e.getEventType().name() + " (" + e.getAndreKriterierType() + ")")
            .collect(Collectors.joining(", "));
    }

}
