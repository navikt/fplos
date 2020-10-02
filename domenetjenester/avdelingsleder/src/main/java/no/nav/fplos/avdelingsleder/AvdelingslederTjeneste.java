package no.nav.fplos.avdelingsleder;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvdelingslederTjeneste {
    List<OppgaveFiltrering> hentOppgaveFiltreringer(String avdelingsEnhet);

    Optional<OppgaveFiltrering> hentOppgaveFiltering(Long oppgaveFiltrering);

    Long lagNyOppgaveFiltrering(String avdelingEnhet);

    void giListeNyttNavn(Long sakslisteId, String navn);

    void slettOppgaveFiltrering(Long listeId);

    void settSortering(Long sakslisteId, KøSortering sortering);

    void endreFiltreringBehandlingType(Long sakslisteId, BehandlingType behandlingType, boolean checked);

    void endreFiltreringYtelseType(Long sakslisteId, FagsakYtelseType behandlingType);

    void endreFiltreringAndreKriterierType(Long sakslisteId, AndreKriterierType behandlingType, boolean checked, boolean inkluder);

    void leggSaksbehandlerTilListe(Long oppgaveFiltreringId, String saksbehandlerIdent);

    void fjernSaksbehandlerFraListe(Long oppgaveFiltreringId, String saksbehandlerIdent);

    List<Avdeling> hentAvdelinger();

    void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato);

    void settSorteringNumeriskIntervall(Long oppgaveFiltreringId, Long fra, Long til);

    void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode);
}
