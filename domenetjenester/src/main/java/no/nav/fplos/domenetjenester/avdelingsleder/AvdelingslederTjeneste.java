package no.nav.fplos.domenetjenester.avdelingsleder;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringBehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class AvdelingslederTjeneste {

    private OrganisasjonRepository organisasjonRepository;
    private OppgaveRepository oppgaveRepository;
    private static final Logger log = LoggerFactory.getLogger(AvdelingslederTjeneste.class);


    AvdelingslederTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AvdelingslederTjeneste(OppgaveRepository oppgaveRepository, OrganisasjonRepository organisasjonRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.organisasjonRepository = organisasjonRepository;
    }

    public List<OppgaveFiltrering> hentOppgaveFiltreringer(String avdelingsEnhet){
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingsEnhet).orElseThrow();
        return oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetAvdeling(avdeling.getId());
    }

    public Optional<OppgaveFiltrering> hentOppgaveFiltering(Long oppgaveFiltrering){
        return oppgaveRepository.hentOppgaveFilterSett(oppgaveFiltrering);
    }

    public Long lagNyOppgaveFiltrering(String avdelingEnhet) {
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow();
        return oppgaveRepository.lagre(OppgaveFiltrering.nyTomOppgaveFiltrering(avdeling));
    }

    public void giListeNyttNavn(Long sakslisteId, String navn) {
        oppgaveRepository.oppdaterNavn(sakslisteId, navn);
    }

    public void slettOppgaveFiltrering(Long oppgavefiltreringId) {
        log.info("Sletter oppgavefilter " + oppgavefiltreringId);
        oppgaveRepository.slettListe(oppgavefiltreringId);
    }

    public void settSortering(Long sakslisteId, KøSortering sortering) {
        oppgaveRepository.settSortering(sakslisteId, sortering.getKode());
    }

    public void endreFiltreringBehandlingType(Long oppgavefiltreringId, BehandlingType behandlingType, boolean checked) {
        OppgaveFiltrering filtre = oppgaveRepository.hentOppgaveFilterSett(oppgavefiltreringId).orElseThrow();
        if (checked) {
            if (!behandlingType.gjelderTilbakebetaling()) {
                settStandardSorteringHvisTidligereTilbakebetaling(oppgavefiltreringId);
            }
            oppgaveRepository.lagre(new FiltreringBehandlingType(filtre, behandlingType));
        } else {
            oppgaveRepository.slettFiltreringBehandlingType(oppgavefiltreringId, behandlingType);
            if (ingenBehandlingsTypeErValgtEtterAtTilbakekrevingErValgtBort(behandlingType, oppgavefiltreringId))
                settStandardSorteringHvisTidligereTilbakebetaling(oppgavefiltreringId);
        }
        oppgaveRepository.refresh(filtre);
    }

    public void endreFiltreringYtelseType(Long oppgavefiltreringId, FagsakYtelseType fagsakYtelseType) {
        var filter = hentFiltrering(oppgavefiltreringId);
        // fjern gamle filtre
        filter.getFiltreringYtelseTyper().stream()
                .map(FiltreringYtelseType::getFagsakYtelseType)
                .forEach(yt -> {
                    oppgaveRepository.slettFiltreringYtelseType(oppgavefiltreringId, yt);});
        if (fagsakYtelseType != null) {
            // legg på eventuelle nye
            oppgaveRepository.lagre(new FiltreringYtelseType(filter, fagsakYtelseType));
        }
        oppgaveRepository.refresh(filter);
    }

    public void endreFiltreringAndreKriterierType(Long oppgavefiltreringId, AndreKriterierType andreKriterierType, boolean checked, boolean inkluder) {
        oppgaveRepository.slettFiltreringAndreKriterierType(oppgavefiltreringId, andreKriterierType);
        var filterSett = hentFiltrering(oppgavefiltreringId);
        if (checked) {
            oppgaveRepository.lagre(new FiltreringAndreKriterierType(filterSett, andreKriterierType, inkluder));
        }
        oppgaveRepository.refresh(filterSett);
    }

    public void leggSaksbehandlerTilListe(Long oppgaveFiltreringId, String saksbehandlerIdent) {
        var saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent)
                .orElseThrow();
        oppgaveRepository.hentOppgaveFilterSett(oppgaveFiltreringId)
                .ifPresent(f -> {
                    f.leggTilSaksbehandler(saksbehandler);
                    oppgaveRepository.lagre(f);
                });
        oppgaveRepository.refresh(saksbehandler);
    }

    public void fjernSaksbehandlerFraListe(Long oppgaveFiltreringId, String saksbehandlerIdent) {
        var saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        oppgaveRepository.hentOppgaveFilterSett(oppgaveFiltreringId)
                .ifPresent(f -> {
                    f.fjernSaksbehandler(saksbehandler);
                    oppgaveRepository.lagre(f);
                });
        oppgaveRepository.refresh(saksbehandler);
    }

    public List<Avdeling> hentAvdelinger(){
        return organisasjonRepository.hentAvdelinger();
    }

    public void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato){
        oppgaveRepository.settSorteringTidsintervallDato(oppgaveFiltreringId, fomDato, tomDato);
    }

    public void settSorteringNumeriskIntervall(Long oppgaveFiltreringId, Long fra, Long til){
        oppgaveRepository.settSorteringNumeriskIntervall(oppgaveFiltreringId, fra, til);
    }

    public void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode){
        oppgaveRepository.settSorteringTidsintervallValg(oppgaveFiltreringId, erDynamiskPeriode);
    }

    private boolean ingenBehandlingsTypeErValgtEtterAtTilbakekrevingErValgtBort(BehandlingType behandlingType, Long oppgavefiltreringId) {
        if (!behandlingType.gjelderTilbakebetaling()) return false;
        var filter = hentFiltrering(oppgavefiltreringId);
        return filter.getFiltreringBehandlingTyper().isEmpty();
    }

    private void settStandardSorteringHvisTidligereTilbakebetaling(Long oppgavefiltreringId) {
        KøSortering sortering = oppgaveRepository.hentSorteringForListe(oppgavefiltreringId);
        if (sortering != null && sortering.getFeltkategori().equals(KøSortering.FK_TILBAKEKREVING)) {
            settSortering(oppgavefiltreringId, KøSortering.BEHANDLINGSFRIST);
        }
    }

    private OppgaveFiltrering hentFiltrering(Long oppgavefiltreringId) {
        return oppgaveRepository.hentOppgaveFilterSett(oppgavefiltreringId)
                .orElseThrow(() -> AvdelingslederTjenesteFeil.fantIkkeOppgavekø(oppgavefiltreringId));
    }

}
