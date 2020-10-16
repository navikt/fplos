package no.nav.fplos.admin;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.repository.AdminRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingKlient;

@ApplicationScoped
public class AdminTjenesteImpl implements AdminTjeneste {

    private static final String AVSLUTTET_STATUS = "AVSLU";

    private ForeldrepengerBehandlingKlient foreldrepengerBehandlingKlient;
    private AdminRepository adminRepository;
    private OrganisasjonRepository organisasjonRepository;

    @Inject
    public AdminTjenesteImpl(AdminRepository adminRepository,
                             ForeldrepengerBehandlingKlient foreldrepengerBehandlingKlient,
                             OrganisasjonRepository organisasjonRepository) {
        this.adminRepository = adminRepository;
        this.foreldrepengerBehandlingKlient = foreldrepengerBehandlingKlient;
        this.organisasjonRepository = organisasjonRepository;
    }

    AdminTjenesteImpl() {
        // CDI
    }

    @Override
    public void opprettAvdeling(Avdeling nyAvdeling) {
        if (enhetEksisterer(nyAvdeling)) {
            throw new IllegalArgumentException("Avdeling eksisterer");
        }
        organisasjonRepository.lagre(nyAvdeling);
    }

    private boolean enhetEksisterer(Avdeling enhet) {
        return organisasjonRepository.hentAvdelingFraEnhet(enhet.getAvdelingEnhet()).isPresent();
    }

    @Override
    public Oppgave synkroniserOppgave(BehandlingId behandlingId) {
        BehandlingFpsak behandlingDto = foreldrepengerBehandlingKlient.getBehandling(behandlingId);
        if (AVSLUTTET_STATUS.equals(behandlingDto.getStatus())) {
            adminRepository.deaktiverSisteOppgave(behandlingId);
        }
        return adminRepository.hentSisteOppgave(behandlingId);
    }

    @Override
    public Oppgave hentOppgave(BehandlingId behandlingId) {
        return adminRepository.hentSisteOppgave(behandlingId);
    }

    @Override
    public List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId) {
        return adminRepository.hentEventer(behandlingId);
    }

    @Override
    public List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId) {
        return adminRepository.hentAlleOppgaverForBehandling(behandlingId);
    }

    @Override
    public Oppgave deaktiverOppgave(Long oppgaveId) {
        return adminRepository.deaktiverOppgave(oppgaveId);
    }

    @Override
    public Oppgave aktiverOppgave(Long oppgaveId) {
        return adminRepository.aktiverOppgave(oppgaveId);
    }
}
