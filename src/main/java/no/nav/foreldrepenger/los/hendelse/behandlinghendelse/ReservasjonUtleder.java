package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.util.Optional;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Behandling;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;

class ReservasjonUtleder {

    private ReservasjonUtleder() {
        //CDI
    }

    static Optional<Reservasjon> utledReservasjon(Oppgave nyOppgave, Optional<Oppgave> eksisterendeOppgaveOpt,
                                           Optional<Behandling> eksisterendeBehandlingOpt, OppgaveGrunnlag oppgaveGrunnlag) {
        if (eksisterendeOppgaveOpt.isPresent()) {
            var eksisterendeOppgave = eksisterendeOppgaveOpt.get();
            if (harEndretEnhet(oppgaveGrunnlag, eksisterendeOppgave)) {
                return Optional.empty();
            }
            if (erReturFraBeslutter(nyOppgave, eksisterendeOppgave)) {
                return Optional.of(ReservasjonTjeneste.opprettReservasjon(nyOppgave, oppgaveGrunnlag.ansvarligSaksbehandlerIdent(),
                    ReservasjonKonstanter.RETUR_FRA_BESLUTTER));
            }
            if (eksisterendeOppgave.harAktivReservasjon()) {
                if (eksisterendeOppgave.harKriterie(AndreKriterierType.PAPIRSØKNAD) && !nyOppgave.harKriterie(AndreKriterierType.PAPIRSØKNAD)) {
                    return Optional.empty();
                }
                if (nyOppgave.harKriterie(AndreKriterierType.TIL_BESLUTTER)) {
                    return eksisterendeOppgave.harKriterie(AndreKriterierType.TIL_BESLUTTER) ? Optional.of(
                        nyReservasjon(nyOppgave, eksisterendeOppgave.getReservasjon())) : Optional.empty();
                }
                return Optional.of(nyReservasjon(nyOppgave, eksisterendeOppgave.getReservasjon()));
            }
            return Optional.empty();
        }
        if (oppgaveGrunnlag.ansvarligSaksbehandlerIdent() != null && (erNyManuellRevurdering(oppgaveGrunnlag, eksisterendeBehandlingOpt) || erPåVent(
            eksisterendeBehandlingOpt))) {
            return Optional.of(ReservasjonTjeneste.opprettReservasjon(nyOppgave, oppgaveGrunnlag.ansvarligSaksbehandlerIdent(), null));
        }
        return Optional.empty();
    }

    private static boolean erPåVent(Optional<Behandling> lagretBehandling) {
        return lagretBehandling.stream().anyMatch(behandling -> behandling.getBehandlingTilstand().erPåVent());
    }

    private static boolean erNyManuellRevurdering(OppgaveGrunnlag oppgaveGrunnlag, Optional<Behandling> lagretBehandling) {
        return lagretBehandling.isEmpty() && erManuellRevurdering(oppgaveGrunnlag);
    }

    private static boolean erManuellRevurdering(OppgaveGrunnlag oppgaveGrunnlag) {
        if (BehandlingType.TILBAKEBETALING_REVURDERING == oppgaveGrunnlag.behandlingstype()) {
            //Fptilbake behandlinger har ikke behandlingsårsaker, eneste måten å opprette en revurdering på er manuell saksbehandling
            return true;
        }
        return oppgaveGrunnlag.behandlingstype() == BehandlingType.REVURDERING && oppgaveGrunnlag.behandlingsårsaker()
            .contains(OppgaveGrunnlag.Behandlingsårsak.MANUELL);
    }

    private static boolean erReturFraBeslutter(Oppgave nyOppgave, Oppgave eksisterendeOppgave) {
        return eksisterendeOppgave.harKriterie(AndreKriterierType.TIL_BESLUTTER) && nyOppgave.harKriterie(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
    }

    private static boolean harEndretEnhet(OppgaveGrunnlag oppgaveGrunnlag, Oppgave eksisterendeOppgave) {
        return !eksisterendeOppgave.getBehandlendeEnhet().equals(oppgaveGrunnlag.behandlendeEnhetId());
    }

    private static Reservasjon nyReservasjon(Oppgave nyOppgave, Reservasjon eksisterendeReservasjon) {
        var reservasjon = new Reservasjon(nyOppgave);
        reservasjon.setReservertTil(eksisterendeReservasjon.getReservertTil());
        reservasjon.setReservertAv(eksisterendeReservasjon.getReservertAv());
        reservasjon.setFlyttetAv(eksisterendeReservasjon.getFlyttetAv());
        reservasjon.setBegrunnelse(eksisterendeReservasjon.getBegrunnelse());
        reservasjon.setFlyttetTidspunkt(eksisterendeReservasjon.getFlyttetTidspunkt());
        return reservasjon;
    }
}
