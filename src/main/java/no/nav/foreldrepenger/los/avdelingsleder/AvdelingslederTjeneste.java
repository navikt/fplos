package no.nav.foreldrepenger.los.avdelingsleder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.function.Predicate.not;


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
            filtre.leggTilFilter(behandlingType);
        } else {
            filtre.fjernFilter(behandlingType);
        }
        boolean gammelSorteringGjelderTilbakebetaling = filtre.getSortering().getFeltkategori().equals(KøSortering.FK_TILBAKEKREVING);
        boolean tilbakekrevingSorteringIkkeAktuell =
            filtre.getBehandlingTyper().isEmpty() || filtre.getBehandlingTyper().stream().anyMatch(not(BehandlingType::gjelderTilbakebetaling));
        if (gammelSorteringGjelderTilbakebetaling && tilbakekrevingSorteringIkkeAktuell) {
            settSortering(filtre.getId(), KøSortering.BEHANDLINGSFRIST);
        }
        oppgaveRepository.lagre(filtre);
    }

    public void endreFagsakYtelseType(Long oppgavefiltreringId, FagsakYtelseType fagsakYtelseType, boolean checked) {
        var filter = hentFiltrering(oppgavefiltreringId);
        filter.fjernFilter(fagsakYtelseType);
        if (checked) {
            filter.leggTilFilter(fagsakYtelseType);
        }
        oppgaveRepository.lagre(filter);
    }

    public void endreFiltreringAndreKriterierType(Long oppgavefiltreringId,
                                                  AndreKriterierType andreKriterierType,
                                                  boolean checked,
                                                  boolean inkluder) {
        var filterSett = hentFiltrering(oppgavefiltreringId);
        filterSett.fjernFilter(andreKriterierType);
        if (checked) {
            var kriterie = new FiltreringAndreKriterierType(filterSett, andreKriterierType, inkluder);
            filterSett.leggTilFilter(kriterie);
        }
        oppgaveRepository.lagre(filterSett);
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
        return organisasjonRepository.hentAktiveAvdelinger();
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

    private OppgaveFiltrering hentFiltrering(Long oppgavefiltreringId) {
        return oppgaveRepository.hentOppgaveFilterSett(oppgavefiltreringId)
            .orElseThrow(() -> AvdelingslederTjenesteFeil.fantIkkeOppgavekø(oppgavefiltreringId));
    }

}
