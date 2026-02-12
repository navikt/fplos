package no.nav.foreldrepenger.los.avdelingsleder;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteLagreDto;


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

    public List<OppgaveFiltrering> hentOppgaveFiltreringer(String avdelingEnhet) {
        return oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetEnhet(avdelingEnhet);
    }

    public Optional<OppgaveFiltrering> hentOppgaveFiltering(Long oppgaveFiltrering) {
        return oppgaveRepository.hentOppgaveFilterSett(oppgaveFiltrering);
    }

    public Long lagNyOppgaveFiltrering(String avdelingEnhet) {
        var avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow();
        var nyOppgavefiltrering = new OppgaveFiltrering();
        nyOppgavefiltrering.setNavn("Ny liste");
        nyOppgavefiltrering.setAvdeling(avdeling);
        nyOppgavefiltrering.setSortering(KøSortering.BEHANDLINGSFRIST);
        var eksluderAndreKriterier = EnumSet.allOf(AndreKriterierType.class).stream()
            .filter(AndreKriterierType::isDefaultEkskludert)
            .collect(Collectors.toSet());
        nyOppgavefiltrering.setAndreKriterierTyper(Set.of(), eksluderAndreKriterier);
        return oppgaveRepository.lagreFiltrering(nyOppgavefiltrering);
    }

    public void slettOppgaveFiltrering(Long oppgavefiltreringId) {
        LOG.info("Sletter oppgavefilter {}", oppgavefiltreringId);
        oppgaveRepository.slettListe(oppgavefiltreringId);
    }

    public void endreEksistrendeOppgaveFilter(SakslisteLagreDto sakslisteLagre) {
        var oppgavefilter = oppgaveRepository.hentOppgaveFilterSett(sakslisteLagre.sakslisteId()).orElseThrow();
        oppgavefilter.setNavn(sakslisteLagre.navn());
        oppgavefilter.setSortering(sakslisteLagre.sortering().sorteringType());
        oppgavefilter.setFomDato(sakslisteLagre.sortering().fomDato());
        oppgavefilter.setTomDato(sakslisteLagre.sortering().tomDato());
        oppgavefilter.setFra(sakslisteLagre.sortering().fra());
        oppgavefilter.setTil(sakslisteLagre.sortering().til());
        oppgavefilter.setPeriodefilter(sakslisteLagre.sortering().periodefilter());
        oppgavefilter.setFiltreringBehandlingTyper(sakslisteLagre.behandlingTyper());
        oppgavefilter.setFiltreringYtelseTyper(sakslisteLagre.fagsakYtelseTyper());
        oppgavefilter.setAndreKriterierTyper(sakslisteLagre.andreKriterie().inkluder(), sakslisteLagre.andreKriterie().ekskluder());
        oppgaveRepository.lagre(oppgavefilter);
    }

    public void leggSaksbehandlerTilListe(Long oppgaveFiltreringId, String saksbehandlerIdent) {
        var saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent).orElseThrow();
        oppgaveRepository.hentOppgaveFilterSett(oppgaveFiltreringId).ifPresent(f -> {
            f.leggTilSaksbehandler(saksbehandler);
            oppgaveRepository.lagre(f);
        });
    }

    public void fjernSaksbehandlerFraListe(Long oppgaveFiltreringId, String saksbehandlerIdent) {
        var saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        oppgaveRepository.hentOppgaveFilterSett(oppgaveFiltreringId).ifPresent(f -> {
            f.fjernSaksbehandler(saksbehandler);
            oppgaveRepository.lagre(f);
        });
    }

    public List<Avdeling> hentAvdelinger() {
        return organisasjonRepository.hentAktiveAvdelinger();
    }

}
