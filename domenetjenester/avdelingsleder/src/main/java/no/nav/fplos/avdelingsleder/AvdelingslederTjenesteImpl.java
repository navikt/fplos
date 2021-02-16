package no.nav.fplos.avdelingsleder;

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
public class AvdelingslederTjenesteImpl implements AvdelingslederTjeneste {

    private OrganisasjonRepository organisasjonRepository;
    private OppgaveRepository oppgaveRepository;
    private static final Logger log = LoggerFactory.getLogger(AvdelingslederTjenesteImpl.class);


    AvdelingslederTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public AvdelingslederTjenesteImpl(OppgaveRepository oppgaveRepository, OrganisasjonRepository organisasjonRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.organisasjonRepository = organisasjonRepository;
    }

    @Override
    public List<OppgaveFiltrering> hentOppgaveFiltreringer(String avdelingsEnhet){
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingsEnhet).orElseThrow();
        return oppgaveRepository.hentAlleOppgaveFiltreringsettTilknyttetAvdeling(avdeling.getId());
    }

    @Override
    public Optional<OppgaveFiltrering> hentOppgaveFiltering(Long oppgaveFiltrering){
        return oppgaveRepository.hentFiltrering(oppgaveFiltrering);
    }

    @Override
    public Long lagNyOppgaveFiltrering(String avdelingEnhet) {
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow();
        return oppgaveRepository.lagre(OppgaveFiltrering.nyTomOppgaveFiltrering(avdeling));
    }

    @Override
    public void giListeNyttNavn(Long sakslisteId, String navn) {
        oppgaveRepository.oppdaterNavn(sakslisteId, navn);
    }

    @Override
    public void slettOppgaveFiltrering(Long oppgavefiltreringId) {
        log.info("Sletter oppgavefilter " + oppgavefiltreringId);
        oppgaveRepository.slettListe(oppgavefiltreringId);
    }

    @Override
    public void settSortering(Long sakslisteId, KøSortering sortering) {
        oppgaveRepository.settSortering(sakslisteId, sortering.getKode());
    }

    @Override
    public void endreFiltreringBehandlingType(Long oppgavefiltreringId, BehandlingType behandlingType, boolean checked) {
        OppgaveFiltrering filtre = oppgaveRepository.hentFiltrering(oppgavefiltreringId).orElseThrow();
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

    @Override
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

    private OppgaveFiltrering hentFiltrering(Long oppgavefiltreringId) {
        return oppgaveRepository.hentFiltrering(oppgavefiltreringId)
                .orElseThrow(() -> AvdelingslederTjenesteFeil.FACTORY.fantIkkeOppgavekø(oppgavefiltreringId).toException());
    }

    @Override
    public void endreFiltreringAndreKriterierType(Long oppgavefiltreringId, AndreKriterierType andreKriterierType, boolean checked, boolean inkluder) {
        var filtre = hentFiltrering(oppgavefiltreringId);
        if (checked) {
            oppgaveRepository.slettFiltreringAndreKriterierType(oppgavefiltreringId, andreKriterierType);
            oppgaveRepository.lagre(new FiltreringAndreKriterierType(filtre, andreKriterierType, inkluder));
        } else {
            oppgaveRepository.slettFiltreringAndreKriterierType(oppgavefiltreringId, andreKriterierType);
        }
        oppgaveRepository.refresh(filtre);
    }

    @Override
    public void leggSaksbehandlerTilListe(Long oppgaveFiltreringId, String saksbehandlerIdent) {
        var saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        oppgaveRepository.hentFiltrering(oppgaveFiltreringId)
                .ifPresent(f -> {
                    f.leggTilSaksbehandler(saksbehandler);
                    oppgaveRepository.lagre(f);
                });
        oppgaveRepository.refresh(saksbehandler);
    }

    @Override
    public void fjernSaksbehandlerFraListe(Long oppgaveFiltreringId, String saksbehandlerIdent) {
        var saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        oppgaveRepository.hentFiltrering(oppgaveFiltreringId)
                .ifPresent(f -> {
                    f.fjernSaksbehandler(saksbehandler);
                    oppgaveRepository.lagre(f);
                });
        oppgaveRepository.refresh(saksbehandler);
    }

    @Override
    public List<Avdeling> hentAvdelinger(){
        return organisasjonRepository.hentAvdelinger();
    }

    @Override
    public void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato){
        oppgaveRepository.settSorteringTidsintervallDato(oppgaveFiltreringId, fomDato, tomDato);
    }

    @Override
    public void settSorteringNumeriskIntervall(Long oppgaveFiltreringId, Long fra, Long til){
        oppgaveRepository.settSorteringNumeriskIntervall(oppgaveFiltreringId, fra, til);
    }

    @Override
    public void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode){
        oppgaveRepository.settSorteringTidsintervallValg(oppgaveFiltreringId, erDynamiskPeriode);
    }

}
