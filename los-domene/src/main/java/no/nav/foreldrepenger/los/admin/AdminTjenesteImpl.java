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
public class AdminTjenesteImpl implements AdminTjeneste {

    private AdminRepository adminRepository;

    @Inject
    public AdminTjenesteImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    AdminTjenesteImpl() {
        // CDI
    }

    @Override
    public List<Oppgave> hentOppgaver(Saksnummer saksnummer) {
        var sortertAktivOpprettet = Comparator.comparing(Oppgave::getAktiv)
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
