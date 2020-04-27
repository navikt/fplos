package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.exception.IntegrasjonException;

@ApplicationScoped
public class SaksbehandlerDtoTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(SaksbehandlerDtoTjeneste.class);

    private OrganisasjonRepository organisasjonRepository;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private AnsattTjeneste ansattTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;

    @Inject
    public SaksbehandlerDtoTjeneste(OrganisasjonRepository organisasjonRepository,
                                    AvdelingslederTjeneste avdelingslederTjeneste,
                                    AnsattTjeneste ansattTjeneste,
                                    OppgaveTjeneste oppgaveTjeneste) {
        this.organisasjonRepository = organisasjonRepository;
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.ansattTjeneste = ansattTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    SaksbehandlerDtoTjeneste() {
        //CDI
    }

    public List<SaksbehandlerDto> hentSaksbehandlereForSaksliste(Long sakslisteId) {
        var oppgaveFiltrering = avdelingslederTjeneste.hentOppgaveFiltering(sakslisteId);
        return oppgaveFiltrering.getSaksbehandlere()
                .stream()
                .map(s -> map(s))
                .collect(Collectors.toList());
    }

    public Optional<SaksbehandlerDto> hentSaksbehandlerTilknyttetMinstEnKø(String ident) {
        List<Saksbehandler> saksbehandlere = organisasjonRepository.hentAlleSaksbehandlere();
        if (saksbehandlere.stream().noneMatch(saksbehandler -> saksbehandler.getSaksbehandlerIdent().equals(ident))) {
            return Optional.empty();
        }

        if (oppgaveTjeneste.hentAlleOppgaveFiltrering(ident).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(lagSaksbehandlerDto(ident));
    }

    public SaksbehandlerDto lagSaksbehandlerDto(String ident) {
        var identDto = new SaksbehandlerBrukerIdentDto(ident);
        var navn = hentSaksbehandlerNavn(ident);
        var avdelinger = ansattTjeneste.hentAvdelingerNavnForAnsatt(ident);
        return new SaksbehandlerDto(identDto, navn, avdelinger);
    }

    public SaksbehandlerDto map(Saksbehandler saksbehandler) {
        var ident = saksbehandler.getSaksbehandlerIdent();
        return lagSaksbehandlerDto(ident);
    }

    public String hentSaksbehandlerNavn(String ident) {
        try {
            return ansattTjeneste.hentAnsattNavn(ident);
        } catch (IntegrasjonException e) {
            LOG.info("Henting av ansattnavn feilet, fortsetter med ukjent navn.", e);
            return "Ukjent ansatt";
        }
    }
}
