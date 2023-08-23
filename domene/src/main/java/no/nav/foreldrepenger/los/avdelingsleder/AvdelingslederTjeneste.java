package no.nav.foreldrepenger.los.avdelingsleder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringBehandlingType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;


@ApplicationScoped
public class AvdelingslederTjeneste {

    private OrganisasjonRepository organisasjonRepository;
    private OppgaveRepository oppgaveRepository;
    private static final Logger LOG = LoggerFactory.getLogger(AvdelingslederTjeneste.class);


    AvdelingslederTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AvdelingslederTjeneste(OppgaveRepository oppgaveRepository, OrganisasjonRepository organisasjonRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.organisasjonRepository = organisasjonRepository;
    }

    public List<OppgaveFiltrering> hentOppgaveFiltreringer(String avdelingsEnhet) {
        var avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingsEnhet).orElseThrow();
        return oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetAvdeling(avdeling.getId());
    }

    public Optional<OppgaveFiltrering> hentOppgaveFiltering(Long oppgaveFiltrering) {
        return oppgaveRepository.hentOppgaveFilterSett(oppgaveFiltrering);
    }

    public Long lagNyOppgaveFiltrering(String avdelingEnhet) {
        var avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow();
        return oppgaveRepository.lagreFiltrering(OppgaveFiltrering.nyTomOppgaveFiltrering(avdeling));
    }

    public void giListeNyttNavn(Long sakslisteId, String navn) {
        oppgaveRepository.oppdaterNavn(sakslisteId, navn);
    }

    public void slettOppgaveFiltrering(Long oppgavefiltreringId) {
        LOG.info("Sletter oppgavefilter {}", oppgavefiltreringId);
        oppgaveRepository.slettListe(oppgavefiltreringId);
    }

    public void settSortering(Long sakslisteId, KøSortering sortering) {
        oppgaveRepository.settSortering(sakslisteId, sortering.getKode());
    }

    public void endreFiltreringBehandlingType(Long oppgavefiltreringId, BehandlingType behandlingType, boolean checked) {
        var filtre = oppgaveRepository.hentOppgaveFilterSett(oppgavefiltreringId).orElseThrow();
        if (checked) {
            if (!behandlingType.gjelderTilbakebetaling()) {
                settStandardSorteringHvisTidligereTilbakebetaling(oppgavefiltreringId);
            }
            oppgaveRepository.lagre(new FiltreringBehandlingType(filtre, behandlingType));
        } else {
            oppgaveRepository.slettFiltreringBehandlingType(oppgavefiltreringId, behandlingType);
            if (ingenBehandlingsTypeErValgtEtterAtTilbakekrevingErValgtBort(behandlingType, oppgavefiltreringId)) {
                settStandardSorteringHvisTidligereTilbakebetaling(oppgavefiltreringId);
            }
        }
        oppgaveRepository.refresh(filtre);
    }

    public void endreFiltreringYtelseType(Long oppgavefiltreringId, FagsakYtelseType fagsakYtelseType) {
        var filter = hentFiltrering(oppgavefiltreringId);
        // fjern gamle filtre
        filter.getFiltreringYtelseTyper()
            .stream()
            .map(FiltreringYtelseType::getFagsakYtelseType)
            .forEach(yt -> oppgaveRepository.slettFiltreringYtelseType(oppgavefiltreringId, yt));
        if (fagsakYtelseType != null) {
            // legg på eventuelle nye
            oppgaveRepository.lagre(new FiltreringYtelseType(filter, fagsakYtelseType));
        }
        oppgaveRepository.refresh(filter);
    }

    public void endreFyt(Long oppgavefiltreringId, FagsakYtelseType fagsakYtelseType, boolean checked) {
        var filter = hentFiltrering(oppgavefiltreringId);
        filter.getFiltreringYtelseTyper()
            .stream()
            .filter(fyt -> fyt.getFagsakYtelseType().equals(fagsakYtelseType))
            .findFirst()
            .ifPresent(fyt -> oppgaveRepository.slettFiltreringYtelseType(oppgavefiltreringId, fagsakYtelseType));
        if (checked) {
            oppgaveRepository.lagre(new FiltreringYtelseType(filter, fagsakYtelseType));
        }
        oppgaveRepository.refresh(filter);
    }

    public void endreFiltreringYtelseTyper(Long oppgavefiltreringId, List<FagsakYtelseType> fagsakYtelseType) {
        LOG.info("Henter oppgavefiltreringId {}", oppgavefiltreringId);
        var filter = hentFiltrering(oppgavefiltreringId);
        filter.getFiltreringYtelseTyper()
            .stream()
            .map(FiltreringYtelseType::getFagsakYtelseType)
            .forEach(yt -> oppgaveRepository.slettFiltreringYtelseType(oppgavefiltreringId, yt));
        fagsakYtelseType.stream().map(yt -> new FiltreringYtelseType(filter, yt)).forEach(oppgaveRepository::lagre);
    }

    public void endreFiltreringAndreKriterierType(Long oppgavefiltreringId,
                                                  AndreKriterierType andreKriterierType,
                                                  boolean checked,
                                                  boolean inkluder) {
        oppgaveRepository.slettFiltreringAndreKriterierType(oppgavefiltreringId, andreKriterierType);
        var filterSett = hentFiltrering(oppgavefiltreringId);
        if (checked) {
            oppgaveRepository.lagre(new FiltreringAndreKriterierType(filterSett, andreKriterierType, inkluder));
        }
        oppgaveRepository.refresh(filterSett);
    }

    public void leggSaksbehandlerTilListe(Long oppgaveFiltreringId, String saksbehandlerIdent) {
        var saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent).orElseThrow();
        oppgaveRepository.hentOppgaveFilterSett(oppgaveFiltreringId).ifPresent(f -> {
            f.leggTilSaksbehandler(saksbehandler);
            oppgaveRepository.lagre(f);
        });
        oppgaveRepository.refresh(saksbehandler);
    }

    public void fjernSaksbehandlerFraListe(Long oppgaveFiltreringId, String saksbehandlerIdent) {
        var saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        oppgaveRepository.hentOppgaveFilterSett(oppgaveFiltreringId).ifPresent(f -> {
            f.fjernSaksbehandler(saksbehandler);
            oppgaveRepository.lagre(f);
        });
        oppgaveRepository.refresh(saksbehandler);
    }

    public List<Avdeling> hentAvdelinger() {
        return organisasjonRepository.hentAvdelinger();
    }

    public void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato) {
        oppgaveRepository.settSorteringTidsintervallDato(oppgaveFiltreringId, fomDato, tomDato);
    }

    public void settSorteringNumeriskIntervall(Long oppgaveFiltreringId, Long fra, Long til) {
        oppgaveRepository.settSorteringNumeriskIntervall(oppgaveFiltreringId, fra, til);
    }

    public void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode) {
        oppgaveRepository.settSorteringTidsintervallValg(oppgaveFiltreringId, erDynamiskPeriode);
    }

    private boolean ingenBehandlingsTypeErValgtEtterAtTilbakekrevingErValgtBort(BehandlingType behandlingType, Long oppgavefiltreringId) {
        if (!behandlingType.gjelderTilbakebetaling()) {
            return false;
        }
        var filter = hentFiltrering(oppgavefiltreringId);
        return filter.getFiltreringBehandlingTyper().isEmpty();
    }

    private void settStandardSorteringHvisTidligereTilbakebetaling(Long oppgavefiltreringId) {
        var sortering = oppgaveRepository.hentSorteringForListe(oppgavefiltreringId);
        if (sortering != null && sortering.getFeltkategori().equals(KøSortering.FK_TILBAKEKREVING)) {
            settSortering(oppgavefiltreringId, KøSortering.BEHANDLINGSFRIST);
        }
    }

    private OppgaveFiltrering hentFiltrering(Long oppgavefiltreringId) {
        return oppgaveRepository.hentOppgaveFilterSett(oppgavefiltreringId)
            .orElseThrow(() -> AvdelingslederTjenesteFeil.fantIkkeOppgavekø(oppgavefiltreringId));
    }

}
