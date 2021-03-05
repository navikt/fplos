package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringBehandlingType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonEventLogg;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;

public interface OppgaveRepository {

    List<Oppgave> hentOppgaver(Oppgavespørring oppgavespørring);

    List<Oppgave> hentOppgaver(Oppgavespørring oppgavespørring, int maksAntall);

    int hentAntallOppgaver(Oppgavespørring oppgavespørring);

    int hentAntallOppgaverForAvdeling(Long avdelingsId);

    List<Reservasjon> hentReservasjonerTilknyttetAktiveOppgaver(String uid);

    List<Reservasjon> hentAlleReservasjonerForAvdeling(String avdelingEnhet);

    List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe);

    Reservasjon hentReservasjon(Long oppgaveId);

    List<OppgaveFiltrering> hentAlleOppgaveFilterSettTilknyttetAvdeling(Long avdelingsId);

    Optional<OppgaveFiltrering> hentOppgaveFilterSett(Long listeId);

    KøSortering hentSorteringForListe(Long listeId);

    void lagre(Reservasjon oppgave);

    void lagre(Oppgave oppgave);

    void lagre(TilbakekrevingOppgave egenskaper);

    Long lagre(OppgaveFiltrering oppgaveFiltrering);

    void oppdaterNavn(Long sakslisteId, String navn);

    void slettListe(Long listeId);

    void settSortering(Long sakslisteId, String sortering);

    void lagre(FiltreringBehandlingType filtreringBehandlingType);

    void lagre(FiltreringYtelseType filtreringYtelseType);

    void lagre(FiltreringAndreKriterierType filtreringAndreKriterierType);

    void slettFiltreringBehandlingType(Long sakslisteId, BehandlingType behandlingType);

    void slettFiltreringYtelseType(Long sakslisteId, FagsakYtelseType behandlingType);

    void slettFiltreringAndreKriterierType(Long oppgavefiltreringId, AndreKriterierType andreKriterierType);

    void refresh(Oppgave oppgave);

    void refresh(OppgaveFiltrering oppgaveFiltrering);

    void refresh(Avdeling avdeling);

    void refresh(Saksbehandler saksbehandler);

    List<Oppgave> sjekkOmOppgaverFortsattErTilgjengelige(List<Long> oppgaveIder);

    Oppgave opprettOppgave(Oppgave oppgave);

    TilbakekrevingOppgave opprettTilbakekrevingOppgave(TilbakekrevingOppgave egenskaper);

    TilbakekrevingOppgave gjenåpneTilbakekrevingOppgave(BehandlingId behandlingId);

    Optional<TilbakekrevingOppgave> hentAktivTilbakekrevingOppgave(BehandlingId behandlingId);

    Oppgave gjenåpneOppgaveForBehandling(BehandlingId behandlingId);

    void avsluttOppgaveForBehandling(BehandlingId behandlingId);

    List<Oppgave> hentSisteReserverteOppgaver(String uid);

    void lagre(OppgaveEgenskap oppgaveEgenskap);

    List<OppgaveEventLogg> hentOppgaveEventer(BehandlingId behandlingId);

    List<OppgaveEgenskap> hentOppgaveEgenskaper(Long oppgaveId);

    void lagre(OppgaveEventLogg oppgaveEventLogg);

    void lagre(ReservasjonEventLogg oppgaveEventLogg);

    void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato);

    void settSorteringNumeriskIntervall(Long oppgaveFiltreringId, Long fra, Long til);

    void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode);

    List<Oppgave> hentOppgaverForSynkronisering();

    Oppgave hentOppgave(Long oppgaveId);

    List<Oppgave> hentOppgaver(BehandlingId behandlingId);

}