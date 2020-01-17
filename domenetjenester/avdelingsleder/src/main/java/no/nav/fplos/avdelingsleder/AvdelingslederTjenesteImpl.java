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
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;


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
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingsEnhet);
        return oppgaveRepository.hentAlleLister(avdeling.getId());
    }

    @Override
    public OppgaveFiltrering hentOppgaveFiltering(Long oppgaveFiltrering){
        return oppgaveRepository.hentListe(oppgaveFiltrering);
    }

    @Override
    public Long lagNyOppgaveFiltrering(String avdelingEnhet) {
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet);
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
        OppgaveFiltrering filtre = oppgaveRepository.hentListe(oppgavefiltreringId);
        if (checked) {//TODO: De utkommenterte linjene må tilbake når tilbakekreving er klart
            //if(behandlingType != BehandlingType.TILBAKEBETALING)
            sjekkSorteringForTilbakekreving(oppgavefiltreringId);
            oppgaveRepository.lagre(new FiltreringBehandlingType(filtre, behandlingType));
        } else {
            //if(behandlingType == BehandlingType.TILBAKEBETALING) sjekkSorteringForTilbakekreving(oppgavefiltreringId);
            oppgaveRepository.slettFiltreringBehandlingType(oppgavefiltreringId, behandlingType);
        }
        oppgaveRepository.refresh(filtre);
    }

    private void sjekkSorteringForTilbakekreving(Long oppgavefiltreringId) {
        KøSortering sortering = oppgaveRepository.hentSorteringForListe(oppgavefiltreringId);
        if(sortering != null && sortering.getFeltkategori() == KøSortering.FK_TILBAKEKREVING) {
            settSortering(oppgavefiltreringId, KøSortering.BEHANDLINGSFRIST);
        }
    }

    @Override
    public void endreFiltreringYtelseType(Long oppgavefiltreringId, FagsakYtelseType fagsakYtelseType) {
        OppgaveFiltrering filtre = oppgaveRepository.hentListe(oppgavefiltreringId);
        filtre.getFiltreringYtelseTyper()
                .forEach(ytelseType -> oppgaveRepository.slettFiltreringYtelseType(oppgavefiltreringId, ytelseType.getFagsakYtelseType()));
        if (fagsakYtelseType != null) {
            oppgaveRepository.lagre(new FiltreringYtelseType(filtre, fagsakYtelseType));
        }
        oppgaveRepository.refresh(filtre);
    }

    @Override
    public void endreFiltreringAndreKriterierType(Long oppgavefiltreringId, AndreKriterierType andreKriterierType, boolean checked, boolean inkluder) {
        OppgaveFiltrering filtre = oppgaveRepository.hentListe(oppgavefiltreringId);
        if (checked) {
            oppgaveRepository.slettFiltreringAndreKriterierType(oppgavefiltreringId, andreKriterierType);
            oppgaveRepository.lagre(new FiltreringAndreKriterierType(filtre, andreKriterierType, inkluder));
        } else {
            oppgaveRepository.slettFiltreringAndreKriterierType(oppgavefiltreringId, andreKriterierType);
        }
        oppgaveRepository.refresh(filtre);
    }

    @Override
    public void leggSaksbehandlerTilListe(Long oppgaveFiltreringId, String saksbehandlerIdent){
        OppgaveFiltrering oppgaveListe = oppgaveRepository.hentListe(oppgaveFiltreringId);
        if (oppgaveListe == null) {
            log.warn(String.format("Fant ikke oppgavefiltreringsliste basert på id %s, saksbehandler %s legges ikke til oppgavefiltrering", oppgaveFiltreringId, saksbehandlerIdent));
            return;
        }
        Saksbehandler saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        oppgaveListe.leggTilSaksbehandler(saksbehandler);
        oppgaveRepository.lagre(oppgaveListe);
        oppgaveRepository.refresh(saksbehandler);
    }

    @Override
    public void fjernSaksbehandlerFraListe(Long oppgaveFiltreringId, String saksbehandlerIdent){
        OppgaveFiltrering oppgaveListe = oppgaveRepository.hentListe(oppgaveFiltreringId);
        if (oppgaveListe == null) {
            log.warn(String.format("Fant ikke oppgavefiltreringsliste basert på id %s, saksbehandler %s fjernes ikke fra oppgavefiltrering", oppgaveFiltreringId, saksbehandlerIdent));
            return;
        }
        Saksbehandler saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        oppgaveListe.fjernSaksbehandler(saksbehandler);
        oppgaveRepository.lagre(oppgaveListe);
        oppgaveRepository.refresh(saksbehandler);
    }

    @Override
    public  List<Avdeling> hentAvdelinger(){
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
