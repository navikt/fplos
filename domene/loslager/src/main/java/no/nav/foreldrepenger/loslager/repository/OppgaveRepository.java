package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringBehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.oppgave.ReservasjonEventLogg;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OppgaveRepository {

    List<Oppgave> hentOppgaver(OppgavespørringDto oppgavespørringDto);

    int hentAntallOppgaver(OppgavespørringDto oppgavespørringDto);

    List<Reservasjon> hentReservasjonerTilknyttetAktiveOppgaver(String uid);

    List<Oppgave> hentOppgaverForSaksnummer(Long fagsakSaksnummer);

    List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe);

    Reservasjon hentReservasjon(Long oppgaveId);

    void reserverOppgaveFraTidligereReservasjon(Long oppgaveId, Reservasjon tidligereReservasjon);

    List<OppgaveFiltrering> hentAlleLister(Long avdelingsId);

    OppgaveFiltrering hentListe(Long listeId);

    void lagre(Reservasjon oppgave);

    void lagre(Oppgave oppgave);

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

    Oppgave opprettOppgave(Oppgave build);
    /**
     * @deprecated Bruk gjenåpneOppgaveForEksternId(Long) i stedet
     */
    @Deprecated
    Oppgave gjenåpneOppgave(Long behandlingId);

    Oppgave gjenåpneOppgaveForEksternId(UUID eksternId);

    /**
     * @deprecated Bruk avsluttOppgaveForEksternId(Long) i stedet
     */
    @Deprecated
    void avsluttOppgave(Long behandlingId);

    void avsluttOppgaveForEksternId(UUID eksternId);

    List<Oppgave> hentSisteReserverteOppgaver(String uid);

    void lagre(OppgaveEgenskap oppgaveEgenskap);

    void lagre(EventmottakFeillogg eventmottakFeillogg);

    /**
     * @deprecated Bruk hentEventerForEksternId(Long) i stedet
     */
    @Deprecated
    List<OppgaveEventLogg> hentEventer(Long behandlingId);

    List<OppgaveEventLogg> hentEventerForEksternId(UUID eksternId);

    List<OppgaveEgenskap> hentOppgaveEgenskaper(Long oppgaveId);

    void lagre(OppgaveEventLogg oppgaveEventLogg);

    void lagre(ReservasjonEventLogg oppgaveEventLogg);

    void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato);

    void settSorteringTidsintervallDager(Long oppgaveFiltreringId, Long fomDager, Long tomDager);

    void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode);
}
