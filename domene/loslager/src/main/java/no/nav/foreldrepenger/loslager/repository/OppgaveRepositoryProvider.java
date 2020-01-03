package no.nav.foreldrepenger.loslager.repository;

public interface OppgaveRepositoryProvider {

    OppgaveRepository getOppgaveRepository();

    OrganisasjonRepository getOrganisasjonRepository();

    StatistikkRepository getStatisikkRepository();

    AdminRepository getAdminRepository();
}
