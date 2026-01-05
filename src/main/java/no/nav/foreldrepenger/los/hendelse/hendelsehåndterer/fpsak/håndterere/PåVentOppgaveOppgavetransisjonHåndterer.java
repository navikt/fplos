package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.felles.util.StreamUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class PåVentOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(PåVentOppgaveOppgavetransisjonHåndterer.class);
    private OppgaveTjeneste oppgaveTjeneste;

    @Inject
    public PåVentOppgaveOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    public PåVentOppgaveOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling, OppgaveHistorikk eventHistorikk) {
        var behandlendeEnhet = behandling.behandlendeEnhetId();
        var aksjonspunkter = Optional.ofNullable(behandling.aksjonspunkt()).orElseGet(List::of)
            .stream().map(Aksjonspunkt::aksjonspunktFra).toList();
        var venteType = manueltSattPåVent(aksjonspunkter) ? OppgaveEventType.MANU_VENT : OppgaveEventType.VENT;
        var aksjonspunktFrist = aksjonspunktFrist(aksjonspunkter, venteType);

        LOG.info("{} behandling er satt på vent, type {}", SYSTEM, venteType);

        var aktivOppgave = oppgaveTjeneste.hentAktivOppgave(behandlingId).filter(Oppgave::getAktiv);
        if (aktivOppgave.isPresent()) {
            LOG.info("Lukker aktiv oppgave for behandling {}", behandlingId);
            oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
        }

        var oel = new OppgaveEventLogg(behandlingId, venteType, null, behandlendeEnhet, aksjonspunktFrist);
        oppgaveTjeneste.lagre(oel);
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.SETT_PÅ_VENT;
    }

    private static LocalDateTime aksjonspunktFrist(List<Aksjonspunkt> aksjonspunkter, OppgaveEventType type) {
        if (type.equals(OppgaveEventType.MANU_VENT)) {
            return finnVentefrist(aksjonspunkter, Aksjonspunkt::erManueltPåVent);
        }
        return finnVentefrist(aksjonspunkter, Aksjonspunkt::erPåVent);
    }

    private static LocalDateTime finnVentefrist(List<Aksjonspunkt> aksjonspunkter, Predicate<Aksjonspunkt> predicate) {
        return StreamUtil.safeStream(aksjonspunkter).filter(Aksjonspunkt::erAktiv)
                         .filter(predicate)
                         .map(Aksjonspunkt::getFristTid)
                         .filter(Objects::nonNull)
                         .findAny()
                         .orElse(null);
    }

    private static boolean manueltSattPåVent(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream().filter(Aksjonspunkt::erAktiv).anyMatch(Aksjonspunkt::erManueltPåVent);
    }
}
