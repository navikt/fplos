package no.nav.fplos.avdelingsleder;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;

public interface AvdelingslederTjeneste {
    List<OppgaveFiltrering> hentOppgaveFiltreringer(String avdelingsEnhet);

    OppgaveFiltrering hentOppgaveFiltering(Long oppgaveFiltrering);

    Long lagNyOppgaveFiltrering(String avdelingEnhet);

    void giListeNyttNavn(Long sakslisteId, String navn);

    void slettOppgaveFiltrering(Long listeId);

    void settSortering(Long sakslisteId, KøSortering sortering);

    void endreFiltreringBehandlingType(Long sakslisteId, BehandlingType behandlingType, boolean checked);

    void endreFiltreringYtelseType(Long sakslisteId, FagsakYtelseType behandlingType);

    void endreFiltreringAndreKriterierTypeType(Long sakslisteId, AndreKriterierType behandlingType, boolean checked, boolean inkluder);

    void leggSaksbehandlerTilOppgaveFiltrering(Long oppgaveFiltreringId, String saksbehandlerIdent);

    void fjernSaksbehandlerFraOppgaveFiltrering(Long oppgaveFiltreringId, String saksbehandlerIdent);

    List<Avdeling> hentAvdelinger();

    void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato);

    void settSorteringTidsintervallDager(Long oppgaveFiltreringId, Long fomDager, Long tomDager);

    void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode);
}
