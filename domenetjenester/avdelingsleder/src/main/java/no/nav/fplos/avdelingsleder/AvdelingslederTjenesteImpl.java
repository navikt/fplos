package no.nav.fplos.avdelingsleder;

import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;


@ApplicationScoped
public class AvdelingslederTjenesteImpl implements AvdelingslederTjeneste {

    private OrganisasjonRepository organisasjonRepository;
    private OppgaveRepository oppgaveRepository;

    AvdelingslederTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public AvdelingslederTjenesteImpl(OppgaveRepositoryProvider oppgaveRepositoryProvider) {
        oppgaveRepository = oppgaveRepositoryProvider.getOppgaveRepository();
        organisasjonRepository = oppgaveRepositoryProvider.getOrganisasjonRepository();
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
        oppgaveRepository.slettListe(oppgavefiltreringId);
    }

    @Override
    public void settSortering(Long sakslisteId, KøSortering sortering) {
        oppgaveRepository.settSortering(sakslisteId, sortering.getKode());
    }

    @Override
    public void endreFiltreringBehandlingType(Long oppgavefiltreringId, BehandlingType behandlingType, boolean checked) {
        OppgaveFiltrering oppgaveFiltrering = oppgaveRepository.hentListe(oppgavefiltreringId);
        if (checked){
            oppgaveRepository.lagre(new FiltreringBehandlingType(oppgaveFiltrering, behandlingType));
        }else{
            oppgaveRepository.slettFiltreringBehandlingType(oppgavefiltreringId,behandlingType);
        }
        oppgaveRepository.refresh(oppgaveFiltrering);
    }

    @Override
    public void endreFiltreringYtelseType(Long oppgavefiltreringId, FagsakYtelseType fagsakYtelseType) {
        OppgaveFiltrering oppgaveFiltrering = oppgaveRepository.hentListe(oppgavefiltreringId);
        oppgaveFiltrering.getFiltreringYtelseTyper().forEach(ytelseType -> oppgaveRepository.slettFiltreringYtelseType(oppgavefiltreringId, ytelseType.getFagsakYtelseType()));
        if (fagsakYtelseType != null){
            oppgaveRepository.lagre(new FiltreringYtelseType(oppgaveFiltrering, fagsakYtelseType));
        }
        oppgaveRepository.refresh(oppgaveFiltrering);
    }

    @Override
    public void endreFiltreringAndreKriterierTypeType(Long oppgavefiltreringId, AndreKriterierType andreKriterierType, boolean checked, boolean inkluder) {
        OppgaveFiltrering oppgaveFiltrering = oppgaveRepository.hentListe(oppgavefiltreringId);
        if (checked){
            oppgaveRepository.slettFiltreringAndreKriterierType(oppgavefiltreringId, andreKriterierType);
            oppgaveRepository.lagre(new FiltreringAndreKriterierType(oppgaveFiltrering, andreKriterierType, inkluder));
        }else{
            oppgaveRepository.slettFiltreringAndreKriterierType(oppgavefiltreringId, andreKriterierType);
        }
        oppgaveRepository.refresh(oppgaveFiltrering);
    }

    @Override
    public void leggSaksbehandlerTilOppgaveFiltrering(Long oppgaveFiltreringId, String saksbehandlerIdent){
        OppgaveFiltrering oppgaveFiltrering = oppgaveRepository.hentListe(oppgaveFiltreringId);
        Saksbehandler saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        oppgaveFiltrering.leggTilSaksbehandler(saksbehandler);
        oppgaveRepository.lagre(oppgaveFiltrering);
        oppgaveRepository.refresh(saksbehandler);
    }

    @Override
    public void fjernSaksbehandlerFraOppgaveFiltrering(Long oppgaveFiltreringId, String saksbehandlerIdent){
        OppgaveFiltrering oppgaveFiltrering = oppgaveRepository.hentListe(oppgaveFiltreringId);
        Saksbehandler saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        oppgaveFiltrering.fjernSaksbehandler(saksbehandler);
        oppgaveRepository.lagre(oppgaveFiltrering);
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
    public void settSorteringTidsintervallDager(Long oppgaveFiltreringId, Long fomDager, Long tomDager){
        oppgaveRepository.settSorteringTidsintervallDager(oppgaveFiltreringId, fomDager, tomDager);
    }

    @Override
    public void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode){
        oppgaveRepository.settSorteringTidsintervallValg(oppgaveFiltreringId, erDynamiskPeriode);
    }

}
