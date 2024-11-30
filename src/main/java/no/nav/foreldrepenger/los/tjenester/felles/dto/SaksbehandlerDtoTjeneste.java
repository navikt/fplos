package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjenesteFeil;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.organisasjon.ansatt.BrukerProfil;
import no.nav.vedtak.exception.IntegrasjonException;

@ApplicationScoped
public class SaksbehandlerDtoTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(SaksbehandlerDtoTjeneste.class);

    private OrganisasjonRepository organisasjonRepository;
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private AnsattTjeneste ansattTjeneste;

    @Inject
    public SaksbehandlerDtoTjeneste(OrganisasjonRepository organisasjonRepository,
                                    AvdelingslederTjeneste avdelingslederTjeneste,
                                    AnsattTjeneste ansattTjeneste,
                                    OppgaveKøTjeneste oppgaveKøTjeneste) {
        this.organisasjonRepository = organisasjonRepository;
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.ansattTjeneste = ansattTjeneste;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
    }

    SaksbehandlerDtoTjeneste() {
        //CDI
    }

    public List<SaksbehandlerDto> hentAktiveSaksbehandlereTilknyttetSaksliste(Long sakslisteId) {
        var filtrering = avdelingslederTjeneste.hentOppgaveFiltering(sakslisteId)
            .orElseThrow(() -> AvdelingslederTjenesteFeil.fantIkkeOppgavekø(sakslisteId));
        return filtrering.getSaksbehandlere().stream().map(this::saksbehandlerDto).flatMap(Optional::stream).toList();
    }

    public Optional<SaksbehandlerDto> hentSaksbehandlerTilknyttetMinstEnKø(String ident) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(ident)
            .filter(sb -> !oppgaveKøTjeneste.hentAlleOppgaveFiltrering(sb.getSaksbehandlerIdent()).isEmpty())
            .flatMap(this::saksbehandlerDto);
    }

    public SaksbehandlerDto lagKjentOgUkjentSaksbehandler(Saksbehandler saksbehandler) {
        // saksbehandler kan eksistere i basen men være ukjent i azuread
        var ident = saksbehandler.getSaksbehandlerIdent();
        var saksbehandlerDto = saksbehandlerDto(saksbehandler);
        return saksbehandlerDto.orElseGet(() -> SaksbehandlerDto.ukjentSaksbehandler(ident));
    }

    public Optional<SaksbehandlerDto> saksbehandlerDto(Saksbehandler saksbehandler) {
        var identDto = new SaksbehandlerBrukerIdentDto(saksbehandler.getSaksbehandlerIdent());
        return hentBrukerProfil(saksbehandler)
            .map(bp -> new SaksbehandlerDto(identDto, bp.navn(), bp.ansattAvdeling()));
    }

    // Denne skal hente Brukerprofil dersom saksbehandler ikke er lagret
    public Optional<SaksbehandlerDto> saksbehandlerDtoForNavIdent(String saksbehandlerIdent) {
        try {
            var identDto = new SaksbehandlerBrukerIdentDto(saksbehandlerIdent);
            return organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent).flatMap(this::hentBrukerProfil)
                .or(() -> Optional.ofNullable(ansattTjeneste.hentBrukerProfil(saksbehandlerIdent)))
                .map(bp -> new SaksbehandlerDto(identDto, bp.navn(), bp.ansattAvdeling()));
        } catch (IntegrasjonException e) {
            return Optional.empty();
        }

    }
    public Optional<BrukerProfil> hentBrukerProfil(Saksbehandler saksbehandler) {
        try {
            return Optional.of(ansattTjeneste.hentBrukerProfil(saksbehandler));
        } catch (IntegrasjonException e) {
            LOG.info("Henting av ansattnavn feilet, fortsetter med empty.", e);
            return Optional.empty();
        }
    }
}
