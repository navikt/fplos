package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.organisasjon.ansatt.BrukerProfil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjenesteFeil;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
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
        return filtrering.getSaksbehandlere().stream().map(this::tilSaksbehandlerDto).flatMap(Optional::stream).toList();
    }

    public Optional<SaksbehandlerMedAvdelingerDto> hentSaksbehandlerTilknyttetMinstEnKø(String ident) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(ident)
            .map(Saksbehandler::getSaksbehandlerIdent)
            .filter(sb -> !oppgaveKøTjeneste.hentAlleOppgaveFiltrering(sb).isEmpty())
            .flatMap(this::lagSaksbehandlerMedAvdelingerDto);
    }

    public Optional<SaksbehandlerMedAvdelingerDto> lagSaksbehandlerMedAvdelingerDto(String ident) {
        return Optional.ofNullable(ident).flatMap(this::tilSaksbehandlerDto).flatMap(sb -> tilSaksbehandlerMedAvdelingerDto(ident, sb));
    }

    private Optional<SaksbehandlerMedAvdelingerDto> tilSaksbehandlerMedAvdelingerDto(String ident, SaksbehandlerDto saksbehandlerDto) {
        return Optional.of(ident).map(ansattTjeneste::hentAvdelingerNavnForAnsatt).map(a -> new SaksbehandlerMedAvdelingerDto(saksbehandlerDto, a));
    }

    public SaksbehandlerMedAvdelingerDto lagKjentOgUkjentSaksbehandlerMedAvdelingerDto(Saksbehandler saksbehandler) {
        // saksbehandler kan eksistere i basen men være ukjent i azuread
        var ident = saksbehandler.getSaksbehandlerIdent();
        var saksbehandlerDto = tilSaksbehandlerDto(ident);
        if (saksbehandlerDto.isPresent()) {
            var avdelinger = ansattTjeneste.hentAvdelingerNavnForAnsatt(ident);
            return new SaksbehandlerMedAvdelingerDto(saksbehandlerDto.get(), avdelinger);
        }
        var ukjent = new SaksbehandlerDto(new SaksbehandlerBrukerIdentDto(ident), "Ukjent saksbehandler " + ident, "Ukjent saksbehandler " + ident,
            null, null);
        return new SaksbehandlerMedAvdelingerDto(ukjent, Collections.emptyList());
    }

    public Optional<SaksbehandlerDto> tilSaksbehandlerDto(Saksbehandler saksbehandler) {
        var ident = saksbehandler.getSaksbehandlerIdent();
        return tilSaksbehandlerDto(ident);
    }

    private Optional<SaksbehandlerDto> tilSaksbehandlerDto(String ident) {
        var identDto = new SaksbehandlerBrukerIdentDto(ident);
        return hentBrukerProfil(ident)
            .map(bp -> new SaksbehandlerDto(identDto, bp.navn(), bp.fornavnEtternavn(), bp.epostAdresse(), bp.ansattAvdeling()));
    }

    public Optional<BrukerProfil> hentBrukerProfil(String ident) {
        try {
            return Optional.of(ansattTjeneste.hentBrukerProfil(ident));
        } catch (IntegrasjonException e) {
            LOG.info("Henting av ansattnavn feilet, fortsetter med empty.", e);
            return Optional.empty();
        }
    }
}
