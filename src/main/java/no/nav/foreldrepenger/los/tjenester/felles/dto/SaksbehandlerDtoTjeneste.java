package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.vedtak.exception.IntegrasjonException;

@ApplicationScoped
public class SaksbehandlerDtoTjeneste {
    private OrganisasjonRepository organisasjonRepository;
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private AnsattTjeneste ansattTjeneste;

    @Inject
    public SaksbehandlerDtoTjeneste(OrganisasjonRepository organisasjonRepository,
                                    AvdelingslederTjeneste avdelingslederTjeneste,
                                    AnsattTjeneste ansattTjeneste,
                                    OppgaveKøTjeneste oppgaveKøTjeneste) {
        this.organisasjonRepository = organisasjonRepository;
        this.ansattTjeneste = ansattTjeneste;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
    }

    SaksbehandlerDtoTjeneste() {
        //CDI
    }

    public Optional<SaksbehandlerDto> hentSaksbehandlerTilknyttetMinstEnKø(String ident) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(ident)
            .filter(sb -> !oppgaveKøTjeneste.hentAlleOppgaveFiltrering(sb.getSaksbehandlerIdent()).isEmpty())
            .map(SaksbehandlerDtoTjeneste::saksbehandlerDto);
    }

    public List<SaksbehandlerDto> hentAvdelingensSaksbehandlere(String avdelingEnhet) {
        return organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet)
            .map(organisasjonRepository::saksbehandlereForAvdeling)
            .orElse(List.of())
            .stream()
            .map(SaksbehandlerDtoTjeneste::saksbehandlerDto)
            .toList();
    }

    public static SaksbehandlerDto saksbehandlerDto(Saksbehandler saksbehandler) {
        // saksbehandler kan eksistere i basen men være ukjent i azuread
        return new SaksbehandlerDto(saksbehandler.getSaksbehandlerIdent(), saksbehandler.getNavnEllerUkjent(), saksbehandler.getAnsattVedEnhetEllerUkjent());
    }

    // Denne skal hente Brukerprofil dersom saksbehandler ikke er lagret
    public Optional<SaksbehandlerDto> saksbehandlerDtoForNavIdent(String saksbehandlerIdent) {
        try {
            return organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent).map(SaksbehandlerDtoTjeneste::saksbehandlerDto)
                .or(() -> ansattTjeneste.hentBrukerProfil(saksbehandlerIdent)
                    .map(bp -> new SaksbehandlerDto(saksbehandlerIdent, bp.navn(), bp.ansattAvdeling())));
        } catch (IntegrasjonException e) {
            return Optional.empty();
        }
    }
}
