package no.nav.foreldrepenger.los.admin;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.oppgave.Oppgave;

@ApplicationScoped
public class AdminTjeneste {

    private AdminRepository adminRepository;

    @Inject
    public AdminTjeneste(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    AdminTjeneste() {
        // CDI
    }

    public List<Oppgave> hentOppgaver(Saksnummer saksnummer) {
        var sortertAktivOpprettet = Comparator.comparing(Oppgave::getAktiv)
                .thenComparing(o -> Optional.ofNullable(o.getEndretTidspunkt()).orElse(o.getOpprettetTidspunkt())).reversed();
        return adminRepository.hentOppgaver(saksnummer).stream()
                .sorted(sortertAktivOpprettet)
                .collect(Collectors.toList());
    }

    public List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId) {
        return adminRepository.hentEventer(behandlingId);
    }

    public List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId) {
        return adminRepository.hentAlleOppgaverForBehandling(behandlingId);
    }

    public Oppgave deaktiverOppgave(Long oppgaveId) {
        return adminRepository.deaktiverOppgave(oppgaveId);
    }

    public Oppgave aktiverOppgave(Long oppgaveId) {
        return adminRepository.aktiverOppgave(oppgaveId);
    }
}
