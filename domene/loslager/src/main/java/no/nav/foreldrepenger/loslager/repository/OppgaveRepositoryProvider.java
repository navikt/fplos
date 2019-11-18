package no.nav.foreldrepenger.loslager.repository;

import no.nav.fplos.kodeverk.KodeverkRepository;

public interface OppgaveRepositoryProvider {

    KodeverkRepository getKodeverkRepository();

    OppgaveRepository getOppgaveRepository();

    OrganisasjonRepository getOrganisasjonRepository();

    StatistikkRepository getStatisikkRepository();

    AdminRepository getAdminRepository();
}
