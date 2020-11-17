package no.nav.fplos.admin;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.domene.typer.Saksnummer;
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
    public List<Oppgave> hentOppgaver(Saksnummer saksnummer) {
        Comparator<Oppgave> sortertAktivOpprettet = Comparator.comparing(Oppgave::getAktiv)
                .thenComparing(o -> Optional.ofNullable(o.getEndretTidspunkt()).orElse(o.getOpprettetTidspunkt())).reversed();
        return adminRepository.hentOppgaver(saksnummer).stream()
                .sorted(sortertAktivOpprettet)
                .collect(Collectors.toList());
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
