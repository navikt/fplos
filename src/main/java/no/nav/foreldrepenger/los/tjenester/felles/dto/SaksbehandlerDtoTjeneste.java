package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjenesteFeil;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.vedtak.exception.IntegrasjonException;

@ApplicationScoped
public class SaksbehandlerDtoTjeneste {

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
        return filtrering.getSaksbehandlere().stream().map(this::saksbehandlerDto).toList();
    }

    public Optional<SaksbehandlerDto> hentSaksbehandlerTilknyttetMinstEnKø(String ident) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(ident)
            .filter(sb -> !oppgaveKøTjeneste.hentAlleOppgaveFiltrering(sb.getSaksbehandlerIdent()).isEmpty())
            .map(this::saksbehandlerDto);
    }

    public SaksbehandlerDto lagKjentOgUkjentSaksbehandler(Saksbehandler saksbehandler) {
        // saksbehandler kan eksistere i basen men være ukjent i azuread
        return saksbehandlerDto(saksbehandler);
    }

    public SaksbehandlerDto saksbehandlerDto(Saksbehandler saksbehandler) {
        return new SaksbehandlerDto(saksbehandler.getSaksbehandlerIdent(), saksbehandler.getNavnEllerUkjent(), saksbehandler.getAnsattVedEnhetEllerUkjent());
    }

    // Denne skal hente Brukerprofil dersom saksbehandler ikke er lagret
    public Optional<SaksbehandlerDto> saksbehandlerDtoForNavIdent(String saksbehandlerIdent) {
        try {
            return organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent).map(this::saksbehandlerDto)
                .or(() -> ansattTjeneste.hentBrukerProfil(saksbehandlerIdent)
                    .map(bp -> new SaksbehandlerDto(saksbehandlerIdent, bp.navn(), bp.ansattAvdeling())));
        } catch (IntegrasjonException e) {
            return Optional.empty();
        }
    }
}
