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
                .map(s -> lagDto(s))
                .collect(Collectors.toList());
    }

    public Optional<SaksbehandlerMedAvdelingerDto> hentSaksbehandlerTilknyttetMinstEnKø(String ident) {
        var saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(ident);
        if (saksbehandler.isEmpty()) {
            return Optional.empty();
        }

        if (oppgaveTjeneste.hentAlleOppgaveFiltrering(ident).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(lagSaksbehandlerMedAvdelingerDto(ident));
    }

    public SaksbehandlerMedAvdelingerDto lagSaksbehandlerMedAvdelingerDto(String ident) {
        SaksbehandlerDto saksbehandlerDto = lagDto(ident);
        var avdelinger = ansattTjeneste.hentAvdelingerNavnForAnsatt(ident);
        return new SaksbehandlerMedAvdelingerDto(saksbehandlerDto, avdelinger);
    }

    public SaksbehandlerMedAvdelingerDto lagDtoMedAvdelinger(Saksbehandler saksbehandler) {
        var ident = saksbehandler.getSaksbehandlerIdent();
        return lagSaksbehandlerMedAvdelingerDto(ident);
    }

    public SaksbehandlerDto lagDto(Saksbehandler saksbehandler) {
        var ident = saksbehandler.getSaksbehandlerIdent();
        return lagDto(ident);
    }

    private SaksbehandlerDto lagDto(String ident) {
        var identDto = new SaksbehandlerBrukerIdentDto(ident);
        var navn = hentSaksbehandlerNavn(ident);
        return new SaksbehandlerDto(identDto, navn);
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
