package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.exception.IntegrasjonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<SaksbehandlerDto> hentAktiveSaksbehandlereTilknyttetSaksliste(Long sakslisteId) {
        var oppgaveFiltrering = avdelingslederTjeneste.hentOppgaveFiltering(sakslisteId);
        return oppgaveFiltrering.getSaksbehandlere().stream()
                .map(this::lagDto)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public Optional<SaksbehandlerMedAvdelingerDto> hentSaksbehandlerTilknyttetMinstEnKø(String ident) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(ident)
                .map(Saksbehandler::getSaksbehandlerIdent)
                .filter(sb -> !oppgaveTjeneste.hentAlleOppgaveFiltrering(sb).isEmpty())
                .flatMap(this::lagSaksbehandlerMedAvdelingerDto);
    }

    public Optional<SaksbehandlerMedAvdelingerDto> lagSaksbehandlerMedAvdelingerDto(String ident) {
        var saksbehandlerDto = lagDto(ident);
        if (!saksbehandlerDto.isEmpty()) {
            var avdelinger = ansattTjeneste.hentAvdelingerNavnForAnsatt(ident);
            return Optional.of(new SaksbehandlerMedAvdelingerDto(saksbehandlerDto.get(), avdelinger));
        }
        return Optional.empty();
    }

    public SaksbehandlerMedAvdelingerDto lagKjentOgUkjentSaksbehandlerMedAvdelingerDto(Saksbehandler saksbehandler) {
        // noe innfløkt løsning - saksbehandler kan eksistere i basen men være ukjent i ldap
        var ident = saksbehandler.getSaksbehandlerIdent();
        var saksbehandlerDto = lagDto(ident);
        if (saksbehandlerDto.isPresent()) {
            var avdelinger = ansattTjeneste.hentAvdelingerNavnForAnsatt(ident);
            return new SaksbehandlerMedAvdelingerDto(saksbehandlerDto.get(), avdelinger);
        }
        var ukjent = new SaksbehandlerDto(new SaksbehandlerBrukerIdentDto(ident), "Ukjent saksbehandler " + ident);
        return new SaksbehandlerMedAvdelingerDto(ukjent, Collections.emptyList());
    }

    public Optional<SaksbehandlerDto> lagDto(Saksbehandler saksbehandler) {
        var ident = saksbehandler.getSaksbehandlerIdent();
        return lagDto(ident);
    }

    private Optional<SaksbehandlerDto> lagDto(String ident) {
        var identDto = new SaksbehandlerBrukerIdentDto(ident);
        return hentSaksbehandlerNavn(ident)
                .map(navn -> new SaksbehandlerDto(identDto, navn));
    }

    public Optional<String> hentSaksbehandlerNavn(String ident) {
        try {
            return Optional.of(ansattTjeneste.hentAnsattNavn(ident));
        } catch (IntegrasjonException e) {
            LOG.info("Henting av ansattnavn feilet, fortsetter med empty.", e);
            return Optional.empty();
        }
    }
}
